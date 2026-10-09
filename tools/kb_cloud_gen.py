#!/usr/bin/env python3
"""云研发方向面试题批量生成（DeepSeek），带去重，输出 JSONL。

用法:
  DEEPSEEK_API_KEY=xx python3 kb_cloud_gen.py --target 100 --batch 10 --workers 5

输出可直接喂给 tools/kb_cloud_import.py 入库，再跑 kb_es_sync.py 同步 ES。
"""
import argparse
import json
import os
import random
import re
import threading
import time
import urllib.request

API = "https://api.deepseek.com/chat/completions"
MODEL = "deepseek-chat"

TECHS = {
    "云研发": [
        "云计算与云原生基础",
        "容器与镜像",
        "Kubernetes 核心原理",
        "Kubernetes 运维与故障排查",
        "云网络与负载均衡",
        "云存储与数据可靠性",
        "可观测性（监控/日志/链路）",
        "CI/CD 与发布策略",
        "微服务与中间件治理",
        "分布式系统基础",
        "云安全与多租户",
        "平台研发与成本治理",
    ],
}

PROMPT = """你是资深云研发 / 云原生方向面试官兼技术作者。请围绕「{tech}」方向出 {n} 道**互不重复**的面试题。
必须覆盖这些子主题（尽量分散）：{subtopics}

要求：
1. title 用面试官提问口吻的一句话（不要"面试官："前缀），要具体、能区分候选人水平；answer 用 Markdown 写，600-1500 字，结构固定：
   `## 回答要点`（原理与关键结论，涉及对比 / 参数 / 步骤用表格或有序列表）
   `## 实战与踩坑`（真实生产怎么做：具体命令、参数、组件行为、排障步骤）
   `## 加分点`（面试官想听到的深度、易忽略的边界或新版本变化）
   `## 常见追问`（2-3 个延伸问题 + 一句话答案）
2. 内容必须贴合真实云平台研发场景：K8s 控制面 / 调度 / 网络 / 存储、VPC 与四层七层负载、对象存储、可观测性、GitOps 与发布、多租户与成本治理等，尽量出现具体组件名与数值（kubelet、etcd、CNI、CSI、Prometheus、Operator 等）。
3. 不要出现下面已经出过的题目：{existed}
4. 难度分布：easy/medium/hard 大致 3:5:2。

只输出 JSON：{{"questions":[{{"title":"问题","answer":"Markdown 格式的完整解答","difficulty":"medium","subtopic":"子主题"}}]}}"""

lock = threading.Lock()
seen = set()
usage_total = {"prompt": 0, "completion": 0}


def norm(title):
    """标题去重键：去掉空白与符号，忽略大小写。"""
    return re.sub(r"[\s\W_]+", "", title.lower())


def parse_questions(content):
    """容错解析：模型偶尔返回带 markdown 包裹 / 数组 / 字符串的 JSON。"""
    if not isinstance(content, str):
        content = json.dumps(content, ensure_ascii=False)
    text = content.strip()
    if text.startswith("```"):
        text = re.sub(r"^```[a-zA-Z]*\n?|\n?```$", "", text).strip()
    try:
        data = json.loads(text)
    except Exception:
        match = re.search(r"\{[\s\S]*\}", text)
        if not match:
            return []
        try:
            data = json.loads(match.group(0))
        except Exception:
            return []
    if isinstance(data, dict):
        items = data.get("questions", [])
    elif isinstance(data, list):
        items = data
    else:
        return []
    return [item for item in items if isinstance(item, dict)]


def call_api(key, tech, subtopics, n, existed):
    body = {
        "model": MODEL,
        "messages": [{"role": "user", "content": PROMPT.format(
            tech=tech, n=n, subtopics="、".join(subtopics), existed="；".join(existed[:40]) or "无")}],
        "temperature": 1.0,
        "max_tokens": 8000,
        "response_format": {"type": "json_object"},
    }
    req = urllib.request.Request(API, data=json.dumps(body).encode(), method="POST")
    req.add_header("Content-Type", "application/json")
    req.add_header("Authorization", "Bearer " + key)
    with urllib.request.urlopen(req, timeout=300) as resp:
        data = json.loads(resp.read().decode())
    usage = data.get("usage") or {}
    with lock:
        usage_total["prompt"] += usage.get("prompt_tokens", 0)
        usage_total["completion"] += usage.get("completion_tokens", 0)
    return parse_questions(data["choices"][0]["message"]["content"])


def gen_tech(key, tech, target, batch, workers, out):
    subs = TECHS[tech]
    done = 0
    titles = []
    if os.path.exists(out):
        for line in open(out, encoding="utf-8"):
            line = line.strip()
            if not line:
                continue
            try:
                item = json.loads(line)
            except Exception:
                continue
            if item.get("tech") != tech:
                continue
            seen.add(norm(item["title"]))
            titles.append(item["title"])
            done += 1
    if done >= target:
        print(" [%s] 已有 %d 题，跳过" % (tech, done), flush=True)
        return
    start = time.time()
    with open(out, "a", encoding="utf-8") as out_file:
        while done < target:
            tasks = []
            for _ in range(workers):
                tasks.append(random.sample(subs, min(len(subs), random.randint(4, 7))))
            results = [None] * len(tasks)

            def run(idx, subtopics):
                try:
                    with lock:
                        existed = titles[-60:]
                    results[idx] = call_api(key, tech, subtopics, batch, existed)
                except Exception as exc:
                    print("  ! 请求失败: %s" % str(exc)[:200], flush=True)
                    results[idx] = []

            threads = [threading.Thread(target=run, args=(i, t)) for i, t in enumerate(tasks)]
            for t in threads:
                t.start()
            for t in threads:
                t.join()

            added = 0
            with lock:
                for batch_items in results:
                    for q in batch_items or []:
                        title = str(q.get("title") or "").strip()
                        answer = str(q.get("answer") or "").strip()
                        if len(title) < 6 or len(answer) < 60:
                            continue
                        k = norm(title)
                        if k in seen:
                            continue
                        seen.add(k)
                        titles.append(title)
                        out_file.write(json.dumps({
                            "tech": tech,
                            "subtopic": q.get("subtopic") or "",
                            "title": title,
                            "content": answer,
                            "difficulty": (q.get("difficulty") or "medium").lower(),
                            "tags": tech,
                        }, ensure_ascii=False) + "\n")
                        added += 1
                        done += 1
                out_file.flush()
            print(" [%s] +%d, 累计 %d/%d, 用时 %.0fs, tokens(出/入)=%d/%d" % (
                tech, added, done, target, time.time() - start,
                usage_total["completion"], usage_total["prompt"]), flush=True)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--tech", default="云研发")
    parser.add_argument("--target", type=int, default=100)
    parser.add_argument("--batch", type=int, default=10)
    parser.add_argument("--workers", type=int, default=5)
    parser.add_argument("--out", default="/home/ubuntu/kbgen/cloud_questions.jsonl")
    args = parser.parse_args()

    key = os.environ["DEEPSEEK_API_KEY"]
    os.makedirs(os.path.dirname(args.out), exist_ok=True)
    tech = args.tech
    print("开始生成 %s，目标 %d 题 -> %s" % (tech, args.target, args.out), flush=True)
    gen_tech(key, tech, args.target, args.batch, args.workers, args.out)
    total = sum(1 for _ in open(args.out, encoding="utf-8"))
    print("完成：%s 共 %d 题，tokens(出/入)=%d/%d" % (
        tech, total, usage_total["completion"], usage_total["prompt"]), flush=True)


if __name__ == "__main__":
    main()

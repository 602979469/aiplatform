#!/usr/bin/env python3
"""把生成的云研发题目做一次规范化：子主题归一到固定清单（模型会写出空格/括号变体甚至空值）。

用法：python3 kb_cloud_normalize.py [输入.jsonl] [输出.jsonl]
"""
import json
import sys
import collections

SRC = sys.argv[1] if len(sys.argv) > 1 else "/home/ubuntu/kbgen/cloud_questions.jsonl"
DST = sys.argv[2] if len(sys.argv) > 2 else "/home/ubuntu/kbgen/cloud_questions_norm.jsonl"

CANON = [
    "云计算与云原生基础",
    "容器与镜像",
    "Kubernetes 核心原理",
    "Kubernetes 运维与故障排查",
    "云网络与负载均衡",
    "云存储与数据可靠性",
    "可观测性",
    "CI/CD 与发布策略",
    "微服务与中间件治理",
    "分布式系统基础",
    "云安全与多租户",
    "平台研发与成本治理",
]

# 关键词兜底：模型自造的子主题名 → 规范名
RULES = [
    ("kubernetes", "Kubernetes 核心原理"),
    ("k8s", "Kubernetes 核心原理"),
    ("运维", "Kubernetes 运维与故障排查"),
    ("排查", "Kubernetes 运维与故障排查"),
    ("容器", "容器与镜像"),
    ("镜像", "容器与镜像"),
    ("网络", "云网络与负载均衡"),
    ("负载", "云网络与负载均衡"),
    ("存储", "云存储与数据可靠性"),
    ("可观测", "可观测性"),
    ("监控", "可观测性"),
    ("日志", "可观测性"),
    ("链路", "可观测性"),
    ("ci/cd", "CI/CD 与发布策略"),
    ("gitops", "CI/CD 与发布策略"),
    ("发布", "CI/CD 与发布策略"),
    ("微服务", "微服务与中间件治理"),
    ("治理", "微服务与中间件治理"),
    ("分布式", "分布式系统基础"),
    ("安全", "云安全与多租户"),
    ("多租户", "云安全与多租户"),
    ("成本", "平台研发与成本治理"),
    ("平台研发", "平台研发与成本治理"),
]


def canon_name(raw):
    """子主题归一：先按去空格匹配规范名，再按关键词兜底。"""
    text = (raw or "").strip()
    key = text.replace(" ", "").replace("（", "(").replace("）", ")").lower()
    for name in CANON:
        if name.replace(" ", "").lower() == key:
            return name
    for word, name in RULES:
        if word in key:
            return name
    return "云计算与云原生基础"


def main():
    rows = [json.loads(line) for line in open(SRC, encoding="utf-8") if line.strip()]
    out = []
    for row in rows:
        row["subtopic"] = canon_name(row.get("subtopic"))
        out.append(row)
    with open(DST, "w", encoding="utf-8") as handle:
        for row in out:
            handle.write(json.dumps(row, ensure_ascii=False) + "\n")
    print("写出 %d 条 -> %s" % (len(out), DST))
    print("子主题分布:", dict(collections.Counter(r["subtopic"] for r in out)))
    print("难度分布:", dict(collections.Counter(r["difficulty"] for r in out)))
    with_sections = sum(1 for r in out if "## 回答要点" in r["content"])
    print("含「## 回答要点」小节: %d/%d" % (with_sections, len(out)))


if __name__ == "__main__":
    main()

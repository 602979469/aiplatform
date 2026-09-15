#!/usr/bin/env python3
"""kb_question → Elasticsearch(java-kb) 全量重建索引（幂等，_id = MySQL 主键）。

用途：题库有增量导入 / 手工 SQL 改动后，ES 索引会落后，跑本脚本对齐。
在 k8s-master 上执行（需要 kubectl 能访问 tsk 与 efk 命名空间）。
"""
import base64
import json
import subprocess
import sys
import urllib.request

INDEX = "java-kb"
BATCH = 500
NAMESPACE_DB = "tsk"
NAMESPACE_ES = "efk"
MYSQL_POD = "mysql-0"
MYSQL_USER = "root"
MYSQL_PASSWORD = "123456"
MYSQL_DB = "aiplatform"

# MySQL 批量导出会转义引号/换行，直接输出 JSON 会解析失败；这里用 base64 包裹保证一行一文档
SQL = (
    "select replace(to_base64(cast(json_object("
    "'id',id,'question_type',question_type,'category',category,'subtopic',subtopic,"
    "'title',title,'content',content,'options',options,'answer',answer,"
    "'difficulty',difficulty,'tags',tags) as char)), char(10), '') from " + MYSQL_DB + ".kb_question;"
)


def sh(cmd):
    """执行命令并返回 stdout（失败直接退出）。"""
    done = subprocess.run(cmd, capture_output=True, text=True)
    if done.returncode != 0:
        sys.exit("命令失败: %s\n%s" % (" ".join(cmd), done.stderr[:500]))
    return done.stdout


es_ip = sh(["kubectl", "-n", NAMESPACE_ES, "get", "svc", "elasticsearch",
            "-o", "jsonpath={.spec.clusterIP}"]).strip()
es_pwd = base64.b64decode(sh(["kubectl", "get", "secret", "efk-secret", "-n", NAMESPACE_ES,
                              "-o", "jsonpath={.data.elastic-password}"]).strip()).decode()
auth = base64.b64encode(("elastic:" + es_pwd).encode()).decode()
es = "http://%s:9200" % es_ip


def call(method, path, body=None, ctype="application/json"):
    """调用 ES REST 接口。"""
    if isinstance(body, str):
        data = body.encode()
    elif body is None:
        data = None
    else:
        data = json.dumps(body).encode()
    req = urllib.request.Request(es + path, data=data, method=method)
    req.add_header("Authorization", "Basic " + auth)
    req.add_header("Content-Type", ctype)
    return urllib.request.urlopen(req, timeout=300).read().decode()


raw = sh(["kubectl", "exec", "-i", "-n", NAMESPACE_DB, MYSQL_POD, "-c", "mysql", "--",
          "mysql", "-N", "-B", "--default-character-set=utf8mb4",
          "-u" + MYSQL_USER, "-p" + MYSQL_PASSWORD, MYSQL_DB, "-e", SQL])
rows = [json.loads(base64.b64decode(line).decode("utf-8")) for line in raw.splitlines() if line.strip()]
print("MySQL 读取: %d 条" % len(rows))

sent = 0
failed = 0
for start in range(0, len(rows), BATCH):
    chunk = rows[start:start + BATCH]
    lines = []
    for r in chunk:
        lines.append(json.dumps({"index": {"_index": INDEX, "_id": str(r["id"])}}))
        content = r.get("content") or ""
        lines.append(json.dumps({
            "id": r["id"],
            "doc_type": r["question_type"],
            "question_type": r["question_type"],
            "category": r["category"],
            "tech": r["category"],
            "subtopic": r["subtopic"],
            "title": r["title"],
            "summary": (r["title"] + " " + content)[:200],
            "content": content,
            # 注意：现有 index mapping 里 options 是 text（老数据存 JSON 字符串），保持一致，不能写成对象数组
            "options": r.get("options") if isinstance(r.get("options"), str)
            else json.dumps(r.get("options"), ensure_ascii=False),
            "answer": r.get("answer"),
            "tags": [x for x in (r.get("tags") or "").split(",") if x],
            "difficulty": r.get("difficulty"),
        }, ensure_ascii=False))
    result = json.loads(call("POST", "/_bulk", "\n".join(lines) + "\n", "application/x-ndjson"))
    if result.get("errors"):
        failed += sum(1 for item in result.get("items", []) if item.get("index", {}).get("error"))
    sent += len(chunk)
    print("  已同步 %d/%d" % (sent, len(rows)))

call("POST", "/%s/_refresh" % INDEX)
print("ES 总数: " + call("GET", "/%s/_count" % INDEX).strip())
print("失败条目: %d" % failed)

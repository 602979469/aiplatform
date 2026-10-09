#!/usr/bin/env python3
"""把 MySQL 里尚未进 ES 的题目补进 ES（按 id 对齐，分批 + 超时保护）。"""
import base64
import json
import subprocess
import urllib.request

ES = "http://10.102.25.172:9200"
INDEX = "java-kb"
BATCH = 200


def sh(cmd, **kw):
    return subprocess.run(cmd, capture_output=True, text=True, **kw)


pw = base64.b64decode(sh(["kubectl", "get", "secret", "mysql-secret", "-n", "tsk", "-o",
                          "jsonpath={.data.root-password}"]).stdout).decode()
espw = base64.b64decode(sh(["kubectl", "get", "secret", "efk-secret", "-n", "efk", "-o",
                            "jsonpath={.data.elastic-password}"]).stdout).decode()
auth = base64.b64encode(("elastic:" + espw).encode()).decode()


def b64(value):
    """解码 MySQL 侧 TO_BASE64 出来的文本字段。"""
    return base64.b64decode(value).decode("utf-8") if value else ""


def es(method, path, body=None, ctype="application/json", timeout=180):
    data = body.encode() if isinstance(body, str) else (json.dumps(body).encode() if body is not None else None)
    req = urllib.request.Request(ES + path, data=data, method=method)
    req.add_header("Authorization", "Basic " + auth)
    req.add_header("Content-Type", ctype)
    with urllib.request.urlopen(req, timeout=timeout) as r:
        return r.read().decode()


def mysql_json(query):
    """用 JSON_OBJECT 输出，避免正文换行破坏行分隔。"""
    out = sh(["kubectl", "exec", "-n", "tsk", "mysql-0", "-c", "mysql", "--", "mysql", "-N",
              "--default-character-set=utf8mb4", "-uroot", "-p" + pw, "-e", query]).stdout
    rows = []
    for line in out.split("\n"):
        line = line.strip()
        if not line.startswith("{"):
            continue
        try:
            rows.append(json.loads(line))
        except Exception:
            continue
    return rows


es_count = json.loads(es("GET", "/%s/_count" % INDEX))["count"]
# 文本字段一律 base64 传输：mysql 批处理客户端会把 JSON 里的 \" 再转义一次，
# 导致正文含双引号（代码片段）的题目 JSON 解析失败被静默跳过，ES 里长期缺数据。
rows = mysql_json("SELECT JSON_OBJECT('id',id,'question_type',question_type,"
                  "'category',REPLACE(TO_BASE64(category),CHAR(10),''),"
                  "'subtopic',REPLACE(TO_BASE64(subtopic),CHAR(10),''),"
                  "'title',REPLACE(TO_BASE64(title),CHAR(10),''),"
                  "'difficulty',difficulty,"
                  "'tags',REPLACE(TO_BASE64(IFNULL(tags,'')),CHAR(10),''),"
                  "'content',REPLACE(TO_BASE64(IFNULL(content,'')),CHAR(10),''),"
                  "'options',REPLACE(TO_BASE64(IFNULL(options,'')),CHAR(10),'')) "
                  "FROM aiplatform.kb_question ORDER BY id;")
print("MySQL 共 %d 题，ES 已有 %d 条" % (len(rows), es_count))

todo = rows
print("本次补写 %d 条（按 id 覆盖写入，幂等）" % len(todo))

ok = 0
for i in range(0, len(todo), BATCH):
    chunk = todo[i:i + BATCH]
    lines = []
    for r in chunk:
        rid = r["id"]; qtype = r["question_type"]; category = b64(r["category"])
        subtopic = b64(r.get("subtopic")); title = b64(r["title"]); difficulty = r.get("difficulty")
        tags = b64(r.get("tags")); content = b64(r.get("content")); options = b64(r.get("options"))
        lines.append(json.dumps({"index": {"_index": INDEX, "_id": str(rid)}}))
        lines.append(json.dumps({
            "id": int(rid), "doc_type": qtype, "question_type": qtype, "category": category,
            "tech": category, "subtopic": subtopic, "title": title,
            "summary": " ".join(content.split())[:200], "content": content,
            "tags": [x for x in (tags or "").split(",") if x], "difficulty": difficulty,
            "options": options,
        }, ensure_ascii=False))
    res = json.loads(es("POST", "/_bulk", "\n".join(lines) + "\n", "application/x-ndjson"))
    if res.get("errors"):
        print("  第 %d 批有错误" % (i // BATCH + 1))
    ok += len(chunk)
    print("  已写 %d/%d" % (ok, len(todo)), flush=True)

es("POST", "/%s/_refresh" % INDEX)
print("ES 总数:", es("GET", "/%s/_count" % INDEX))
print("按题型:", es("GET", "/java-kb/_search?size=0",
                    {"aggs": {"t": {"terms": {"field": "question_type"}}}}) if False else
      [b for b in json.loads(es("GET", "/java-kb/_search", {"size": 0, "aggs": {"t": {"terms": {"field": "question_type", "size": 5}}}}))["aggregations"]["t"]["buckets"]])

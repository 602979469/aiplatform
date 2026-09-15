#!/usr/bin/env python3
"""选择题/判断题/多选题 JSONL -> kb_question 增量导入（按标题去重，可重跑）。

JSONL 每行一个对象：
  {"question_type":"单选","category":"游戏","subtopic":"王者荣耀","title":"...",
   "options":[{"key":"A","text":"..."}],"answer":"A","content":"Markdown 解析",
   "difficulty":"easy","tags":"游戏,王者荣耀"}

在 k8s-master 上执行（需要 kubectl 能访问 tsk 命名空间）。
导入完成后需执行 kb_es_rebuild.py 把 MySQL 同步到 ES。

用法：
  python3 kb_choice_import.py /home/ubuntu/kbgen/hok.jsonl
  python3 kb_choice_import.py /home/ubuntu/kbgen/hok.jsonl --limit 5
"""
import base64
import hashlib
import json
import os
import subprocess
import sys

NAMESPACE = "tsk"
MYSQL_POD = "mysql-0"
MYSQL_DB = "aiplatform"
DEFAULT_SOURCE = "ai-hok"
ROWS_PER_FILE = 300
ROWS_PER_STATEMENT = 30
WORK_DIR = "/tmp/kb_choice_load"
POD_SQL_PATH = "/tmp/kb_choice_load.sql"


def sh(cmd, **kwargs):
    """执行命令并返回 stdout，非 0 退出码直接终止。"""
    done = subprocess.run(cmd, capture_output=True, text=True, **kwargs)
    if done.returncode != 0:
        sys.exit("命令失败: %s\n%s" % (" ".join(cmd[:8]), (done.stderr or "")[:500]))
    return done.stdout


def shell_quote(text):
    """单引号包裹，供容器内 sh -c 使用。"""
    return "'" + str(text).replace("'", "'\\''") + "'"


def mysql_exec(sql=None, stdin_path=None):
    """在 mysql Pod 里执行 SQL：查询用 -e，灌数据用先 cp 再容器内重定向。"""
    base = ["kubectl", "exec", "-n", NAMESPACE, MYSQL_POD, "-c", "mysql", "--", "sh", "-c"]
    client = "mysql -N -B --default-character-set=utf8mb4 -uroot -p%s %s" % (mysql_password, MYSQL_DB)
    if sql is not None:
        cmd = base + ["%s -e %s" % (client, shell_quote(sql))]
    else:
        done = subprocess.run(["kubectl", "cp", stdin_path,
                               "%s/%s:%s" % (NAMESPACE, MYSQL_POD, POD_SQL_PATH), "-c", "mysql"],
                              stdin=subprocess.DEVNULL, capture_output=True, text=True)
        if done.returncode != 0:
            sys.exit("SQL 文件拷贝失败: %s" % (done.stderr or "")[:500])
        cmd = base + ["%s < %s" % (client, POD_SQL_PATH)]
    done = subprocess.run(cmd, stdin=subprocess.DEVNULL, capture_output=True, text=True)
    if done.returncode != 0 or "ERROR " in (done.stderr or ""):
        sys.exit("SQL 执行失败: %s" % ((done.stderr or "") + (done.stdout or ""))[:500])
    return done.stdout


def esc(value):
    """MySQL 单引号字符串转义。"""
    return str(value).replace("\\", "\\\\").replace("'", "''")


def clean(value):
    """去掉 NUL 等控制字符（库里有些内容带 JSON \\u0000 转义，MySQL 批量模式会中断）。"""
    return "".join(ch for ch in str(value) if ch in "\n\r\t" or ord(ch) >= 0x20)


def title_key(title):
    """标题去重键（与落库截断长度一致）。"""
    return hashlib.md5(title.strip()[:250].encode("utf-8")).hexdigest()


mysql_password = base64.b64decode(sh([
    "kubectl", "get", "secret", "mysql-secret", "-n", NAMESPACE, "-o",
    "jsonpath={.data.root-password}"]).strip()).decode()

source_path = DEFAULT_SOURCE
args = [a for a in sys.argv[1:] if not a.startswith("--")]
limit = 0
if "--limit" in sys.argv:
    limit = int(sys.argv[sys.argv.index("--limit") + 1])
if not args:
    sys.exit("用法: python3 kb_choice_import.py <questions.jsonl> [--limit N]")
jsonl_path = args[0]

rows = []
seen = set()
for line in open(jsonl_path, encoding="utf-8"):
    line = line.strip()
    if not line:
        continue
    raw = json.loads(line)
    title = clean(raw.get("title") or "").strip()
    content = clean(raw.get("content") or "").strip()
    options = raw.get("options") or []
    if len(title) < 6 or not options:
        continue
    key = title_key(title)
    if key in seen:
        continue
    seen.add(key)
    rows.append({
        "key": key,
        "question_type": str(raw.get("question_type") or "单选").strip(),
        "category": clean(raw.get("category") or "").strip(),
        "subtopic": clean(raw.get("subtopic") or "").strip(),
        "title": title[:250],
        "options": json.dumps(options, ensure_ascii=False),
        "answer": clean(raw.get("answer") or "").strip(),
        "content": content,
        "difficulty": clean(raw.get("difficulty") or "medium").strip() or "medium",
        "tags": clean(raw.get("tags") or "").strip(),
    })
print("文件有效题目: %d 道" % len(rows))

existing = set()
for line in mysql_exec("SELECT MD5(title) FROM kb_question;").splitlines():
    line = line.strip()
    if line:
        existing.add(line)
print("库中已有题目: %d 道" % len(existing))

pending = [r for r in rows if r["key"] not in existing]
if limit:
    pending = pending[:limit]
print("本次待导入: %d 道" % len(pending))
if not pending:
    sys.exit("没有需要导入的数据，结束。")

start_id = int(mysql_exec("SELECT IFNULL(MAX(id),0) FROM kb_question;").strip()) + 1
print("起始 id = %d" % start_id)

os.makedirs(WORK_DIR, exist_ok=True)
inserted = 0
for start in range(0, len(pending), ROWS_PER_FILE):
    chunk = pending[start:start + ROWS_PER_FILE]
    statements = []
    for offset in range(0, len(chunk), ROWS_PER_STATEMENT):
        part = chunk[offset:offset + ROWS_PER_STATEMENT]
        values = ",".join(
            "(%d,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')" % (
                start_id + start + offset + index,
                esc(r["question_type"]), esc(r["category"]), esc(r["subtopic"]), esc(r["title"]),
                esc(r["options"]), esc(r["answer"]), esc(r["content"]),
                esc(r["difficulty"]), esc(r["tags"]), source_path)
            for index, r in enumerate(part))
        statements.append("INSERT INTO kb_question (id, question_type, category, subtopic, title, "
                          "options, answer, content, difficulty, tags, source_path) VALUES " + values + ";")
    sql_path = os.path.join(WORK_DIR, "load_%05d.sql" % start)
    with open(sql_path, "w", encoding="utf-8") as handle:
        handle.write("\n".join(statements))
    mysql_exec(None, stdin_path=sql_path)
    os.remove(sql_path)
    inserted += len(chunk)
    print("  已写入 %d/%d" % (inserted, len(pending)), flush=True)

print("MySQL 导入完成，新增 %d 道" % inserted)
print(mysql_exec("SELECT CONCAT('总数=', COUNT(*)) FROM kb_question;").strip())
print(mysql_exec("SELECT CONCAT(question_type, '=', COUNT(*)) FROM kb_question WHERE category='游戏' "
                 "GROUP BY question_type;").strip())

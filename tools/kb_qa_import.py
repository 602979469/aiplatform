#!/usr/bin/env python3
"""qa_md_backup.jsonl -> kb_question（题型=解答）增量导入。

在 k8s-master 上执行（需要 kubectl 能访问 tsk 命名空间）。
特性：
  1. 按标题（截断 250 字符，与建表长度一致）MD5 去重，已在库里的不再重复插入；
  2. id 接在现有 MAX(id) 之后，保证与 ES _id 一一对应；
  3. 分批写 SQL 文件后整体灌库，失败即退出并打印 stderr；
  4. 幂等：中途失败直接重跑即可，已入库的会自动跳过。

用法：
  python3 kb_qa_import.py            # 全量增量导入
  python3 kb_qa_import.py --limit 5  # 只导 5 条（演练/自检）
导入完成后还需执行 kb_es_rebuild.py 把 MySQL 全量同步到 ES。
"""
import base64
import hashlib
import json
import os
import subprocess
import sys

SRC = "/home/ubuntu/kbgen/qa_md_backup.jsonl"
NAMESPACE = "tsk"
MYSQL_POD = "mysql-0"
MYSQL_DB = "aiplatform"
QUESTION_TYPE = "解答"
SOURCE_PATH = "ai-qa-md"
ROWS_PER_FILE = 300      # 每个 SQL 文件塞多少行
ROWS_PER_STATEMENT = 30  # 每条 INSERT 塞多少行
WORK_DIR = "/tmp/kb_qa_load"
POD_SQL_PATH = "/tmp/kb_load.sql"


def sh(cmd, **kwargs):
    """执行命令并返回 stdout，非 0 退出码直接终止。"""
    done = subprocess.run(cmd, capture_output=True, text=True, **kwargs)
    if done.returncode != 0:
        sys.exit("命令失败: %s\n%s" % (" ".join(cmd[:8]), (done.stderr or "")[:500]))
    return done.stdout


def mysql_exec(sql=None, stdin_path=None):
    """在 mysql Pod 里执行 SQL。

    注意：不要用 `kubectl exec -i` 从本机灌大 SQL —— 这个环境里会话关闭会挂住，
    正确做法是 kubectl cp 进 Pod 后在容器内重定向执行（无 stdin 传输）。
    """
    base = ["kubectl", "exec", "-n", NAMESPACE, MYSQL_POD, "-c", "mysql", "--",
            "sh", "-c"]
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


def shell_quote(text):
    """单引号包裹，供容器内 sh -c 使用。"""
    return "'" + str(text).replace("'", "'\\''") + "'"


def esc(value):
    """MySQL 单引号字符串转义。"""
    return str(value).replace("\\", "\\\\").replace("'", "''")


def clean(value):
    """去掉 NUL 等控制字符（备份里有 JSON \\u0000 转义，落库前必须清掉）。

    MySQL 客户端批量模式遇到真正的 NUL 字节会直接报
    "ASCII '\\0' appeared in statement" 并中断整批导入。
    """
    text = str(value)
    return "".join(ch for ch in text if ch in "\n\r\t" or ord(ch) >= 0x20)


def title_key(title):
    """标题去重键：与落库时的截断长度保持一致。"""
    return hashlib.md5(title.strip()[:250].encode("utf-8")).hexdigest()


mysql_password = base64.b64decode(sh([
    "kubectl", "get", "secret", "mysql-secret", "-n", NAMESPACE, "-o",
    "jsonpath={.data.root-password}"]).strip()).decode()

limit = 0
if len(sys.argv) > 2 and sys.argv[1] == "--limit":
    limit = int(sys.argv[2])

rows = []
seen = set()
for line in open(SRC, encoding="utf-8"):
    line = line.strip()
    if not line:
        continue
    try:
        raw = json.loads(line)
    except Exception:
        continue
    title = str(raw.get("title") or "").strip()
    content = clean(raw.get("content") or "").strip()
    title = clean(title)
    if len(title) < 6 or len(content) < 100:
        continue
    key = title_key(title)
    if key in seen:
        continue
    seen.add(key)
    rows.append({
        "key": key,
        "category": clean(raw.get("tech") or "").strip(),
        "subtopic": clean(raw.get("subtopic") or "").strip(),
        "title": title[:250],
        "content": content,
        "difficulty": clean(raw.get("difficulty") or "medium").strip() or "medium",
        "tags": clean(raw.get("tags") or raw.get("tech") or "").strip(),
    })
print("备份文件有效解答题: %d 道（已按标题去重）" % len(rows))

existing = set()
for line in mysql_exec("SELECT MD5(title) FROM kb_question WHERE question_type='%s';" % QUESTION_TYPE).splitlines():
    line = line.strip()
    if line:
        existing.add(line)
print("库中已有解答题: %d 道" % len(existing))

pending = [r for r in rows if r["key"] not in existing]
if limit:
    pending = pending[:limit]
print("本次待导入: %d 道" % len(pending))
if not pending:
    sys.exit("没有需要导入的数据，结束。")

current = mysql_exec("SELECT IFNULL(MAX(id),0) FROM kb_question;").strip()
start_id = int(current) + 1
print("起始 id = %d" % start_id)

os.makedirs(WORK_DIR, exist_ok=True)
inserted = 0
for start in range(0, len(pending), ROWS_PER_FILE):
    chunk = pending[start:start + ROWS_PER_FILE]
    statements = []
    for offset in range(0, len(chunk), ROWS_PER_STATEMENT):
        part = chunk[offset:offset + ROWS_PER_STATEMENT]
        values = ",".join(
            "(%d,'%s','%s','%s','%s','%s','%s','%s','%s')" % (
                start_id + start + offset + index,
                QUESTION_TYPE, esc(r["category"]), esc(r["subtopic"]), esc(r["title"]),
                esc(r["content"]), esc(r["difficulty"]), esc(r["tags"]), SOURCE_PATH)
            for index, r in enumerate(part))
        statements.append("INSERT INTO kb_question (id, question_type, category, subtopic, title, "
                          "content, difficulty, tags, source_path) VALUES " + values + ";")
    sql_path = os.path.join(WORK_DIR, "load_%05d.sql" % start)
    with open(sql_path, "w", encoding="utf-8") as handle:
        handle.write("\n".join(statements))
    mysql_exec(None, stdin_path=sql_path)
    os.remove(sql_path)
    inserted += len(chunk)
    print("  已写入 %d/%d" % (inserted, len(pending)), flush=True)

print("MySQL 导入完成，新增 %d 道" % inserted)
print(mysql_exec("SELECT CONCAT('总数=', COUNT(*), ' 解答=', SUM(question_type='%s')) FROM kb_question;"
                 % QUESTION_TYPE).strip())

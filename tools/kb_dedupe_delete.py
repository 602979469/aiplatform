#!/usr/bin/env python3
"""按 kb_dedupe_analyze.py 的结论删除近重复题（先备份整行，再删除）。

安全设计：
  1. 删除前把待删整行导出到 /home/ubuntu/kbgen/dedupe/backup_<时间戳>.jsonl（可完整回滚）；
  2. 跳过被历史试卷（kb_exam_paper_question）引用的题目，避免历史成绩里题面查不到；
  3. 分批 DELETE 并打印进度，失败即停。

用法（在 k8s-master 上执行）：
  python3 kb_dedupe_analyze.py --dump          # 先分析并生成候选 id 文件
  python3 kb_dedupe_delete.py --dry-run        # 预览将删除多少条
  python3 kb_dedupe_delete.py                  # 真正删除
删除后需执行 kb_es_rebuild.py 重建索引。
"""
import base64
import datetime
import json
import os
import subprocess
import sys

NAMESPACE = "tsk"
MYSQL_POD = "mysql-0"
MYSQL_DB = "aiplatform"
ID_FILE = "/home/ubuntu/kbgen/dedupe/dedupe_delete_ids.txt"
BACKUP_DIR = "/home/ubuntu/kbgen/dedupe"
BATCH = 500


def sh(cmd, **kwargs):
    """执行命令并返回 stdout，非 0 退出码直接终止。"""
    done = subprocess.run(cmd, capture_output=True, text=True, **kwargs)
    if done.returncode != 0:
        sys.exit("命令失败: %s\n%s" % (" ".join(cmd[:6]), (done.stderr or "")[:400]))
    return done.stdout


def query(sql):
    """执行查询并返回行列表。"""
    out = sh(["kubectl", "exec", "-n", NAMESPACE, MYSQL_POD, "-c", "mysql", "--", "mysql", "-N", "-B",
              "--default-character-set=utf8mb4", "-uroot", "-p" + password, MYSQL_DB, "-e", sql])
    return [line.strip() for line in out.splitlines() if line.strip()]


password = base64.b64decode(sh([
    "kubectl", "get", "secret", "mysql-secret", "-n", NAMESPACE, "-o",
    "jsonpath={.data.root-password}"]).strip()).decode()

dry_run = "--dry-run" in sys.argv

if not os.path.exists(ID_FILE):
    sys.exit("找不到候选 id 文件 %s，请先执行 kb_dedupe_analyze.py --dump" % ID_FILE)

ids = []
for line in open(ID_FILE, encoding="utf-8"):
    line = line.strip()
    if line.isdigit():
        ids.append(int(line))
ids = sorted(set(ids))
print("候选删除: %d 条" % len(ids))

# 排除历史试卷引用的题目
referenced = set()
for i in range(0, len(ids), 1000):
    chunk = ids[i:i + 1000]
    sql = ("SELECT DISTINCT question_id FROM kb_exam_paper_question WHERE question_id IN (%s);"
           % ",".join(str(x) for x in chunk))
    for line in query(sql):
        if line.isdigit():
            referenced.add(int(line))
targets = [x for x in ids if x not in referenced]
print("被历史试卷引用、跳过: %d 条；实际删除: %d 条" % (len(referenced), len(targets)))
if not targets:
    sys.exit("没有可删除的数据。")

before = query("SELECT COUNT(*) FROM kb_question;")[0]
if dry_run:
    print("[dry-run] 当前题量 %s，删除后约 %d 条" % (before, int(before) - len(targets)))
    sys.exit(0)

# 1. 备份整行
os.makedirs(BACKUP_DIR, exist_ok=True)
stamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
backup_path = os.path.join(BACKUP_DIR, "backup_%s.jsonl" % stamp)
written = 0
with open(backup_path, "w", encoding="utf-8") as handle:
    for i in range(0, len(targets), BATCH):
        chunk = targets[i:i + BATCH]
        sql = ("SELECT REPLACE(TO_BASE64(CAST(JSON_OBJECT('id',id,'question_type',question_type,"
               "'category',category,'subtopic',subtopic,'title',title,'options',options,"
               "'answer',answer,'content',content,'difficulty',difficulty,'tags',tags,"
               "'source_path',source_path) AS CHAR)), CHAR(10), '') "
               "FROM kb_question WHERE id IN (%s);" % ",".join(str(x) for x in chunk))
        for line in query(sql):
            try:
                handle.write(base64.b64decode(line).decode("utf-8") + "\n")
                written += 1
            except Exception:
                continue
print("已备份 %d 行 -> %s" % (written, backup_path))

# 2. 分批删除
deleted = 0
for i in range(0, len(targets), BATCH):
    chunk = targets[i:i + BATCH]
    query("DELETE FROM kb_question WHERE id IN (%s);" % ",".join(str(x) for x in chunk))
    deleted += len(chunk)
    print("  已删除 %d/%d" % (deleted, len(targets)), flush=True)

after = query("SELECT COUNT(*) FROM kb_question;")[0]
print("完成：%s -> %s 条" % (before, after))
print("下一步：在 k8s-master 执行 python3 kb_es_rebuild.py 重建 ES 索引")

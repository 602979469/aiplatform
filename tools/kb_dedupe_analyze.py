#!/usr/bin/env python3
"""题库近重复题扫描（只读分析 + 可选导出待删备份，不删数据）。

背景：早期批量生成的题目里存在大量「同义改写」——同一知识点换种问法重复入库，
组卷时会被当成不同题抽出来，导致一套卷里看着像重复出题。

判定方式：
  1. 标题归一化（去标点/空格、全角转半角、转小写）后完全相同 -> 重复；
  2. 同（分类, 知识点, 题型）内两两算相似度，>= 阈值 -> 归为同一簇：
     - 解答题：只看标题（阈值 essay_threshold）；
     - 客观题：标题 + 全部选项文本一起比（阈值 choice_threshold，更严，避免
       「下列英雄中定位是射手的是？」和「下列英雄中通常担任辅助的是？」这类结构相似但内容不同的题被误判）；
  3. 解答题短标题是长标题的前缀（被截断的标题）-> 重复。
每簇保留「内容最长、id 最小」的一条，其余标记为待删。

用法（在 k8s-master 上执行）：
  python3 kb_dedupe_analyze.py                # 只统计，打印示例
  python3 kb_dedupe_analyze.py --dump         # 额外把待删整行导出成备份 jsonl
  python3 kb_dedupe_analyze.py --essay-threshold 0.78 --choice-threshold 0.92
"""
import base64
import difflib
import json
import os
import subprocess
import sys
import unicodedata

NAMESPACE = "tsk"
MYSQL_POD = "mysql-0"
MYSQL_DB = "aiplatform"
BACKUP_DIR = "/home/ubuntu/kbgen/dedupe"


def sh(cmd, **kwargs):
    """执行命令并返回 stdout，非 0 退出码直接终止。"""
    done = subprocess.run(cmd, capture_output=True, text=True, **kwargs)
    if done.returncode != 0:
        sys.exit("命令失败: %s\n%s" % (" ".join(cmd[:6]), (done.stderr or "")[:400]))
    return done.stdout


def normalize(title):
    """标题归一化：全角转半角、去掉标点空格、转小写。"""
    text = unicodedata.normalize("NFKC", str(title or "")).lower()
    return "".join(ch for ch in text if ch.isalnum())


def ratio(a, b):
    """标题相似度。"""
    return difflib.SequenceMatcher(None, a, b).ratio()


password = base64.b64decode(sh([
    "kubectl", "get", "secret", "mysql-secret", "-n", NAMESPACE, "-o",
    "jsonpath={.data.root-password}"]).strip()).decode()

essay_threshold = 0.78
choice_threshold = 0.92
if "--essay-threshold" in sys.argv:
    essay_threshold = float(sys.argv[sys.argv.index("--essay-threshold") + 1])
if "--choice-threshold" in sys.argv:
    choice_threshold = float(sys.argv[sys.argv.index("--choice-threshold") + 1])
dump = "--dump" in sys.argv

# 标题里可能有引号/换行，直接用 mysql 客户端输出会破坏 JSON：用 base64 包一层
sql = ("SELECT REPLACE(TO_BASE64(CAST(JSON_OBJECT('id',id,'type',question_type,'category',category,"
       "'subtopic',subtopic,'title',title,'options',IFNULL(options,''),"
       "'len',CHAR_LENGTH(IFNULL(content,'')),'source',source_path) "
       "AS CHAR)), CHAR(10), '') FROM kb_question ORDER BY id;")
raw = sh(["kubectl", "exec", "-n", NAMESPACE, MYSQL_POD, "-c", "mysql", "--", "mysql", "-N", "-B",
          "--default-character-set=utf8mb4", "-uroot", "-p" + password, MYSQL_DB, "-e", sql])

rows = []
for line in raw.splitlines():
    line = line.strip()
    if not line:
        continue
    try:
        rows.append(json.loads(base64.b64decode(line).decode("utf-8")))
    except Exception:
        continue
print("题库总数: %d" % len(rows))

# 按（分类, 知识点, 题型）分桶，只在同桶内比较
buckets = {}
for row in rows:
    key = (row.get("category"), row.get("subtopic"), row.get("type"))
    buckets.setdefault(key, []).append(row)

parent = {}


def find(x):
    """并查集查找。"""
    while parent[x] != x:
        parent[x] = parent[parent[x]]
        x = parent[x]
    return x


def union(a, b):
    """并查集合并。"""
    ra, rb = find(a), find(b)
    if ra != rb:
        parent[rb] = ra


def signature(row):
    """比较用的签名：解答题用标题；客观题用「标题 + 全部选项文本」。"""
    text = normalize(row.get("title"))
    if row.get("type") != "解答":
        options = []
        try:
            for item in json.loads(row.get("options") or "[]"):
                options.append(str(item.get("text") or ""))
        except Exception:
            options = []
        text += "|" + "|".join(sorted(normalize(o) for o in options))
    return text


for i, row in enumerate(rows):
    parent[i] = i
sig = [signature(row) for row in rows]
norm = [normalize(row.get("title")) for row in rows]
index_of = {}
for i, row in enumerate(rows):
    key = (row.get("category"), row.get("subtopic"), row.get("type"))
    index_of.setdefault(key, []).append(i)

for key, indexes in index_of.items():
    for a in range(len(indexes)):
        for b in range(a + 1, len(indexes)):
            i, j = indexes[a], indexes[b]
            na, nb = norm[i], norm[j]
            sa, sb = sig[i], sig[j]
            if not sa or not sb:
                continue
            is_essay = rows[i].get("type") == "解答"
            threshold = essay_threshold if is_essay else choice_threshold
            if sa == sb:
                union(i, j)
                continue
            shorter, longer = (na, nb) if len(na) <= len(nb) else (nb, na)
            if is_essay and len(shorter) >= 8 and longer.startswith(shorter):
                union(i, j)
                continue
            if ratio(sa, sb) >= threshold:
                union(i, j)

clusters = {}
for i in range(len(rows)):
    clusters.setdefault(find(i), []).append(i)

dup_clusters = [members for members in clusters.values() if len(members) > 1]
to_delete = []
for members in dup_clusters:
    # 保留内容最长、其次 id 最小的一条
    keep = sorted(members, key=lambda i: (-(rows[i].get("len") or 0), rows[i].get("id")))[0]
    for i in members:
        if i != keep:
            to_delete.append(i)

print("近重复簇: %d 组，涉及 %d 条，可删除 %d 条（保留每组 1 条）"
      % (len(dup_clusters), sum(len(m) for m in dup_clusters), len(to_delete)))

by_source = {}
for i in to_delete:
    by_source[rows[i].get("source")] = by_source.get(rows[i].get("source"), 0) + 1
print("待删按来源:", dict(sorted(by_source.items(), key=lambda kv: -kv[1])))

by_type = {}
for i in to_delete:
    by_type[rows[i].get("type")] = by_type.get(rows[i].get("type"), 0) + 1
print("待删按题型:", dict(sorted(by_type.items(), key=lambda kv: -kv[1])))

print("\n=== 示例（前 12 组）===")
shown = 0
for members in sorted(dup_clusters, key=lambda m: -len(m)):
    if shown >= 12:
        break
    shown += 1
    print("[%s / %s / %s]" % (rows[members[0]].get("category"), rows[members[0]].get("subtopic"),
                              rows[members[0]].get("type")))
    keep = sorted(members, key=lambda i: (-(rows[i].get("len") or 0), rows[i].get("id")))[0]
    for i in sorted(members, key=lambda i: rows[i].get("id")):
        flag = "保留" if i == keep else "待删"
        print("   %s id=%-6s %s" % (flag, rows[i].get("id"), str(rows[i].get("title"))[:58]))

if dump:
    os.makedirs(BACKUP_DIR, exist_ok=True)
    ids = ",".join(str(rows[i].get("id")) for i in to_delete)
    backup_sql = ("SELECT REPLACE(TO_BASE64(CAST(JSON_OBJECT('id',id,'question_type',question_type,"
                  "'category',category,'subtopic',subtopic,'title',title,'options',options,"
                  "'answer',answer,'content',content,'difficulty',difficulty,'tags',tags,"
                  "'source_path',source_path) AS CHAR)), CHAR(10), '') "
                  "FROM kb_question WHERE id IN (%s);" % (ids or "0"))
    text = sh(["kubectl", "exec", "-n", NAMESPACE, MYSQL_POD, "-c", "mysql", "--", "mysql", "-N", "-B",
               "--default-character-set=utf8mb4", "-uroot", "-p" + password, MYSQL_DB, "-e", backup_sql])
    path = os.path.join(BACKUP_DIR, "dedupe_delete_candidates.jsonl")
    count = 0
    with open(path, "w", encoding="utf-8") as handle:
        for line in text.splitlines():
            line = line.strip()
            if not line:
                continue
            try:
                handle.write(base64.b64decode(line).decode("utf-8") + "\n")
                count += 1
            except Exception:
                continue
    id_path = os.path.join(BACKUP_DIR, "dedupe_delete_ids.txt")
    with open(id_path, "w", encoding="utf-8") as handle:
        handle.write("\n".join(str(rows[i].get("id")) for i in sorted(to_delete)) + "\n")
    print("\n备份文件: %s（%d 行）、%s" % (path, count, id_path))

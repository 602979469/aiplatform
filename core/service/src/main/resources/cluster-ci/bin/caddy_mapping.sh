#!/bin/bash
# 公网域名映射管理：通过 SSH 操作公网服务器上的 Caddy（站点块增删查 + 校验 + reload）
# 用法:
#   caddy_mapping.sh list                     列出当前 Caddy 站点域名（JSON 数组）
#   caddy_mapping.sh add <domain>             新增站点块（xxxx.jakt.online → 127.0.0.1:8080）
#   caddy_mapping.sh remove <domain>          删除站点块
# 前置: master 上存在免密私钥（默认 /home/ubuntu/.ssh/caddy_manage），公钥已加入公网服务器 root
set -euo pipefail

CADDY_HOST="${CADDY_HOST:-root@124.222.40.231}"
CADDY_KEY="${CADDY_KEY:-/home/ubuntu/.ssh/caddy_manage}"
UPSTREAM="${UPSTREAM:-127.0.0.1:8080}"
SSH_OPTS=(-i "${CADDY_KEY}" -o BatchMode=yes -o StrictHostKeyChecking=no -o ConnectTimeout=10)

ACTION="${1:-}"
DOMAIN="${2:-}"

if [ -z "${ACTION}" ]; then
  echo "用法: caddy_mapping.sh <list|add|remove> [domain]" >&2
  exit 1
fi

if [ "${ACTION}" != "list" ]; then
  if ! echo "${DOMAIN}" | grep -qE '^[a-z0-9]([a-z0-9-]*[a-z0-9])?\.jakt\.online$'; then
    echo "域名不合法（只允许 xxxx.jakt.online 形式）: ${DOMAIN}" >&2
    exit 1
  fi
fi

ssh "${SSH_OPTS[@]}" "${CADDY_HOST}" "python3 - '${ACTION}' '${DOMAIN}' '${UPSTREAM}'" <<'PY'
import json
import os
import re
import shutil
import subprocess
import sys
import time

action = sys.argv[1]
domain = sys.argv[2] if len(sys.argv) > 2 else ''
upstream = sys.argv[3] if len(sys.argv) > 3 else '127.0.0.1:8080'
path = '/etc/caddy/Caddyfile'


def read_text():
    with open(path, 'r', encoding='utf-8') as f:
        return f.read()


def find_block(lines, domain):
    """返回 (start, end) 行号区间（含头含尾），找不到返回 None。"""
    start = None
    for i, line in enumerate(lines):
        if re.match(r'^' + re.escape(domain) + r'\s*\{', line.strip()):
            start = i
            break
    if start is None:
        return None
    depth = 0
    for j in range(start, len(lines)):
        depth += lines[j].count('{') - lines[j].count('}')
        if depth <= 0 and j > start:
            return start, j
        if depth <= 0 and j == start:
            return start, j
    return None


def list_domains(lines):
    out = []
    for line in lines:
        s = line.strip()
        if not s or s.startswith('#'):
            continue
        m = re.match(r'^([a-z0-9][a-z0-9.\-]*)\s*\{$', s)
        if m:
            out.append(m.group(1))
    return out


def apply_and_reload(new_text):
    backup = '{}.bak.{}'.format(path, time.strftime('%Y%m%d%H%M%S'))
    shutil.copy2(path, backup)
    tmp = path + '.tmp'
    with open(tmp, 'w', encoding='utf-8') as f:
        f.write(new_text)
    check = subprocess.run(['/usr/local/bin/caddy', 'validate', '--config', tmp],
                           capture_output=True, text=True)
    if check.returncode != 0:
        os.remove(tmp)
        print('配置校验失败，已保留原配置:', (check.stderr or check.stdout).strip()[:400])
        sys.exit(1)
    os.replace(tmp, path)
    reload_res = subprocess.run(['systemctl', 'reload', 'caddy'], capture_output=True, text=True)
    if reload_res.returncode != 0:
        shutil.copy2(backup, path)
        subprocess.run(['systemctl', 'reload', 'caddy'], capture_output=True, text=True)
        print('reload 失败，已回滚:', (reload_res.stderr or '').strip()[:300])
        sys.exit(1)
    print('backup=' + backup)


text = read_text()
lines = text.splitlines()

if action == 'list':
    print(json.dumps(list_domains(lines), ensure_ascii=False))
    sys.exit(0)

if action == 'add':
    if find_block(lines, domain):
        print('已存在: ' + domain)
        sys.exit(0)
    block = [
        '',
        domain + ' {',
        '\tencode zstd gzip',
        '\treverse_proxy ' + upstream + ' {',
        '\t\theader_up Host {host}',
        '\t\theader_up X-Real-IP {remote_host}',
        '\t\theader_up X-Forwarded-For {remote_host}',
        '\t}',
        '}',
    ]
    apply_and_reload(text.rstrip('\n') + '\n' + '\n'.join(block) + '\n')
    print('已新增: ' + domain)
    sys.exit(0)

if action == 'remove':
    rng = find_block(lines, domain)
    if not rng:
        print('不存在: ' + domain)
        sys.exit(0)
    start, end = rng
    new_lines = lines[:start] + lines[end + 1:]
    apply_and_reload('\n'.join(new_lines).rstrip('\n') + '\n')
    print('已删除: ' + domain)
    sys.exit(0)

print('未知操作: ' + action)
sys.exit(1)
PY

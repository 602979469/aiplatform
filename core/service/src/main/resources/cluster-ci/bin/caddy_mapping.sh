#!/bin/bash
# 公网域名映射（只管理公网 Caddy，不创建/删除集群 Ingress）
#
# 数据来源：
#   - 集群 Ingress 的域名（kubectl 读取，作为「ingress 类型」候选，页面只做 Caddy 开关）
#   - 公网 Caddy 的站点块（/etc/caddy/Caddyfile 解析，含 upstream）
#
# 用法:
#   caddy_mapping.sh list                       列出全部域名（集群 Ingress ∪ Caddy 站点，JSON）
#   caddy_mapping.sh enable <domain> [upstream] 开启/新增公网映射（默认 127.0.0.1:8080）+ reload
#   caddy_mapping.sh disable <domain>           关闭/删除公网映射 + reload
#
# 前置: master 上存在免密私钥（默认 /home/ubuntu/.ssh/caddy_manage），公钥已加入公网服务器 root
set -euo pipefail

CADDY_HOST="${CADDY_HOST:-root@124.222.40.231}"
CADDY_KEY="${CADDY_KEY:-/home/ubuntu/.ssh/caddy_manage}"
DEFAULT_UPSTREAM="${DEFAULT_UPSTREAM:-127.0.0.1:8080}"
KUBECTL="${KUBECTL:-kubectl}"
SSH_OPTS=(-i "${CADDY_KEY}" -o BatchMode=yes -o StrictHostKeyChecking=no -o ConnectTimeout=10)

ACTION="${1:-}"
DOMAIN="${2:-}"
UPSTREAM="${3:-${DEFAULT_UPSTREAM}}"

usage() {
  echo "用法: caddy_mapping.sh <list|enable|disable> [domain] [upstream]" >&2
  exit 1
}

[ -n "${ACTION}" ] || usage
case "${ACTION}" in
  list|enable|disable) ;;
  *) usage ;;
esac

if [ "${ACTION}" != "list" ]; then
  if ! echo "${DOMAIN}" | grep -qE '^[a-z0-9]([a-z0-9-]*[a-z0-9])?\.jakt\.online$'; then
    echo "域名不合法（只允许 xxxx.jakt.online 形式）: ${DOMAIN}" >&2
    exit 1
  fi
  if ! echo "${UPSTREAM}" | grep -qE '^[A-Za-z0-9._-]+:[0-9]{1,5}$'; then
    echo "上游地址不合法（形如 127.0.0.1:8080）: ${UPSTREAM}" >&2
    exit 1
  fi
fi

# 集群内 Ingress 域名清单（master 上执行）
k8s_list_json() {
  ${KUBECTL} get ingress -A -o json 2>/dev/null | python3 -c '
import json, sys
d = json.load(sys.stdin)
out = []
for it in d.get("items", []):
    ns = it["metadata"]["namespace"]
    name = it["metadata"]["name"]
    for rule in (it.get("spec", {}) or {}).get("rules", []) or []:
        host = rule.get("host")
        if not host:
            continue
        for p in ((rule.get("http", {}) or {}).get("paths", []) or []):
            b = ((p.get("backend", {}) or {}).get("service", {}) or {})
            port = (b.get("port", {}) or {})
            out.append({
                "domain": host,
                "ingress": name,
                "namespace": ns,
                "service": b.get("name"),
                "port": port.get("number") or port.get("name"),
            })
print(json.dumps(out, ensure_ascii=False))
'
}

# 公网 Caddy 操作（SSH 到公网服务器执行 python）
caddy_py() {
  ssh "${SSH_OPTS[@]}" "${CADDY_HOST}" "python3 - '${1}' '${DOMAIN}' '${UPSTREAM}'" <<'PY'
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
    """返回 (start, end) 行号区间；找不到返回 None。"""
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
        if depth <= 0:
            return start, j
    return None


def list_blocks(lines):
    """解析站点块：返回 [{domain, upstream}]。"""
    out = []
    i = 0
    while i < len(lines):
        s = lines[i].strip()
        if s and not s.startswith('#') and re.match(r'^[a-z0-9][a-z0-9.\-]*\s*\{$', s):
            domain = s.split()[0]
            rng = find_block(lines, domain)
            end = rng[1] if rng else i
            body = '\n'.join(lines[i:end + 1])
            m = re.search(r'reverse_proxy\s+([^\s{]+)', body)
            out.append({'domain': domain, 'upstream': m.group(1) if m else None})
            i = end + 1
            continue
        i += 1
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
        print('配置校验失败，已保留原配置: ' + (check.stderr or check.stdout).strip()[:400])
        sys.exit(1)
    os.replace(tmp, path)
    reload_res = subprocess.run(['systemctl', 'reload', 'caddy'], capture_output=True, text=True)
    if reload_res.returncode != 0:
        shutil.copy2(backup, path)
        subprocess.run(['systemctl', 'reload', 'caddy'], capture_output=True, text=True)
        print('reload 失败，已回滚: ' + (reload_res.stderr or '').strip()[:300])
        sys.exit(1)
    print('backup=' + backup)


text = read_text()
lines = text.splitlines()

if action == 'list':
    print(json.dumps(list_blocks(lines), ensure_ascii=False))
    sys.exit(0)

if action == 'enable':
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
    print('已开启: ' + domain + ' -> ' + upstream)
    sys.exit(0)

if action == 'disable':
    rng = find_block(lines, domain)
    if not rng:
        print('不存在: ' + domain)
        sys.exit(0)
    start, end = rng
    new_lines = lines[:start] + lines[end + 1:]
    apply_and_reload('\n'.join(new_lines).rstrip('\n') + '\n')
    print('已关闭: ' + domain)
    sys.exit(0)

print('未知操作: ' + action)
sys.exit(1)
PY
}

case "${ACTION}" in
  list)
    CADDY_JSON=$(caddy_py list)
    INGRESS_JSON=$(k8s_list_json)
    CADDY_JSON="${CADDY_JSON}" INGRESS_JSON="${INGRESS_JSON}" python3 -c '
import json, os
caddy = {item["domain"]: item for item in json.loads(os.environ["CADDY_JSON"])}
ingress = json.loads(os.environ["INGRESS_JSON"])
by_domain = {}
for item in ingress:
    by_domain.setdefault(item["domain"], item)
domains = sorted(set(caddy.keys()) | set(by_domain.keys()))
out = []
for d in domains:
    ing = by_domain.get(d)
    cad = caddy.get(d) or {}
    out.append({
        "domain": d,
        "caddy": d in caddy,
        "type": "ingress" if ing else "custom",
        "upstream": cad.get("upstream"),
        "ingressName": (ing or {}).get("ingress"),
        "namespace": (ing or {}).get("namespace"),
        "service": (ing or {}).get("service"),
        "port": (ing or {}).get("port"),
    })
print(json.dumps(out, ensure_ascii=False))
'
    ;;
  enable)
    caddy_py enable
    ;;
  disable)
    caddy_py disable
    ;;
esac

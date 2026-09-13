#!/bin/bash
# 公网域名映射（只管理公网 Caddy；集群 Ingress 仅作域名来源，不改动）
#
# 关闭（disable）采用「注释站点块」实现：域名仍保留在配置文件里（列表能看到、可再次开启），
# 删除（delete）才会真正把站点块从配置里移除。
#
# 用法:
#   caddy_mapping.sh list                        列出全部域名（集群 Ingress ∪ Caddy，含已关闭），JSON
#   caddy_mapping.sh enable <domain> [upstream]  开启/新增（默认 127.0.0.1:8080）+ reload
#   caddy_mapping.sh disable <domain>            关闭（注释站点块，保留记录）+ reload
#   caddy_mapping.sh delete <domain>             彻底删除站点块（含已关闭记录）+ reload
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
  echo "用法: caddy_mapping.sh <list|enable|disable|delete> [domain] [upstream]" >&2
  exit 1
}

[ -n "${ACTION}" ] || usage
case "${ACTION}" in
  list|enable|disable|delete) ;;
  *) usage ;;
esac

if [ "${ACTION}" != "list" ]; then
  if ! echo "${DOMAIN}" | grep -qE '^([a-z0-9]([a-z0-9-]*[a-z0-9])?\.)+jakt\.online$'; then
    echo "域名不合法（只允许 *.jakt.online，支持多级）: ${DOMAIN}" >&2
    exit 1
  fi
  if [ "${ACTION}" = "enable" ] && ! echo "${UPSTREAM}" | grep -qE '^[A-Za-z0-9._-]+:[0-9]{1,5}$'; then
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
    for rule in (it.get("spec", {}) or {}).get("rules", []) or []:
        host = rule.get("host")
        if not host:
            continue
        for p in ((rule.get("http", {}) or {}).get("paths", []) or []):
            b = ((p.get("backend", {}) or {}).get("service", {}) or {})
            port = (b.get("port", {}) or {})
            out.append({
                "domain": host,
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


def read_lines():
    with open(path, 'r', encoding='utf-8') as f:
        return f.read().splitlines()


def header_re(domain, disabled):
    prefix = r'#\s*' if disabled else ''
    return re.compile(r'^' + prefix + re.escape(domain) + r'\s*\{')


def strip_comment(line):
    if line.startswith('# '):
        return line[2:]
    if line.startswith('#'):
        return line[1:]
    return line


def find_block(lines, domain, disabled=False):
    """返回 (start, end)；找不到返回 None。disabled=True 时匹配被注释的块。"""
    rx = header_re(domain, disabled)
    start = None
    for i, line in enumerate(lines):
        if rx.match(line.strip()):
            start = i
            break
    if start is None:
        return None
    depth = 0
    for j in range(start, len(lines)):
        text = strip_comment(lines[j]) if disabled else lines[j]
        depth += text.count('{') - text.count('}')
        if depth <= 0:
            return start, j
    return None


def parse_all(lines):
    """解析全部站点块（含被注释关闭的），返回 [{domain, upstream, caddy}]。"""
    out = []
    i = 0
    while i < len(lines):
        s = lines[i].strip()
        m = re.match(r'^(#\s*)?([a-z0-9][a-z0-9.\-]*)\s*\{\s*$', s)
        if m and (not s.startswith('#') or m.group(1)):
            disabled = bool(m.group(1))
            dom = m.group(2)
            rng = find_block(lines, dom, disabled)
            end = rng[1] if rng else i
            body = '\n'.join(strip_comment(x) if disabled else x for x in lines[i:end + 1])
            up = re.search(r'reverse_proxy\s+([^\s{]+)', body)
            out.append({'domain': dom, 'upstream': up.group(1) if up else None, 'caddy': not disabled})
            i = end + 1
            continue
        i += 1
    return out


def apply_and_reload(new_lines):
    backup = '{}.bak.{}'.format(path, time.strftime('%Y%m%d%H%M%S'))
    shutil.copy2(path, backup)
    tmp = path + '.tmp'
    with open(tmp, 'w', encoding='utf-8') as f:
        f.write('\n'.join(new_lines).rstrip('\n') + '\n')
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


lines = read_lines()

if action == 'list':
    print(json.dumps(parse_all(lines), ensure_ascii=False))
    sys.exit(0)

if action == 'enable':
    disabled_rng = find_block(lines, domain, True)
    if disabled_rng:
        start, end = disabled_rng
        for i in range(start, end + 1):
            lines[i] = strip_comment(lines[i])
        apply_and_reload(lines)
        print('已开启: ' + domain)
        sys.exit(0)
    if find_block(lines, domain, False):
        print('已开启: ' + domain)
        sys.exit(0)
    lines += [
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
    apply_and_reload(lines)
    print('已开启: ' + domain + ' -> ' + upstream)
    sys.exit(0)

if action == 'disable':
    active = find_block(lines, domain, False)
    if not active:
        print('已关闭: ' + domain)
        sys.exit(0)
    start, end = active
    for i in range(start, end + 1):
        lines[i] = '# ' + lines[i]
    apply_and_reload(lines)
    print('已关闭（保留配置，可再次开启）: ' + domain)
    sys.exit(0)

if action == 'delete':
    removed = False
    for disabled in (False, True):
        rng = find_block(lines, domain, disabled)
        if rng:
            start, end = rng
            del lines[start:end + 1]
            removed = True
    if not removed:
        print('不存在: ' + domain)
        sys.exit(0)
    apply_and_reload(lines)
    print('已删除: ' + domain)
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
PRIMARY = {"jakt.online", "www.jakt.online"}
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
        "caddy": bool(cad.get("caddy")),
        "primary": d in PRIMARY,
        "type": "ingress" if ing else "custom",
        "upstream": cad.get("upstream"),
        "namespace": (ing or {}).get("namespace"),
        "service": (ing or {}).get("service"),
        "port": (ing or {}).get("port"),
    })
out.sort(key=lambda r: (not r["primary"], r["domain"]))
print(json.dumps(out, ensure_ascii=False))
'
    ;;
  enable)
    caddy_py enable
    ;;
  disable)
    caddy_py disable
    ;;
  delete)
    caddy_py delete
    ;;
esac

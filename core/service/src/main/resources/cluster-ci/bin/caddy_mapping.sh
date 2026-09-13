#!/bin/bash
# 公网域名映射管理：Ingress（集群内路由）+ Caddy（公网入口）双写
# 用法:
#   caddy_mapping.sh list                                     列出全部域名映射（JSON）
#   caddy_mapping.sh add <domain> [namespace service port]     新增：建 Ingress（可选）+ Caddy 站点块 + reload
#   caddy_mapping.sh remove <domain>                           删除：删 Ingress（dm- 前缀）+ Caddy 站点块 + reload
# 域名规则: 只允许 xxxx.jakt.online（防注入）；Ingress 名称固定为 dm-<域名点转横线>
# 前置: master 上存在免密私钥（默认 /home/ubuntu/.ssh/caddy_manage），公钥已加入公网服务器 root
set -euo pipefail

CADDY_HOST="${CADDY_HOST:-root@124.222.40.231}"
CADDY_KEY="${CADDY_KEY:-/home/ubuntu/.ssh/caddy_manage}"
UPSTREAM="${UPSTREAM:-127.0.0.1:8080}"
KUBECTL="${KUBECTL:-kubectl}"
SSH_OPTS=(-i "${CADDY_KEY}" -o BatchMode=yes -o StrictHostKeyChecking=no -o ConnectTimeout=10)

ACTION="${1:-}"
DOMAIN="${2:-}"
NAMESPACE="${3:-}"
SERVICE="${4:-}"
PORT="${5:-80}"

usage() {
  echo "用法: caddy_mapping.sh <list|add|remove> [domain] [namespace service port]" >&2
  exit 1
}

[ -n "${ACTION}" ] || usage
case "${ACTION}" in
  list|add|remove) ;;
  *) usage ;;
esac

if [ "${ACTION}" != "list" ]; then
  if ! echo "${DOMAIN}" | grep -qE '^[a-z0-9]([a-z0-9-]*[a-z0-9])?\.jakt\.online$'; then
    echo "域名不合法（只允许 xxxx.jakt.online 形式）: ${DOMAIN}" >&2
    exit 1
  fi
fi

INGRESS_NAME="dm-$(echo "${DOMAIN}" | tr '.' '-')"

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
                "path": p.get("path"),
                "managed": name.startswith("dm-"),
            })
print(json.dumps(out, ensure_ascii=False))
'
}

k8s_apply_ingress() {
  ${KUBECTL} apply -f - <<YAML
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ${INGRESS_NAME}
  namespace: ${NAMESPACE}
  annotations:
    nginx.ingress.kubernetes.io/use-forwarded-headers: "true"
spec:
  ingressClassName: nginx
  rules:
    - host: ${DOMAIN}
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: ${SERVICE}
                port:
                  number: ${PORT}
YAML
}

k8s_delete_ingress() {
  local ns
  ns=$(${KUBECTL} get ingress -A -o json 2>/dev/null | python3 -c "
import json, sys
d = json.load(sys.stdin)
print(next((i['metadata']['namespace'] for i in d.get('items', [])
            if i['metadata']['name'] == '${INGRESS_NAME}'), ''))
")
  if [ -n "${ns}" ]; then
    ${KUBECTL} delete ingress "${INGRESS_NAME}" -n "${ns}" --ignore-not-found
  else
    echo "Ingress 不存在: ${INGRESS_NAME}"
  fi
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
}

case "${ACTION}" in
  list)
    CADDY_JSON=$(caddy_py list)
    INGRESS_JSON=$(k8s_list_json)
    CADDY_JSON="${CADDY_JSON}" INGRESS_JSON="${INGRESS_JSON}" python3 -c '
import json, os
caddy = set(json.loads(os.environ["CADDY_JSON"]))
ingress = json.loads(os.environ["INGRESS_JSON"])
by_domain = {}
for item in ingress:
    by_domain.setdefault(item["domain"], []).append(item)
domains = sorted(caddy | set(by_domain.keys()))
out = []
for d in domains:
    entries = by_domain.get(d, [])
    e = entries[0] if entries else {}
    out.append({
        "domain": d,
        "caddy": d in caddy,
        "ingress": bool(entries),
        "ingressName": e.get("ingress"),
        "namespace": e.get("namespace"),
        "service": e.get("service"),
        "port": e.get("port"),
        "managed": bool(e.get("managed")),
    })
print(json.dumps(out, ensure_ascii=False))
'
    ;;
  add)
    if [ -n "${NAMESPACE}" ] && [ -n "${SERVICE}" ]; then
      k8s_apply_ingress >/dev/null
      echo "Ingress 已更新: ${NAMESPACE}/${INGRESS_NAME} -> ${SERVICE}:${PORT}"
    fi
    caddy_py add
    ;;
  remove)
    k8s_delete_ingress
    caddy_py remove
    ;;
esac

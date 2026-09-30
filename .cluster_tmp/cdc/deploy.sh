#!/bin/bash
# Canal（MySQL → ES CDC）部署脚本，在 k8s-master 上执行，幂等可反复执行
# 组成：canal-server（镜像）+ canal-adapter（官方 tar.gz 包，用同一镜像的 Java 8 运行）
# 凭据：cdc-secret（canal 库密码 + ES 密码，ES 密码直接从 efk-secret 取，保证一致）
set -euo pipefail

MIRROR="docker.xuanyuan.run"
NS="cdc"
DIR="$(cd "$(dirname "$0")" && pwd)"
CANAL_IMAGE="canal/canal-server:v1.1.8"
ADAPTER_DIR="/home/ubuntu/cdc/adapter"
CANAL_DB_USER="canal"
CANAL_DB_PASSWORD="${CANAL_DB_PASSWORD:-Canal@2026}"

echo ">>> 1/6 镜像准备（${CANAL_IMAGE}）"
docker pull "${MIRROR}/${CANAL_IMAGE}" >/dev/null
docker tag "${MIRROR}/${CANAL_IMAGE}" "docker.io/${CANAL_IMAGE}"
docker save "docker.io/${CANAL_IMAGE}" | sudo ctr -n k8s.io images import - >/dev/null
echo "    ok docker.io/${CANAL_IMAGE}"

echo ">>> 2/6 检查 adapter 包"
[ -d "${ADAPTER_DIR}" ] || {
  echo "缺 adapter 包：请先在 ${ADAPTER_DIR} 解压 canal.adapter-1.1.8.tar.gz" >&2
  exit 1
}

echo ">>> 3/6 MySQL 同步账号（幂等）"
MYSQL_PW=$(kubectl -n tsk get secret mysql-secret -o jsonpath='{.data.root-password}' | base64 -d)
kubectl -n tsk exec mysql-0 -- mysql -uroot -p"${MYSQL_PW}" -e "
CREATE USER IF NOT EXISTS '${CANAL_DB_USER}'@'%' IDENTIFIED BY '${CANAL_DB_PASSWORD}';
ALTER USER '${CANAL_DB_USER}'@'%' IDENTIFIED BY '${CANAL_DB_PASSWORD}';
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO '${CANAL_DB_USER}'@'%';
FLUSH PRIVILEGES;" 2>/dev/null

echo ">>> 4/6 namespace + secret"
kubectl create namespace "${NS}" 2>/dev/null || true
ES_PASSWORD=$(kubectl -n efk get secret efk-secret -o jsonpath='{.data.elastic-password}' | base64 -d)
kubectl -n "${NS}" create secret generic cdc-secret \
  --from-literal=canal-db-password="${CANAL_DB_PASSWORD}" \
  --from-literal=es-password="${ES_PASSWORD}" \
  --dry-run=client -o yaml | kubectl apply -f - >/dev/null

echo ">>> 5/6 应用 manifests"
kubectl apply -R -f "${DIR}/manifests/"

echo ">>> 6/6 等待就绪"
kubectl -n "${NS}" rollout status deployment/canal-server --timeout=300s
kubectl -n "${NS}" rollout status deployment/canal-adapter --timeout=300s

echo
kubectl -n "${NS}" get pods -o wide
echo
echo "canal-server: canal-server.${NS}.svc.cluster.local:11111"
echo "adapter 状态: kubectl -n ${NS} logs deploy/canal-adapter | grep -i 'adapter\\|error' | tail"

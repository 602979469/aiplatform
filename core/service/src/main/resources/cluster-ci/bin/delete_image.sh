#!/bin/bash
# 删除镜像：Harbor artifact + 各节点（master/worker）containerd/docker 镜像 + MinIO tar
# 用法: delete_image.sh <image_name> [version]
#   - 传 version：删除该镜像的指定版本（harbor.jakt.online/library/{image_name}:{version}）
#   - 不传 version：删除该镜像名的全部版本（Harbor 仓库整删 + 节点上所有该名前缀镜像）
set -euo pipefail

IMAGE_NAME="$1"
VERSION="${2:-}"

HARBOR_HOST="harbor.jakt.online"
PROJECT="library"
HARBOR_URL="http://${HARBOR_HOST}"
WORKER_HOSTS="${WORKER_HOSTS:-ubuntu@192.168.3.217}"

# Harbor 凭据：从 harbor-cred（docker-registry secret）里解出 user:pass
CRED_JSON=$(kubectl get secret harbor-cred -n harbor -o jsonpath='{.data.\.dockerconfigjson}' | base64 -d)
CRED=$(echo "${CRED_JSON}" | python3 -c "
import json, sys, base64
d = json.load(sys.stdin)
auth = d['auths'].get('${HARBOR_HOST}', {}).get('auth', '')
print(base64.b64decode(auth).decode() if auth else 'admin:Harbor12345')
")
HARBOR_USER="${CRED%%:*}"
HARBOR_PASS="${CRED#*:}"

echo "========== 删除镜像 ${IMAGE_NAME}${VERSION:+:${VERSION}} =========="

# 1. Harbor 删除
echo ">>> Harbor 删除"
if [ -n "${VERSION}" ]; then
  code=$(curl -s -o /tmp/harbor-del.log -w '%{http_code}' -u "${HARBOR_USER}:${HARBOR_PASS}" \
    -X DELETE "${HARBOR_URL}/api/v2.0/projects/${PROJECT}/repositories/${IMAGE_NAME}/artifacts/${VERSION}")
else
  code=$(curl -s -o /tmp/harbor-del.log -w '%{http_code}' -u "${HARBOR_USER}:${HARBOR_PASS}" \
    -X DELETE "${HARBOR_URL}/api/v2.0/projects/${PROJECT}/repositories/${IMAGE_NAME}")
fi
if [ "${code}" = "200" ] || [ "${code}" = "202" ]; then
  echo "Harbor 删除成功（HTTP ${code}）"
else
  echo "Harbor 删除返回 ${code}：$(cat /tmp/harbor-del.log 2>/dev/null | head -c 200)"
fi
rm -f /tmp/harbor-del.log

# 2. 各节点 containerd + docker 删除（按镜像名前缀匹配，支持历史镜像批量清）
echo ">>> 节点镜像删除"
delete_on_node() {
  local target="$1"
  local script
  script=$(cat <<'NODEEOF'
set +e
MATCH="${1}"
# containerd（k8s 运行时）
CTR_IMAGES=$(sudo ctr -n k8s.io images ls -q 2>/dev/null | grep -F "${MATCH}")
if [ -n "${CTR_IMAGES}" ]; then
  echo "containerd 待删:"
  echo "${CTR_IMAGES}"
  for img in ${CTR_IMAGES}; do
    sudo ctr -n k8s.io images rm "${img}" 2>&1 | grep -vE '^$' || echo "  [跳过] ${img}（可能被占用）"
  done
fi
# docker
DOCKER_IMAGES=$(docker images --format '{{.Repository}}:{{.Tag}}' 2>/dev/null | grep -F "${MATCH}")
if [ -n "${DOCKER_IMAGES}" ]; then
  echo "docker 待删:"
  echo "${DOCKER_IMAGES}"
  for img in ${DOCKER_IMAGES}; do
    docker rmi "${img}" >/dev/null 2>&1 || echo "  [跳过] ${img}（可能被占用）"
  done
fi
echo "节点 $(hostname) 清理完成"
NODEEOF
)
  if [ "${target}" = "localhost" ]; then
    sudo bash -c "${script}" _ "${IMAGE_NAME}${VERSION:+:${VERSION}}"
  else
    ssh -o BatchMode=yes "${target}" "sudo bash -c '$(printf '%s' "${script}" | sed "s/'/'\\\\''/g")' _ '${IMAGE_NAME}${VERSION:+:${VERSION}}'"
  fi
}

delete_on_node "localhost"
delete_on_node "${WORKER_HOSTS}"

# 3. MinIO tar 删除（指定版本时）
if [ -n "${VERSION}" ]; then
  TAR_NAME="${IMAGE_NAME}_${VERSION}.tar.gz"
  MIP=$(kubectl get svc minio -n tsk -o jsonpath='{.spec.clusterIP}')
  AK=$(kubectl get secret minio-credentials -n tsk -o jsonpath='{.data.MINIO_ROOT_USER}' | base64 -d)
  SK=$(kubectl get secret minio-credentials -n tsk -o jsonpath='{.data.MINIO_ROOT_PASSWORD}' | base64 -d)
  docker run --rm --entrypoint sh docker.xuanyuan.run/minio/mc:latest -c "
    mc alias set minio http://${MIP}:9000 ${AK} ${SK} >/dev/null
    mc rm minio/image-tars/${TAR_NAME} >/dev/null 2>&1 && echo 'MinIO tar 已删除: ${TAR_NAME}' || echo 'MinIO tar 不存在或已删'
  "
fi

echo "========== 删除流程完成 =========="

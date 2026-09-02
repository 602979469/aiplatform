#!/bin/bash
# 导入外部镜像到 Harbor（buildx imagetools create：保留多架构 manifest，不本地拉取）
# 用法: import_image.sh <image_name> <version> <external_ref>
#   例: import_image.sh mysql 8.0 docker.io/mysql:8.0
# 输出: stdout 最后一行 = harbor_ref（供后端解析）
set -euo pipefail

IMAGE_NAME="$1"
VERSION="$2"
EXTERNAL_REF="$3"

HARBOR_HOST="harbor.jakt.online"
HARBOR_REF="${HARBOR_HOST}/library/${IMAGE_NAME}:${VERSION}"

# 1. 登录 Harbor（按需：已登录则跳过）
if ! docker info 2>/dev/null | grep -q "${HARBOR_HOST}"; then
  kubectl get secret harbor-cred -n harbor -o jsonpath='{.data.\.dockerconfigjson}' | base64 -d \
    | docker login "${HARBOR_HOST}" --password-stdin >/dev/null
fi

# 2. imagetools 复制：源 registry → Harbor（保留 amd64/arm64 等全部架构）
echo ">>> 导入 ${EXTERNAL_REF} -> ${HARBOR_REF}"
docker buildx imagetools create --tag "${HARBOR_REF}" "${EXTERNAL_REF}"

echo "[OK] ${HARBOR_REF}"
echo "${HARBOR_REF}"

#!/bin/bash
# 构建镜像 → Harbor（buildx 多架构 amd64+arm64）→ tar 归档 MinIO
# 用法: build_image.sh <image_name> <version> <src_dir> [dockerfile_path]
# 前置:
#   - buildx multi builder 已就绪（docker buildx inspect multi）
#   - harbor-cred Secret（harbor 命名空间）存在
#   - worker ssh 免密（不需要，buildx 全在 master）
set -euo pipefail

IMAGE_NAME="$1"
VERSION="$2"
SRC_DIR="$3"
DOCKERFILE_PATH="${4:-}"

HARBOR_HOST="harbor.jakt.online"
HARBOR_REF="${HARBOR_HOST}/library/${IMAGE_NAME}:${VERSION}"
TAR_NAME="${IMAGE_NAME}_${VERSION}.tar.gz"

# 1. 登录 Harbor（按需：已登录则跳过）
if ! docker info 2>/dev/null | grep -q "${HARBOR_HOST}"; then
  kubectl get secret harbor-cred -n harbor -o jsonpath='{.data.\.dockerconfigjson}' | base64 -d \
    | docker login "${HARBOR_HOST}" --password-stdin >/dev/null
fi

# 2. 用户 Dockerfile 覆盖仓库自带（可选）
if [ -n "${DOCKERFILE_PATH}" ] && [ -f "${DOCKERFILE_PATH}" ]; then
  cp "${DOCKERFILE_PATH}" "${SRC_DIR}/Dockerfile"
fi

# 3. buildx 多架构构建并推送（Harbor 保存多架构 manifest，节点按架构拉）
echo ">>> buildx 多架构构建并推送 ${HARBOR_REF}"
docker buildx build --platform linux/amd64,linux/arm64 \
  -f "${SRC_DIR}/Dockerfile" \
  -t "${HARBOR_REF}" \
  --push "${SRC_DIR}"

# 4. tar 归档 MinIO（amd64 tar，备份/下载用；存 image-tars/{tar_name}）
echo ">>> 生成 tar 并归档 MinIO"
docker buildx build --platform linux/amd64 \
  -o type=docker,dest="/tmp/${TAR_NAME}" \
  -f "${SRC_DIR}/Dockerfile" "${SRC_DIR}" >/dev/null

MIP=$(kubectl get svc minio -n tsk -o jsonpath='{.spec.clusterIP}')
AK=$(kubectl get secret minio-credentials -n tsk -o jsonpath='{.data.MINIO_ROOT_USER}' | base64 -d)
SK=$(kubectl get secret minio-credentials -n tsk -o jsonpath='{.data.MINIO_ROOT_PASSWORD}' | base64 -d)
cat "/tmp/${TAR_NAME}" | docker run -i --rm --entrypoint sh docker.xuanyuan.run/minio/mc:latest -c "
  mc alias set minio http://${MIP}:9000 ${AK} ${SK} >/dev/null
  mc mb -p minio/image-tars >/dev/null 2>&1 || true
  mc pipe minio/image-tars/${TAR_NAME} >/dev/null
"
rm -f "/tmp/${TAR_NAME}"

echo "[OK] ${HARBOR_REF}"
echo "${HARBOR_REF}"

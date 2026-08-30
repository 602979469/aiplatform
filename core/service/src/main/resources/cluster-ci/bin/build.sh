#!/bin/bash
# 构建 + 导入镜像（master 本机 + worker 各自架构），不含部署
# 用法: build.sh <image> <tag> <src_dir> <worker_host>
# 说明: 输出走 stdout/stderr，由调用方（pipeline.sh）统一 tee 进日志；单独调用直接打印
set -euo pipefail

IMAGE="$1"
TAG="$2"
SRC_DIR="$3"
WORKER_HOST="${4:-ubuntu@192.168.3.217}"

BIN_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_IMPORT="${BIN_DIR}/build_import.sh"

echo ">>> master 构建导入"
bash "${BUILD_IMPORT}" "${IMAGE}" "${TAG}" "${SRC_DIR}"

# worker 需要同一脚本
echo ">>> worker 构建导入"
ssh "${WORKER_HOST}" "mkdir -p $(dirname "${BUILD_IMPORT}")"
scp -q "${BUILD_IMPORT}" "${WORKER_HOST}:${BUILD_IMPORT}"
WORKER_SRC="/tmp/${IMAGE}-src"
ssh "${WORKER_HOST}" "rm -rf ${WORKER_SRC} && mkdir -p ${WORKER_SRC}"
rsync -a --delete "${SRC_DIR}/" "${WORKER_HOST}:${WORKER_SRC}/"
ssh "${WORKER_HOST}" "bash '${BUILD_IMPORT}' '${IMAGE}' '${TAG}' '${WORKER_SRC}'"
echo "========== 双架构镜像导入完成 =========="

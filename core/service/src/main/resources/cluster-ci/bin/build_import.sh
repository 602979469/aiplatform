#!/bin/bash
# 单节点构建并导入 containerd：docker build → save → ctr import（各自架构天然正确）
# 用法: build_import.sh <image> <tag> <src_dir>
# 依赖缓存由 Dockerfile 内 --mount=type=cache 负责（buildx 不支持 docker build -v）
# 防并发：同一节点只允许一个构建任务（flock 非阻塞，占用则退出码 1）
set -euo pipefail

LOCK_DIR="/home/ubuntu/cluster-ci/locks"
mkdir -p "${LOCK_DIR}"
exec 9>"${LOCK_DIR}/build.lock"
if ! flock -n 9; then
    echo "节点 $(hostname) 另一构建任务进行中，本次跳过"
    exit 1
fi

IMAGE="$1"
TAG="$2"
SRC_DIR="$3"
echo ">>> $(hostname) $(uname -m): build ${IMAGE}:${TAG} from ${SRC_DIR}"
docker build -t "${IMAGE}:${TAG}" -f "${SRC_DIR}/Dockerfile" "${SRC_DIR}" >/dev/null
TAR="/tmp/${IMAGE}-${TAG}.tar"
docker save "${IMAGE}:${TAG}" -o "${TAR}"
sudo ctr -n k8s.io image import "${TAR}"
rm -f "${TAR}"
echo "[OK] $(hostname) $(uname -m) 导入完成 ${IMAGE}:${TAG}"

#!/bin/bash
# 拉取指定仓库分支源码（git 浅克隆单分支，直接用用户填写的 git 地址）
# 用法: fetch_source.sh <git_url> <branch> <dest_dir> [dockerfile_path]
# 地址说明: 公开仓库直接填 https://域名/owner/repo.git；私有仓库地址自带凭证 https://用户名:token@域名/owner/repo.git
# 输出约定: 拉取进度归一化后走 stderr（由 pipeline.sh 统一 tee 进 deploy 日志）；stdout 只输出 commit 短哈希
set -euo pipefail

GIT_URL="$1"
BRANCH="$2"
DEST_DIR="$3"
DOCKERFILE_PATH="${4:-}"

# 浅克隆单分支：进度（\r 转 \n、去空行）输出到 stderr，stdout 保持只有短哈希
export GIT_HTTP_LOW_SPEED_LIMIT=1
export GIT_HTTP_LOW_SPEED_TIME=60
rm -rf "${DEST_DIR}"
if ! git clone --progress --depth 1 --branch "${BRANCH}" "${GIT_URL}" "${DEST_DIR}" 2>&1 \
    | tr '\r' '\n' \
    | sed '/^[[:space:]]*$/d' \
    >&2; then
    echo "git 克隆失败: ${BRANCH}" >&2
    exit 1
fi

# 用户配置的 Dockerfile 覆盖仓库里的
if [ -n "${DOCKERFILE_PATH}" ] && [ -f "${DOCKERFILE_PATH}" ]; then
    cp "${DOCKERFILE_PATH}" "${DEST_DIR}/Dockerfile"
fi

# 输出 commit 短哈希（供镜像 tag；stdout 仅此一行）
SHORT=$(git -C "${DEST_DIR}" rev-parse --short HEAD)
echo "${SHORT}"

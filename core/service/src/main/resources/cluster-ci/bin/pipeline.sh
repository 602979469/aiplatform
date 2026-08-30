#!/bin/bash
# cluster-ci 统一编排：lock + state 判断，决定是否拉源码构建，最后统一部署
# 用法: pipeline.sh <appId> <image> <gitUrl> <branch> <srcDir> <appDir> <namespace> <deployYaml> <dockerfilePath> <workerHost>
# 行为:
#   - 有 state 且等于最新 commit → 跳过 fetch+build，直接 deploy
#   - 无 state 或 commit 变化   → fetch_source → build → deploy → 写 state
#   - 整个流水线（拉码+构建+部署）统一 tee 到 appDir/deploy-<时间戳>.log，stdout 同步输出
#   - stdout 最后一行输出镜像 tag（供后端解析）
set -euo pipefail

APP_ID="$1"
IMAGE="$2"
GIT_URL="$3"
BRANCH="$4"
SRC_DIR="$5"
APP_DIR="$6"
NAMESPACE="$7"
DEPLOY_YAML="$8"
DOCKERFILE_PATH="${9:-}"
WORKER_HOST="${10:-ubuntu@192.168.3.217}"

BIN_DIR="$(cd "$(dirname "$0")" && pwd)"
STATE_FILE="${APP_DIR}/state"
LOCK_FILE="$(dirname "${BIN_DIR}")/locks/pipeline-${APP_ID}.lock"

# 统一日志：一次流水线一个文件，覆盖拉码/构建/部署全阶段
# 注意：不能在这里 wait 进程替换（会死锁：脚本握着 stdout 写端，tee 等不到 EOF）
if [ -n "${APP_DIR}" ]; then
    mkdir -p "${APP_DIR}"
    LOG_FILE="${APP_DIR}/deploy-$(date +%Y%m%d%H%M%S).log"
    exec > >(tr '\r' '\n' | tee "${LOG_FILE}") 2>&1
fi

# 防并发：同一 app 同一时间只允许一个流水线
mkdir -p "$(dirname "${LOCK_FILE}")"
exec 9>"${LOCK_FILE}"
flock -n 9 || { echo "另一流水线进行中，本次跳过" >&2; exit 1; }

# 1. 最新 commit（短哈希）
LATEST=$(git ls-remote "${GIT_URL}" "${BRANCH}" 2>/dev/null | head -1 | cut -c1-7)
if [ -z "${LATEST}" ]; then
    echo "获取远程 commit 失败: ${GIT_URL} ${BRANCH}" >&2
    exit 1
fi

TAG="${LATEST}"
LAST_BUILT=$(cat "${STATE_FILE}" 2>/dev/null || true)

# 2. state 判断：本地已构建过同一 commit → 跳过 fetch+build，直接部署
if [ -n "${LAST_BUILT}" ] && [ "${LAST_BUILT}" = "${LATEST}" ]; then
    echo "commit 无变化(${LATEST})，跳过构建，直接部署"
else
    echo "检测到新提交 ${LATEST}，拉取源码并构建"
    FETCH_OUT=$(bash "${BIN_DIR}/fetch_source.sh" "${GIT_URL}" "${BRANCH}" "${SRC_DIR}" "${DOCKERFILE_PATH}" | tail -1)
    TAG="${FETCH_OUT:-${LATEST}}"
    bash "${BIN_DIR}/build.sh" "${IMAGE}" "${TAG}" "${SRC_DIR}" "${WORKER_HOST}"
    echo "${TAG}" > "${STATE_FILE}"
fi

# 3. 部署（deploy.sh 内部幂等：资源已存在且镜像一致则跳过）
bash "${BIN_DIR}/deploy.sh" "${NAMESPACE}" "${DEPLOY_YAML}" "${IMAGE}" "${TAG}"

# 4. stdout 最后一行输出镜像 tag（供后端解析）
echo "${TAG}"

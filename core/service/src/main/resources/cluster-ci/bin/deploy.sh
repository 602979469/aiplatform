#!/bin/bash
# 部署：资源已存在且镜像一致 → 跳过（幂等）；否则 apply/set image + rollout
# 用法: deploy.sh <namespace> <yaml_path> <image> <tag>
set -euo pipefail

NAMESPACE="$1"
YAML="$2"
IMAGE="$3"
TAG="$4"

DEPLOY_NAME=$(grep -m1 "^  name:" "${YAML}" | awk "{print \$2}")
CONTAINER_NAME=$(awk "/^[[:space:]]*containers:/{found=1} found && /^[[:space:]]*- name:/{print \$3; exit}" "${YAML}")
FULL_IMAGE="${IMAGE}:${TAG}"

# 资源已存在且镜像一致 → 跳过
CURRENT_IMAGE=$(kubectl get deployment "${DEPLOY_NAME}" -n "${NAMESPACE}" \
    -o jsonpath='{.spec.template.spec.containers[0].image}' 2>/dev/null || true)
if [ -n "${CURRENT_IMAGE}" ]; then
    if [ "${CURRENT_IMAGE}" = "${FULL_IMAGE}" ]; then
        echo "[SKIP] deployment/${DEPLOY_NAME} 已是最新镜像 ${FULL_IMAGE}，跳过部署"
        exit 0
    fi
    echo "deployment/${DEPLOY_NAME} 已存在，更新镜像 -> ${FULL_IMAGE}"
else
    echo "deployment/${DEPLOY_NAME} 不存在，创建并部署 ${FULL_IMAGE}"
    kubectl apply -f "${YAML}"
fi

kubectl set image "deployment/${DEPLOY_NAME}" -n "${NAMESPACE}" "${CONTAINER_NAME}=${FULL_IMAGE}"
kubectl rollout status "deployment/${DEPLOY_NAME}" -n "${NAMESPACE}" --timeout=300s
echo "[OK] deployment/${DEPLOY_NAME} 就绪 image=${FULL_IMAGE}"

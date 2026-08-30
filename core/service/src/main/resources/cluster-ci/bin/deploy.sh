#!/bin/bash
# 部署：总是 apply 用户配置的 yaml（幂等覆盖，Deployment/Service/Ingress 变更随配置生效），
# 再 set image 到构建 tag + rollout 滚动更新；资源不存在时自动创建
# 用法: deploy.sh <namespace> <yaml_path> <image> <tag>
set -euo pipefail

NAMESPACE="$1"
YAML="$2"
IMAGE="$3"
TAG="$4"

DEPLOY_NAME=$(grep -m1 "^  name:" "${YAML}" | awk "{print \$2}")
CONTAINER_NAME=$(awk "/^[[:space:]]*containers:/{found=1} found && /^[[:space:]]*- name:/{print \$3; exit}" "${YAML}")
FULL_IMAGE="${IMAGE}:${TAG}"

kubectl apply -f "${YAML}"
kubectl set image "deployment/${DEPLOY_NAME}" -n "${NAMESPACE}" "${CONTAINER_NAME}=${FULL_IMAGE}"
kubectl rollout status "deployment/${DEPLOY_NAME}" -n "${NAMESPACE}" --timeout=300s
echo "[OK] deployment/${DEPLOY_NAME} 就绪 image=${FULL_IMAGE}"

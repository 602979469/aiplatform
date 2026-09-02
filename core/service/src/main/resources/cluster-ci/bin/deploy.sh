#!/bin/bash
# 部署：从 Harbor 拉镜像（各节点按架构拉多架构 manifest，containerd 已配 harbor hosts）→ apply + set image + rollout
# 用法: deploy.sh <namespace> <yaml_path> <harbor_ref>
set -euo pipefail

NAMESPACE="$1"
YAML="$2"
HARBOR_REF="$3"

DEPLOY_NAME=$(grep -m1 "^  name:" "${YAML}" | awk "{print \$2}")
CONTAINER_NAME=$(awk "/^[[:space:]]*containers:/{found=1} found && /^[[:space:]]*- name:/{print \$3; exit}" "${YAML}")

kubectl apply -f "${YAML}"
kubectl set image "deployment/${DEPLOY_NAME}" -n "${NAMESPACE}" "${CONTAINER_NAME}=${HARBOR_REF}"
kubectl rollout status "deployment/${DEPLOY_NAME}" -n "${NAMESPACE}" --timeout=300s
echo "[OK] deployment/${DEPLOY_NAME} 就绪 image=${HARBOR_REF}"

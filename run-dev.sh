#!/usr/bin/env bash
# 本地开发启动脚本（不依赖重新打包）
#
# 背景：application-dev.yml 只存在于源码目录（已被 gitignore，不进仓库），
# 如果它是在 mvn package 之后才创建的，就不会被打进 jar，
# 于是 java -jar 启动时 ${MYSQL_HOST} 之类的占位符解析失败。
# 这里用 spring.config.additional-location 直接从源码目录加载 dev 配置，
# 无论 jar 是什么时候打的都能生效。
#
# 用法：
#   ./run-dev.sh                      # 默认 8080
#   ./run-dev.sh --server.port=18080  # 自定义端口/其他 spring 参数
set -euo pipefail
cd "$(dirname "$0")"

JAR=$(ls bootstrap/target/aiplatform-bootstrap-*.jar 2>/dev/null | head -1)
if [ -z "${JAR}" ]; then
  echo "找不到 jar，请先构建：mvn -o -DskipTests package" >&2
  exit 1
fi

exec java -jar "${JAR}" \
  --spring.profiles.active=dev \
  --spring.config.additional-location=file:./bootstrap/src/main/resources/ \
  "$@"

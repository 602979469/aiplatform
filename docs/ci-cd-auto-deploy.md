# Git Push → K8s 自动构建部署（CI 流水线）操作文档

> 适用仓库：aiplatform（Spring Boot / SOFABoot 多模块工程，Java 17）
> 目标：本地 `git push` 到 `ai` 分支后，虚拟机上的流水线自动完成「拉代码 → 构建 jar → 打镜像 → 导入 containerd → 初始化数据库 → 注入密钥 → 部署到 k8s」，全程无需人工登录服务器。

---

## 1. 背景

项目早期部署靠人工操作：本地打包、scp 上传、登录服务器手动 build/apply，步骤多且容易漏（例如：虚拟机上没有 `DEEPSEEK_API_KEY` 环境变量导致 AI 对话功能不可用；SQL 导入字符集不对导致中文乱码）。

这套流水线解决三个问题：

1. **提交即部署**：只要往 `ai` 分支推代码，集群自动更新，最短 1~2 分钟内生效。
2. **环境变量集中注入**：密钥通过 k8s Secret 注入（如 `aiplatform-secret` 里的 `DEEPSEEK_API_KEY`），不依赖虚拟机手动 export，也不进 Git。
3. **可复制**：新同事拿到本文档 + 脚本，即可在另一台虚拟机/集群原样搭建。

整体链路：

```text
开发机 git push origin ai
        │
        ▼
GitHub（api.github.com 查询最新 commit / codeload 下载源码包）
        │
        ▼
虚拟机 cron（每 2 分钟跑一次 pipeline.sh，flock 防并发）
        │
        ├─ 检测到新 commit？
        │    否 → 退出（不重复构建）
        │    是 → 下载源码 → mvn package → docker build → 导入 containerd
        │
        ├─ 数据库幂等初始化（表不存在才导入 sql/*.sql）
        ├─ 创建/更新 k8s Secret（aiplatform-secret，读本地 secrets.env）
        │
        └─ kubectl apply deploy/aiplatform.yaml（替换镜像 tag 为 commit 短哈希）
              → kubectl rollout status 等待就绪
```

---

## 2. 名词解释

| 名词 | 说明 |
|---|---|
| `ai` 分支 | 流水线轮询的分支名（在 `pipeline.sh` 里用 `BRANCH` 变量配置）。推这个分支才会触发构建。 |
| `pipeline.sh` | 虚拟机上的核心脚本，由 cron 每 2 分钟调用一次。 |
| `state` 文件 | 记录「上次已经构建过的 commit 完整哈希」。commit 没变化时脚本直接退出，避免每 2 分钟重复构建。 |
| `codeload` | GitHub 提供源码压缩包下载的域名（`codeload.github.com`），比 `git clone` 更稳定，且支持任意 commit。 |
| `ctr -n k8s.io image import` | 把 Docker 镜像导入 containerd。本集群的 k8s 运行时是 containerd，**不是 Docker**，所以 `docker images` 里看不到 k8s 正在用的镜像。 |
| Deployment / StatefulSet | k8s 工作负载：Deployment 管无状态应用（aiplatform），StatefulSet 管有状态服务（MySQL）。 |
| Secret / ConfigMap | k8s 配置对象：Secret 存敏感信息（密码、API Key），ConfigMap 存普通配置（如 MySQL 字符集）。 |
| `aiplatform-secret` | 保存 `DEEPSEEK_API_KEY` 的 Secret，Deployment 通过 `valueFrom.secretKeyRef` 注入为环境变量。 |
| `optional: true` | Secret 不存在时环境变量不注入、Pod 照常启动；存在则注入。保证「没配 Key 也能部署，只是 AI 对话不可用」。 |
| NodePort / Ingress | 对外暴露服务的方式：NodePort 用 `节点IP:30081` 访问；Ingress 用域名（如 `aiplatform.com`）访问。 |
| `flock` | 文件锁，防止 cron 密集触发时多个脚本同时跑（首次构建耗时可能超过 2 分钟）。 |
| 短哈希 | commit 前 7 位，用作镜像 tag（如 `aiplatform:6845081`）。 |

---

## 3. 环境

### 3.1 开发机（你写代码的机器）

- 操作系统：macOS / Linux / Windows 均可，需要 Git。
- Java 17 + Maven 3.8+（本地编译验证用，流水线在虚拟机里也各装了一份）。
- GitHub 账号，能访问目标仓库；推送用 HTTPS + Personal Access Token（PAT）或 SSH。

### 3.2 虚拟机（跑流水线和 k8s）

本项目实际环境（可参考，不必完全一致）：

| 项 | 值 |
|---|---|
| 虚拟机 | Multipass，实例名 `k8s-master`，Ubuntu 24.04.4 LTS |
| 配置 | 2 CPU / 96.8 GiB 磁盘（IP `192.168.252.20`） |
| Java | OpenJDK 17.0.19 |
| Maven | Apache Maven 3.8.7 |
| Docker | 29.7.0（仅用于 build/保存镜像，k8s 运行时是 containerd） |
| kubectl | v1.29.15 |
| k8s 集群 | v1.29.15 单控制面节点，containerd 2.2.6，网络插件 flannel，入口 ingress-nginx |

### 3.3 集群内的基础服务（命名空间 `tsk`）

| 服务 | 说明 |
|---|---|
| `mysql-0`（StatefulSet） | MySQL 8.0，供 aiplatform 使用，服务名 `mysql.tsk.svc.cluster.local` |
| `ruoyi-redis` | Redis 6/7，服务名 `ruoyi-redis.tsk.svc.cluster.local`（沿用 RuoYi 命名） |
| `aiplatform`（Deployment） | 业务应用，NodePort `30081`，Ingress 域名 `aiplatform.com` |
| Secret `mysql-secret` | MySQL root 密码（key：`root-password`） |
| ConfigMap `mysql-config` | MySQL 服务端配置（utf8mb4、时区、lower_case_table_names） |

### 3.4 网络说明

- `github.com` 主站（git 协议/网页）在本网络环境不稳定，所以流水线**不用 git clone**，改用 `api.github.com` 查 commit + `codeload.github.com` 下源码包。
- 若目标仓库是**私有仓库**，上述两个 API 请求需要带 PAT（见第 5 节注意事项）。
- 流水线从虚拟机访问 GitHub 需要外网可达；访问内网 k8s 用本机 `kubectl` 即可。

---

## 4. 实操

### 4.1 开发机：本地仓库与推送

```bash
# 1. 克隆仓库（以 https 为例；私有仓库用 PAT，见注意事项）
git clone https://github.com/<你的账号>/aiplatform.git
cd aiplatform

# 2. 切到流水线监听的分支（没有就新建）
git checkout -b ai

# 3. 日常流程：改代码 → 提交 → 推送
git add .
git commit -m "feat: xxx"
git push origin ai
```

> HTTPS 推送时 GitHub 已不支持账号密码，需要 PAT：
>
> ```bash
> git push https://<用户名>:<PAT>@github.com/<你的账号>/aiplatform.git ai
> ```
>
> 或把 PAT 配置进凭据管理器（`git config --global credential.helper osxkeychain` 后第一次输入一次即可）。

### 4.2 虚拟机：准备运行环境

如果还没有虚拟机，先创建并安装依赖（已有可跳过）：

```bash
# 创建 Ubuntu 虚拟机（macOS 上 Multipass）
multipass launch --name k8s-master --cpus 2 --disk 100G --memory 4G ubuntu-24.04
multipass shell k8s-master

# 虚拟机内：安装基础依赖
sudo apt update && sudo apt install -y git curl tar

# Java 17（示例用 OpenJDK）
sudo apt install -y openjdk-17-jdk
java -version

# Maven 3.8+
sudo apt install -y maven
mvn -version

# Docker（仅用于构建镜像；k8s 运行时是 containerd，两者互不影响）
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
docker --version

# kubectl（版本建议与集群一致）
curl -LO "https://dl.k8s.io/release/v1.29.15/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

确认 `kubectl` 能连集群（单节点集群的 kubeconfig 通常已在 `/root/.kube/config` 或 `/home/ubuntu/.kube/config`）：

```bash
kubectl get nodes
```

> **关键**：`pipeline.sh` 由 cron 以当前用户执行，该用户必须能直接跑 `kubectl`（把 kubeconfig 放到 `~/.kube/config`）。

### 4.3 集群：命名空间与基础服务

以下清单是「能跑通的最小集合」，已有资源的集群直接跳过对应步骤。

**① 命名空间 + MySQL Secret + ConfigMap + StatefulSet + Service**（保存为 `mysql.yaml`）：

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: tsk
---
apiVersion: v1
kind: Secret
metadata:
  name: mysql-secret
  namespace: tsk
type: Opaque
stringData:
  # 改成你自己的 MySQL root 密码
  root-password: "YourRootPassword123"
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: mysql-config
  namespace: tsk
data:
  my.cnf: |
    [mysqld]
    character-set-server=utf8mb4
    collation-server=utf8mb4_unicode_ci
    default-time-zone=+8:00
    lower_case_table_names=1
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mysql
  namespace: tsk
spec:
  serviceName: mysql
  replicas: 1
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
        - name: mysql
          image: mysql:8.0
          env:
            - name: MYSQL_ROOT_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: root-password
          ports:
            - containerPort: 3306
              name: mysql
          readinessProbe:
            exec:
              command: ["sh", "-c", "mysqladmin ping -h localhost"]
            initialDelaySeconds: 5
            periodSeconds: 5
          volumeMounts:
            - name: mysql-config
              mountPath: /etc/mysql/conf.d/my.cnf
              subPath: my.cnf
            - name: mysql-data
              mountPath: /var/lib/mysql
  volumeClaimTemplates:
    - metadata:
        name: mysql-data
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 5Gi
---
apiVersion: v1
kind: Service
metadata:
  name: mysql
  namespace: tsk
spec:
  selector:
    app: mysql
  ports:
    - port: 3306
      targetPort: 3306
      name: mysql
```

**② Redis**（`redis.yaml`，服务名必须与应用配置一致：`ruoyi-redis.tsk.svc.cluster.local`）：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ruoyi-redis
  namespace: tsk
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ruoyi-redis
  template:
    metadata:
      labels:
        app: ruoyi-redis
    spec:
      containers:
        - name: redis
          image: redis:7
          ports:
            - containerPort: 6379
---
apiVersion: v1
kind: Service
metadata:
  name: ruoyi-redis
  namespace: tsk
spec:
  selector:
    app: ruoyi-redis
  ports:
    - port: 6379
      targetPort: 6379
```

**③ 应用部署 + Service + Ingress**（仓库里的 `deploy/aiplatform.yaml`，见 4.5，直接 `kubectl apply` 即可，注意镜像 tag 用 `aiplatform:latest` 占位，流水线会替换）。

**④ ingress-nginx**（已有则跳过；用 Helm 安装最省事）：

```bash
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm install ingress-nginx ingress-nginx/ingress-nginx -n ingress-nginx --create-namespace
```

依次应用：

```bash
kubectl apply -f mysql.yaml
kubectl apply -f redis.yaml
kubectl apply -f deploy/aiplatform.yaml   # 在仓库根目录执行
```

### 4.4 流水线：脚本 + secrets + cron（核心）

**① 创建目录与密钥文件**

```bash
mkdir -p /home/ubuntu/aiplatform-ci

# 密钥文件：一行一个环境变量，权限必须收紧，内容不进 Git
umask 077
cat > /home/ubuntu/aiplatform-ci/secrets.env <<'EOF'
DEEPSEEK_API_KEY=sk-你的DeepSeekKey
EOF
chmod 600 /home/ubuntu/aiplatform-ci/secrets.env
```

> `pipeline.sh` 会在每次部署前用这个文件创建/更新 k8s Secret `aiplatform-secret`，应用容器通过环境变量 `DEEPSEEK_API_KEY` 读取（对应 `application.yml` 的 `${DEEPSEEK_API_KEY:}`）。密钥不落仓库、不打印日志。

**② 粘贴流水线脚本**（保存为 `/home/ubuntu/aiplatform-ci/pipeline.sh`，整个文件直接复制）：

```bash
#!/bin/bash
# aiplatform 本地流水线：轮询 origin/ai，检测到新提交则构建镜像并部署到 tsk
set -euo pipefail

export PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
export MAVEN_OPTS="-Xmx1024m"

# ===== 按你的环境修改以下变量 =====
BRANCH="ai"                                   # 监听的分支
REMOTE_URL="https://github.com/602979469/aiplatform.git"  # 仅作参考，实际用 API 查询
BASE_DIR="/home/ubuntu/aiplatform-ci"          # 流水线工作目录
NAMESPACE="tsk"                                # k8s 命名空间
IMAGE_NAME="aiplatform"                        # 镜像名
DEPLOY_FILE="${BASE_DIR}/src/deploy/aiplatform.yaml"   # 仓库里的部署清单
MYSQL_POD="mysql-0"                            # MySQL StatefulSet 的 Pod 名
SECRETS_FILE="${BASE_DIR}/secrets.env"         # 本机密钥文件（不落仓库）
# ===================================

LOCK_FILE="${BASE_DIR}/pipeline.lock"
REPO_DIR="${BASE_DIR}/src"
STATE_FILE="${BASE_DIR}/state"
LOG_FILE="${BASE_DIR}/pipeline.log"

# 打日志（中文日志，UTF-8 环境无问题）
log() { echo "[$(date '+%F %T')] $*" >> "${LOG_FILE}"; }

# 防并发锁：同一时间只允许一个实例运行（首次构建耗时长，cron 会密集触发）
exec 9>"${LOCK_FILE}"
flock -n 9 || exit 0

# 1. 获取远程分支最新 commit
#    本地网络对 github.com 主站间歇性超时，因此用 API 查询 + codeload 下载源码包，绕开 git 协议
LATEST=$(curl -fsS --max-time 20 \
    "https://api.github.com/repos/602979469/aiplatform/commits/${BRANCH}" \
    -o /tmp/aiplatform-commit.json 2>/dev/null \
    && grep -m1 '"sha"' /tmp/aiplatform-commit.json | sed -E 's/.*"sha": "([a-f0-9]+)".*/\1/') \
    || { log "获取远程 commit 失败"; exit 1; }
rm -f /tmp/aiplatform-commit.json

# 2. 与上次构建的 commit 对比，没变化就退出（避免每 2 分钟重复构建）
LAST_BUILT=$(cat "${STATE_FILE}" 2>/dev/null || true)
if [ -n "${LAST_BUILT}" ] && [ "${LATEST}" = "${LAST_BUILT}" ]; then
    exit 0
fi

SHORT=$(echo "${LATEST}" | cut -c1-7)
log "检测到新提交 ${SHORT}，开始构建部署"

# 3. 下载源码快照并解压
rm -rf "${REPO_DIR}"
mkdir -p "${REPO_DIR}"
curl -fsSL --max-time 120 \
    "https://codeload.github.com/602979469/aiplatform/tar.gz/${LATEST}" \
    -o /tmp/aiplatform-src.tar.gz || { log "下载源码失败"; exit 1; }
tar -xzf /tmp/aiplatform-src.tar.gz --strip-components=1 -C "${REPO_DIR}"
rm -f /tmp/aiplatform-src.tar.gz

# 4. 构建 jar
(cd "${REPO_DIR}" && mvn -q -Dmaven.test.skip=true package) || { log "Maven 构建失败"; exit 1; }

# 5. 确保基础镜像存在（docker.io 不可达时回退镜像站）
if ! docker image inspect eclipse-temurin:17-jre >/dev/null 2>&1; then
    if ! docker pull eclipse-temurin:17-jre >/dev/null 2>&1; then
        docker pull docker.xuanyuan.run/library/eclipse-temurin:17-jre >/dev/null || { log "基础镜像拉取失败"; exit 1; }
        docker tag docker.xuanyuan.run/library/eclipse-temurin:17-jre eclipse-temurin:17-jre
    fi
fi

# 6. 构建镜像并导入 containerd（k8s 运行时是 containerd，不是 docker）
docker build -t "${IMAGE_NAME}:${SHORT}" "${REPO_DIR}" || { log "docker build 失败"; exit 1; }
docker save "${IMAGE_NAME}:${SHORT}" -o /tmp/aiplatform-${SHORT}.tar
sudo ctr -n k8s.io image import /tmp/aiplatform-${SHORT}.tar || { log "镜像导入 containerd 失败"; exit 1; }
rm -f /tmp/aiplatform-${SHORT}.tar

# 7. 数据库幂等初始化：以每个 SQL 文件的第一张表是否存在为准，缺失才执行该文件
#    （兼容大写/小写 create table、IF NOT EXISTS 以及带种子数据的多表文件，如 auth_rbac.sql）
#    注意：导入含中文的 SQL 必须带 --default-character-set=utf8mb4，否则 UTF-8 中文会被
#    按 latin1 二次编码成乱码（本项目踩过坑，见第 5 节注意事项）
MYSQL_PW=$(kubectl get secret mysql-secret -n "${NAMESPACE}" -o jsonpath='{.data.root-password}' | base64 -d)
kubectl exec -n "${NAMESPACE}" "${MYSQL_POD}" -- mysql -uroot -p"${MYSQL_PW}" -e \
    "CREATE DATABASE IF NOT EXISTS aiplatform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
for f in "${REPO_DIR}"/sql/*.sql; do
    TABLE=$(sed -nE 's/^[[:space:]]*CREATE TABLE (IF NOT EXISTS )?`?([A-Za-z0-9_]+).*/\2/ip' "${f}" | head -1)
    if [ -z "${TABLE}" ]; then
        log "SQL 文件无法识别建表语句，跳过: $(basename "${f}")"
        continue
    fi
    COUNT=$(kubectl exec -n "${NAMESPACE}" "${MYSQL_POD}" -- mysql -N -uroot -p"${MYSQL_PW}" \
        -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='aiplatform' AND table_name='${TABLE}'" | tr -d '[:space:]')
    if [ "${COUNT}" = "0" ]; then
        kubectl exec -i -n "${NAMESPACE}" "${MYSQL_POD}" -- mysql --default-character-set=utf8mb4 \
            -uroot -p"${MYSQL_PW}" aiplatform < "${f}"
        log "数据库初始化: $(basename "${f}")"
    fi
done

# 8. 密钥注入：优先读本机 secrets.env，创建/更新 k8s Secret aiplatform-secret
#    应用侧 application.yml 通过环境变量 DEEPSEEK_API_KEY 读取（${DEEPSEEK_API_KEY:}），无需改 Java
if [ -f "${SECRETS_FILE}" ]; then
    if kubectl create secret generic aiplatform-secret -n "${NAMESPACE}" \
        --from-env-file="${SECRETS_FILE}" --dry-run=client -o yaml | kubectl apply -f - >/dev/null; then
        log "DeepSeek API Key 已注入 secret: aiplatform-secret（来源: secrets.env）"
    else
        log "警告: aiplatform-secret 创建失败，AI 对话功能将不可用"
    fi
elif [ -n "${DEEPSEEK_API_KEY:-}" ]; then
    kubectl create secret generic aiplatform-secret -n "${NAMESPACE}" \
        --from-literal=DEEPSEEK_API_KEY="${DEEPSEEK_API_KEY}" \
        --dry-run=client -o yaml | kubectl apply -f - >/dev/null
    log "DeepSeek API Key 已注入 secret: aiplatform-secret（来源: 环境变量）"
else
    log "警告: 未找到 ${SECRETS_FILE} 且 DEEPSEEK_API_KEY 为空，AI 对话功能将不可用（应用仍可部署）"
fi

# 9. 应用部署（把清单里的 image: aiplatform:latest 占位符替换成实际镜像 tag）
sed "s|image: aiplatform:latest|image: ${IMAGE_NAME}:${SHORT}|" "${DEPLOY_FILE}" > /tmp/aiplatform-deploy.yaml
kubectl apply -f /tmp/aiplatform-deploy.yaml
kubectl rollout status deployment/aiplatform -n "${NAMESPACE}" --timeout=180s

# 10. 记录本次构建的 commit，供下次对比
echo "${LATEST}" > "${STATE_FILE}"
log "部署完成: ${IMAGE_NAME}:${SHORT}"
```

**③ 给脚本加执行权限 + 语法检查 + 注册 cron**

```bash
chmod +x /home/ubuntu/aiplatform-ci/pipeline.sh
bash -n /home/ubuntu/aiplatform-ci/pipeline.sh    # 语法检查，无输出即通过

# 添加 cron：每 2 分钟跑一次，输出追加到 cron.log（方便排查）
( crontab -l 2>/dev/null; echo "*/2 * * * * /home/ubuntu/aiplatform-ci/pipeline.sh >> /home/ubuntu/aiplatform-ci/cron.log 2>&1" ) | crontab -
crontab -l
```

**④ 手动触发一次构建（验证全链路）**

```bash
# 手动跑一次（会正常构建并部署；也可以先 push 一个 commit 等 cron 自动触发）
bash /home/ubuntu/aiplatform-ci/pipeline.sh
tail -20 /home/ubuntu/aiplatform-ci/pipeline.log
```

> 第一次构建要下载依赖和基础镜像，耗时较长（几分钟），期间 cron 每 2 分钟触发的其他实例会被 `flock` 挡住直接退出，不会重复构建。

### 4.5 仓库：部署清单 `deploy/aiplatform.yaml`

这个文件必须在仓库里（流水线每次从下载的源码中读取它）。核心要点：

- `image: aiplatform:latest` 是**占位符**，流水线用 `sed` 替换成 `aiplatform:<commit短哈希>`，不要改成别的值；
- 数据库/Redis 地址按集群实际服务名配置；
- `DEEPSEEK_API_KEY` 通过 `valueFrom.secretKeyRef` 从 `aiplatform-secret` 注入，`optional: true` 表示 secret 缺失时应用也能启动（只是 AI 对话不可用）。

完整内容（按需修改环境变量）：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: aiplatform
  namespace: tsk
  labels:
    app: aiplatform
spec:
  replicas: 1
  selector:
    matchLabels:
      app: aiplatform
  template:
    metadata:
      labels:
        app: aiplatform
    spec:
      containers:
        - name: aiplatform
          # 流水线会替换为 aiplatform:<commit 短哈希>
          image: aiplatform:latest
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
              name: http
          env:
            - name: MYSQL_HOST
              value: mysql.tsk.svc.cluster.local
            - name: MYSQL_PORT
              value: "3306"
            - name: MYSQL_USERNAME
              value: root
            - name: MYSQL_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: root-password
            - name: REDIS_HOST
              value: ruoyi-redis.tsk.svc.cluster.local
            - name: REDIS_PORT
              value: "6379"
            - name: REDIS_PASSWORD
              value: ""
            - name: AIPLATFORM_AVATAR_DIR
              value: /data/uploads
            - name: DEEPSEEK_API_KEY
              valueFrom:
                secretKeyRef:
                  name: aiplatform-secret
                  key: DEEPSEEK_API_KEY
                  optional: true
          volumeMounts:
            - name: uploads
              mountPath: /data/uploads
          resources:
            requests:
              cpu: 250m
              memory: 512Mi
            limits:
              cpu: "1"
              memory: 1Gi
          livenessProbe:
            tcpSocket:
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 10
          readinessProbe:
            tcpSocket:
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 5
      volumes:
        - name: uploads
          hostPath:
            path: /data/aiplatform-uploads
            type: DirectoryOrCreate
---
apiVersion: v1
kind: Service
metadata:
  name: aiplatform
  namespace: tsk
  labels:
    app: aiplatform
spec:
  type: NodePort
  ports:
    - port: 8080
      targetPort: 8080
      nodePort: 30081
      protocol: TCP
      name: http
  selector:
    app: aiplatform
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: aiplatform
  namespace: tsk
spec:
  ingressClassName: nginx
  rules:
    - host: aiplatform.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: aiplatform
                port:
                  number: 8080
```

> 部署文件在仓库里，所以**每次修改部署配置也要走「提交 → push → 流水线」**，改动会随下一个 commit 一起生效。

### 4.6 日常使用与验证

**日常发布**（开发机上）：

```bash
git add .
git commit -m "更新内容"
git push origin ai
```

约 1~2 分钟内（cron 周期 2 分钟 + 构建时间）自动部署完成。

**验证是否生效**（虚拟机或开发机配好 kubeconfig 后）：

```bash
# 1. 看流水线日志
tail -20 /home/ubuntu/aiplatform-ci/pipeline.log

# 2. 看 Pod 状态（READY 1/1 表示就绪）
kubectl get pods -n tsk -o wide

# 3. 看新 Pod 是否带上了环境变量（确认密钥注入）
kubectl exec -n tsk deploy/aiplatform -- env | grep DEEPSEEK

# 4. 访问验证（二选一）
curl http://192.168.252.20:30081/          # NodePort
curl http://aiplatform.com/                # Ingress（需要 DNS 指向 192.168.252.20）
```

### 4.7 常见问题排查

| 现象 | 排查 |
|---|---|
| push 了但没有新部署 | 看 `pipeline.log` 有没有「检测到新提交」；看 `state` 文件里的 commit 是否已经是最新；确认 push 的是 `ai` 分支 |
| 构建一直不成功 | 看 `cron.log`（构建输出）和 `pipeline.log`（阶段日志），常见为 Maven 依赖下载慢/失败、GitHub 网络超时 |
| Pod 一直 ImagePullBackOff | 大概率是镜像没有导入 containerd：`sudo ctr -n k8s.io images ls \| grep aiplatform` 确认有没有 `<短哈希>` 的镜像 |
| Pod 起不来 / CrashLoopBackOff | `kubectl logs -n tsk deploy/aiplatform --tail=50`；检查数据库/Redis 是否就绪、`mysql-secret` 是否存在 |
| AI 对话报「API Key 未配置」 | 确认 `/home/ubuntu/aiplatform-ci/secrets.env` 存在且内容正确；确认 `kubectl get secret aiplatform-secret -n tsk` 存在；重启触发一次部署 |
| 想强制重新构建 | 删除 `state` 文件（或 push 一个空 commit）：`rm /home/ubuntu/aiplatform-ci/state`，下次 cron 会重新构建 |

---

## 5. 注意事项

1. **密钥安全**
   - `secrets.env`、任何 `.env`、PAT 一律**不进 Git**（仓库 `.gitignore` 里加好）。
   - `secrets.env` 权限设为 `600`，流水线日志里也不打印密钥内容。
   - 密钥轮换：改 `secrets.env` → 触发一次部署（新 commit 或 `kubectl rollout restart deployment/aiplatform -n tsk`）→ 用 `kubectl get secret aiplatform-secret -n tsk` 确认已更新。

2. **SQL 导入字符集（本项目踩过的坑）**
   - 含中文的 SQL 文件必须用 `mysql --default-character-set=utf8mb4` 导入。如果漏掉，MySQL 客户端按 latin1 解析 UTF-8 字节，会把「AI 应用」二次编码成 `AI åº"ç"¨` 这类乱码存进库，Java 端怎么配 utf8 读出来都是乱的。
   - 本机直连数据库时同理：`mysql -u root -p --default-character-set=utf8mb4`，JDBC 连接串带 `characterEncoding=utf8`。

3. **k8s 镜像在 containerd，不在 docker**
   - `docker images` 看不到集群实际使用的镜像；查镜像用 `sudo ctr -n k8s.io images ls`。
   - 手动 `kubectl apply` 部署清单前，镜像必须已导入 containerd。所以日常部署一律走流水线，别手动改 Deployment 的镜像 tag（下次流水线会覆盖）。

4. **部署清单的 `image: aiplatform:latest` 是占位符**
   - 流水线靠 `sed "s|image: aiplatform:latest|image: ${IMAGE_NAME}:${SHORT}|"` 替换 tag。删掉或改名会导致部署失败或回滚成旧镜像。

5. **GitHub 网络与私有仓库**
   - 流水线依赖 `api.github.com` 和 `codeload.github.com`。公网仓库无需认证；**私有仓库**需要给两个请求加 PAT：`curl -H "Authorization: Bearer <PAT>"`（API）与 `curl -H "Authorization: token <PAT>"`（codeload），把 PAT 放在 `secrets.env` 里读取，不要写死在脚本中。
   - `api.github.com` 有速率限制（未认证 60 次/小时），cron 每 2 分钟一次没问题，但手动频繁调试注意限流。

6. **cron 与并发**
   - cron 周期建议 ≥1 分钟；脚本内 `flock` 保证同一时刻只有一个实例在跑。
   - `state` 文件是去重依据，误删会触发一次完整重建（无害，只是耗时）。

7. **数据库初始化是幂等的**
   - 以「表是否存在」判断是否导入，已存在的表不会被覆盖。想重新初始化某个表，先手动 `DROP TABLE` 或清库。
   - 数据库日常备份建议另加 cron：`kubectl exec -n tsk mysql-0 -- mysqldump -uroot -p<密码> aiplatform > backup_$(date +%F).sql`。

8. **时区与编码**
   - 应用容器 `-Duser.timezone=Asia/Shanghai`，MySQL `default-time-zone=+8:00`，保持一致避免时间差 8 小时。
   - 虚拟机 locale 建议 UTF-8，中文日志/文件名才不会乱。

9. **域名与端口**
   - Ingress 域名（示例 `aiplatform.com`）需要本地 DNS/hosts 指向节点 IP，或用 NodePort `30081` 直连测试。
   - 应用暴露的端口、NodePort、Ingress 规则按实际环境调整，改完记得提交 deploy 文件走流水线。

10. **首次搭建建议顺序**
    先手动 `kubectl apply` 基础服务（MySQL/Redis/Deployment）跑通应用 → 再上流水线（4.4）→ 最后日常 push。别一上来就全自动化，问题会被「构建 + 部署」混在一起，不好定位。

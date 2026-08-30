# cluster-ci 脚本托管与 aiplatform 部署前置准备

> 现状说明：aiplatform 通过 SSH 操作集群节点，cluster-ci 脚本不再手工维护在服务器上，
> 而是以 Java 资源形式随代码发布，调度前自动比对/同步到 master。

## 1. 脚本托管与同步机制

### 1.1 脚本存放位置

- **Java 侧（唯一事实来源）**：`core/service/src/main/resources/cluster-ci/bin/*.sh`
- **远端**：master 的 `<work-dir>/bin/`（默认 `/home/ubuntu/cluster-ci/bin/`）

当前脚本清单：

| 脚本 | 作用 |
|---|---|
| `pipeline.sh` | 统一编排：lock + commit 比对，决定拉码/构建/部署，统一 tee 日志 |
| `fetch_source.sh` | git 浅克隆拉源码，stdout 输出 commit 短哈希 |
| `build.sh` | master + worker 双架构构建导入 |
| `build_import.sh` | 单节点 docker build → save → ctr import（含节点级 flock） |
| `deploy.sh` | 幂等部署：镜像一致跳过，否则 apply / set image / rollout |

### 1.2 同步规则

脚本同步在 **aiplatform 启动时**执行一次（`ClusterScriptStartupRunner`，`ApplicationRunner`），
失败直接阻塞启动（SSH 连不上说明环境未准备好，尽早暴露）。部署业务 pod 时**不再检查**：

同步逻辑（与部署解耦）：

1. 遍历 classpath 下 `cluster-ci/bin/*.sh`（新增 `.sh` 自动纳入，无需改代码）；
2. 计算本地脚本 SHA-256；
3. SSH 远端执行 `sha256sum <work-dir>/bin/<name>`：
   - 远端文件不存在 → 上传写入（新服务器无需手工建 bin）；
   - hash 不一致（push 了 bugfix 等变更）→ 上传覆盖；
   - hash 一致 → 跳过。

### 1.3 新增/修改脚本的流程

1. 修改或新增 `core/service/src/main/resources/cluster-ci/bin/` 下的脚本；
2. 重新构建部署 aiplatform（重启即触发启动同步）；
3. 启动时自动同步到 master（缺失/变更才写）。

> **临时调试**：允许直接在服务器上改 `bin/*.sh` 调试，Java 不会在部署业务 pod 时覆盖；
> 调试完成后把改动同步回 Java 资源，下次重启 aiplatform 时由启动同步覆盖远端。

> 远端 `bin/*.bak.*` 备份文件不会被清理（只管理资源清单内的 `.sh`）；并发锁文件在
> `<work-dir>/locks/`（`build.lock`、`pipeline-<appId>.lock`），由脚本运行时自动维护。

## 2. aiplatform k8s 部署前置准备清单

aiplatform 依赖集群外部能力（SSH 到节点、K8s API、MySQL/Redis/MinIO），
`deploy/aiplatform.yaml` 部署完毕后，需要以下前置条件才能正常工作：

### 2.1 节点与目录

- 节点：master `192.168.3.131`（k8s-master，amd64）、worker `192.168.3.217`（arm64）；
- master 上创建 `~/cluster-ci/`（apps、locks），属主 `ubuntu`；`bin/` 由 Java 首次部署自动创建；
- master → worker 免密 SSH（`ubuntu@192.168.3.217`，build.sh 同步源码用）；
- master、worker 均需 docker + containerd（`ctr -n k8s.io`）可用，`ubuntu` 有 `sudo ctr` 权限。

### 2.2 Secret（tsk 命名空间）

| Secret | 用途 | 对应 yaml |
|---|---|---|
| `aiplatform-ssh-key` | pod 内 `/root/.ssh/id_rsa`（SSH 到 master/worker） | volumeMounts `ssh-key` |
| `mysql-secret` | `MYSQL_PASSWORD`（root-password） | env |
| `redis-secret` | `REDIS_PASSWORD` | env |
| `minio-credentials` | `MINIO_ENDPOINT/ACCESS_KEY/SECRET_KEY/BUCKET` | env |
| `deepseek-api-key` | DeepSeek Key（可选，`optional: true`） | env |

### 2.3 RBAC

- 应用 `deploy/aiplatform-cluster-rbac.yaml`（ClusterRoleBinding `aiplatform-cluster-admin`，
  绑定 default SA），否则 K8s 客户端（k8s-client）查询/操作集群资源会 403。

### 2.4 MySQL 初始化（aiplatform 库）

- `sql/cluster_pod_config.sql`（业务 pod 配置表，含状态机字段）；
- `sql/file_info.sql`（文件元数据表，内容存 MinIO）；
- 菜单 SQL（`cluster_menu.sql`，幂等）——集群管理菜单；
- 已有旧数据时执行 `sql/cluster_pod_config_migration.sql`。

### 2.5 环境变量（deploy/aiplatform.yaml 已配）

- `SSH_USERNAME=ubuntu`、`SSH_PRIVATE_KEY_PATH=/root/.ssh/id_rsa`；
- `CLUSTER_CI_WORK_DIR=/home/ubuntu/cluster-ci`、`CLUSTER_CI_MASTER_HOST=192.168.3.131`、
  `CLUSTER_CI_WORKER_HOST=192.168.3.217`；
- 访问入口：NodePort `30081` + Ingress `www.jakt.online/prod-api`。

### 2.6 aiplatform 自身部署方式

- 保持不变：仍由 `~/aiplatform-ci/pipeline.sh`（cron 每 2 分钟）构建部署到 tsk；
- 集群管理的其余脚本（cluster-ci/bin）全部由 Java 同步机制管理，不再手工维护。

## 3. 排障速查

- 部署报"脚本同步失败" → 检查 master SSH 连通、`<work-dir>` 可写（ubuntu 属主）；
- 报"构建部署失败: pipeline.sh: line xx: 参数未绑定" → 确认远端 `bin/*.sh` 与资源 hash 一致
  （删掉远端对应文件后重试部署，会自动重新同步）；
- 日志缺失 → 看 `<appId>/deploy-*.log`（pipeline 统一 tee，覆盖拉码/构建/部署全阶段）。

## 4. 配置项整理（默认值收敛到 application-dev.yml）

### 4.1 原则

- `application.yml` 只留 `${ENV_VAR}` 占位符，**不含默认值**；
- 本地默认值全部集中到 `bootstrap/src/main/resources/application-dev.yml`（**已 gitignore**，
  用 `--spring.profiles.active=dev` 或 `SPRING_PROFILES_ACTIVE=dev` 加载）；
- 生产环境由 K8s env 注入（见 `deploy/aiplatform.yaml`），缺 env 启动即失败，尽早暴露配置问题。

### 4.2 迁移后的配置来源对照

| 配置项 | 生产来源（K8s env） | 本地默认（application-dev.yml） |
|---|---|---|
| MySQL | `MYSQL_HOST/PORT/USERNAME/PASSWORD` | `192.168.3.131:30306` root/123456 |
| Redis | `REDIS_HOST/PORT/PASSWORD/DATABASE` | `192.168.3.131:30379` Redis@2026/0 |
| SSH（节点） | `SSH_USERNAME/SSH_PRIVATE_KEY_PATH` | ubuntu + `/Users/jakt/.ssh/id_ed25519` |
| cluster-ci | `CLUSTER_CI_WORK_DIR/MASTER_HOST/WORKER_HOST` | `/home/ubuntu/cluster-ci`、131、217 |
| 启动脚本同步开关 | `CLUSTER_CI_SYNC_ON_STARTUP` | `false`（本地调试关闭） |
| MinIO | `MINIO_ENDPOINT/ACCESS_KEY/SECRET_KEY/BUCKET` | 集群内 endpoint + 空凭据 |
| 镜像加速主机 | `AIPLATFORM_MIRROR_SSH_HOST` | `192.168.3.131` |
| 日志目录 | `LOG_FILE_PATH` | `/Users/jakt/IdeaProjects/aiplatform/logs/` |
| xuanyuan 上传账号 | `XUANYUAN_USERNAME/PASSWORD` | 空 |
| DeepSeek Key | `DEEPSEEK_API_KEY`（可选 secret） | 空 |

### 4.3 代码内 @Value 默认值已清理

- `SysLogManagerImpl`：`log.file.path`（原默认 `/Users/jakt/.../logs/`）→ 配置文件提供；
- `K8sClientImpl`：原硬编码 service account TOKEN + `k8s.test` 开关 → 改用 Spring `Environment`
  按 profile 分流：dev 读 `application-dev.yml` 的 `k8s.master-url/k8s.token` 直连集群 API Server，
  非 dev 走 in-cluster/kubeconfig 自动发现（生产无需配置，消除硬编码凭据）；
- `AiMirrorDownloadServiceImpl`：`ai.mirror.ssh-host`（原默认 192.168.3.131）→ 配置提供。

### 4.4 保留的例外

- `ai.deepseek.api-key: ${DEEPSEEK_API_KEY:}`：可选能力，保留空默认；若取消需同时去掉
  `deploy/aiplatform.yaml` 中 `deepseek-api-key` 的 `optional: true`。

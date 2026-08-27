# 集群管理功能 PRD

> 文档定位：开发阅读用。本文档描述"集群管理"功能的产品范围、业务流程、数据模型与实现约定，是 aiplatform 仓库新增该功能的唯一行为契约。
>
> 关联约定：[AGENTS.md](../AGENTS.md) 的模块职责、统一出口、命名规范全部适用，本文档只补充业务语义，不覆盖仓库级约束。

## 1. 背景与目标

aiplatform 需要一个"集群管理"功能，让开发同学通过页面完成两件事：

1. 查看集群整体大盘：节点数量、节点架构、CPU/内存等资源情况。
2. 管理业务 pod：配置（git 地址、Dockerfile、Deployment 参数等）后由系统触发部署，并支持查看、停用、启用、删除已部署的业务 pod。

核心产品形态：**系统本质是"前端拿参数 → 生成 Dockerfile + Deployment YAML → 执行 shell 脚本 → 触发部署"**。aiplatform 自身也是一个业务 pod（已提前拼好 YAML 可直接部署），用户登录 aiplatform 管理其他业务 pod。

## 2. 术语

| 术语 | 含义 |
|---|---|
| 业务 pod | 通过本系统创建、部署、管理的业务应用，是系统管理的原子对象。K8s 侧对应一个 Deployment（+ Service，可选 Ingress）。 |
| 业务命名空间 | 允许业务 pod 部署的目标命名空间（本期为 `tsk`、`test`），由系统配置维护，不建表。 |
| 配置版本 | 用户在系统里的一份业务 pod 配置即一个版本（版本号）。同一业务 pod 可有多份配置（多个版本），可分别启停，用于新版本上线、旧版本回退。版本号与镜像版本无关。 |

## 3. 产品范围

本期只做三块：

1. **数据大盘**：集群节点信息、资源使用、业务 pod 分布。
2. **配置管理**：业务 pod 配置的增删改查（含 Dockerfile、Deployment YAML、版本、开关）。
3. **实时管理**：查看集群中正在运行/已停止的业务 pod，支持查看日志、停用、启用、删除。

### 3.1 明确不做（本期非目标）

- 不搭建 Harbor / 私有 registry（镜像不 push 到任何仓库）。
- 不做多集群管理。
- 不做 Dockerfile / Deployment 的智能识别或模板替换（Dockerfile 由用户必填，YAML 由系统预填 + 用户自由编辑）。
- 不做 Helm、不做仓库内 CI 脚本编排（仓库只提供源码）。
- 不做命名空间白名单表（命名空间由系统配置维护）。
- 不做越权扫描（部署账号权限本身收敛，越权由 K8s RBAC 拒绝）。
- 不做权限设计（复用现有 RBAC / 菜单体系，细节不在本文档范围）。
- 不做部署任务/实例落库（实时状态、日志、事件全部通过 Java k8s client 实时查询）。

## 4. 总体架构

### 4.1 技术选型

| 关注点 | 选型 | 说明 |
|---|---|---|
| K8s 操作 | Java 内嵌 k8s client（fabric8 或官方 client），放 `common-integration` | 大盘查询、实时状态查询、apply / scale / delete / watch |
| 节点脚本执行 | Java SSH 驱动 master 上脚本（如 JSch） | Java 不安装 docker/ctr/kubectl，命令全部甩给 master |
| 文件交换 | hostPath 挂载 master 的 `~/aiplatform-ci` 到 aiplatform 容器 | Java 生成的文件（Dockerfile、deployment.yaml、日志）master 直接可见 |
| 镜像构建 | 每目标节点各自本地构建 | master 构建 AMD、worker 构建 ARM，各自导入自己 containerd |
| 调度 | Java 层定时任务（`@Scheduled`） | 不做系统级 cron；按业务 pod 配置的"自动刷新"开关轮询 git 分支 |

### 4.2 部署链路（脚本一体）

```text
┌────────────┐  SSH    ┌────────────────────────────────────────┐
│   Java      │ ──────▶ │   master 上的 ~/cluster-ci/            │
│ (aiplatform)│         │                                        │
│            │         │  fetch_source.sh（拉源码）              │
│ 读配置      │         │    └─ git 浅克隆指定分支               │
│ 生成Dockerfile│       │  build_deploy.sh（构建+部署一体）        │
│ 生成deploy.yaml │      │    ├─ master: docker build→save→ctr   │
│ SSH触发脚本  │         │    ├─ worker: 同上（各自架构）         │
│ k8s client   │         │    └─ kubectl apply + rollout         │
│ 查大盘/状态   │         │  全程输出 tee 到 deploy-*.log         │
└────────────┘         └────────────────────────────────────────┘
```

- **Java（aiplatform）**：读配置 → 生成 Dockerfile + deployment.yaml → 写入挂载目录 → SSH 触发 master 脚本 → 轮询状态 → 日志回显。
- **fetch_source.sh**：git 浅克隆指定分支到挂载目录；大仓库带低网速保护（60s 低于 1KB/s 终止）。
- **build_deploy.sh**：构建 + 部署一体——master/worker 各自构建导入 + `kubectl apply` + `set image` + `rollout status`，**全程输出 tee 到 `apps/<configId>/deploy-*.log`**（一次 SSH 调用，日志完整可见）。
- **image_tools.sh / deploy.sh**：保留为底层组件，被 build_deploy.sh 内联调用（避免双重 tee）。

### 4.3 镜像构建语义

- 不使用任何 push 型 registry；`docker.xuanyuan.run` 仅作为基础镜像拉取加速器。
- 每台目标节点：`git 拉码 → docker build -t <podName>:<短哈希> → docker save → sudo ctr -n k8s.io image import`。
- 镜像名 = 配置的 podName；tag = 本次构建的 git commit 前 7 位（脚本自动计算），用户完全不接触镜像版本号。
- 不同架构的节点各自构建各自架构的镜像，天然满足 AMD/ARM 需求。

## 5. 业务流程

### 5.1 配置业务 pod（保存 ≠ 部署）

```text
用户填写配置（podName/命名空间/git/Dockerfile/YAML等）
  → 提交时轻校验（YAML 合法、命名空间在业务命名空间集合内）
  → 落库（生成一条配置版本记录）
  → 结束，不触发任何部署
```

### 5.2 触发部署

两个触发入口，均从 Java 层发起：

```text
① 手动：用户在配置/实时管理页点击"部署"
② 自动（可选）：Java 定时任务按"自动刷新"开关轮询 git 分支
      └─ 比对"上次构建 commit"与远程分支最新 commit，有变化才触发
  → 部署前校验：集群中已存在同 podName 的 Deployment → 提示"已部署过"，拒绝本次部署
  → 未部署过 → 异步执行：生成 Dockerfile + deployment.yaml → 写入挂载目录
  → SSH master 执行 fetch_source.sh（git 浅克隆拉源码）
  → SSH master 执行 build_deploy.sh（逐节点构建导入 + kubectl apply + rollout，全程写日志）
  → 结束：成功/失败不随请求返回，用户在实时管理页通过状态、日志、Events 自行排查
```

> 部署是**异步执行**：点击部署只做受理（校验 + 起后台任务），构建+导入+apply+rollout 在后台线程中执行，不阻塞请求；结果不随响应返回。实时管理页实时反映 Deployment/Pod 状态，用户通过日志与 Events 自行排查。

### 5.3 实时管理操作语义

| 操作 | 语义 | 配置行 |
|---|---|---|
| 停用 | 对应 Deployment 的 replicas 改为 0（`kubectl scale` / client 改），K8s 资源保留 | 保留，状态标记"已停用" |
| 启用 | 对应 Deployment 的 replicas 恢复为配置 YAML 中声明的副本数；YAML 未声明则按默认 1 | 保留 |
| 删除 | 删除该 Deployment（Service/Ingress 一并删除），配置行一并删除，二次确认 | 删除，不可恢复 |
| 回退 | 停用新版本配置行 + 启用旧版本配置行（由用户操作组合完成） | 保留 |

### 5.4 并发控制

- 同一业务 pod（同一配置版本）同一时间只允许一个部署在异步执行中；新触发直接拒绝/跳过并提示"部署中"。
- 不同业务 pod 之间互不影响，可并行。

## 6. 功能详述

### 6.1 数据大盘

**节点信息**

| 字段 | 说明 |
|---|---|
| 节点名称 | K8s node name |
| 角色 | master / worker（按 label 判断） |
| 架构 | AMD / ARM（按 `kubernetes.io/arch`） |
| 状态 | Ready / NotReady |

**资源概览**

- 集群 CPU 总量 / 已用（来源：metrics-server 的 NodeMetrics，Java k8s client 拉取）。
- 集群内存总量 / 已用。

**业务维度**

- 系统管理的业务 pod 总数、各状态分布（运行中 / 已停止 / 部署中 / 失败）。
- 每个节点的业务 pod 数量，按命名空间（tsk / test）分组统计。

> 大盘查询全部走 Java k8s client，不做 SSH 拼 kubectl 输出。

### 6.2 配置管理

#### 6.2.1 业务命名空间

- 不建表；由 Java 枚举或环境变量维护（推荐环境变量 `AIPLATFORM_BIZ_NAMESPACES=tsk,test`）。
- 创建业务 pod 时从该集合下拉选择。
- 提交校验：命名空间必须在该集合内且符合 K8s 命名规范。

#### 6.2.2 业务 pod 配置字段

| 字段 | 必填 | 说明 |
|---|---|---|
| 资源名称 | 是 | 中文名，业务展示用（如"用户中心"） |
| podName | 是 | 业务 pod 名称，唯一，同时作为镜像名 |
| 版本号 | 是 | 用户可读的配置版本号（如 v1、v2），同一 podName 下唯一 |
| 业务命名空间 | 是 | 从业务命名空间集合下拉选择 |
| git 仓库地址 | 是 | 支持 `https://<user>:<token>@...` 带 token 写法；敏感字段，不回显 |
| git 分支 | 是 | 触发部署/自动刷新时拉取该分支 |
| Dockerfile 内容 | 是 | 用户必填完整 Dockerfile，不支持留空、不做默认 |
| Deployment YAML | 是 | 系统按所选参数预填基础 YAML（约完成 80%），用户可自由编辑，支持任意 K8s 资源（含 Secret 等） |
| 自动刷新 | 否 | 勾选后入库，后台 Java 定时任务按此轮询该分支自动构建部署 |

> 配置页只负责"配置 + 首次部署"；启停不落库，是实时管理页的操作按钮，点击调用接口直接对 K8s Deployment 生效。是否已在运行由 k8s client 实时查询判断（用户手动在 K8s 删除也如实反映），不依赖任何落库状态字段。

**前端辅助控件（不落库）**

| 控件 | 说明 |
|---|---|
| 架构选择 | 仅 AMD / 仅 ARM / 两者；只参与"生成 Deployment"预填，不落库 |
| 副本数 | 期望副本数；只参与"生成 Deployment"预填，不落库 |
| 是否开启 Ingress | 勾选后预填 YAML 追加 Ingress；不落库 |

以上三个控件是前端下拉框/选择框，用户在配置页选择后点击"生成 Deployment"，系统把参数映射成基础 YAML 回填到编辑框；**落库的只有用户最终编辑后的 YAML 本身**。后续部署/启停所需的架构、副本数等信息一律从落库 YAML 解析（nodeSelector、spec.replicas），不依赖前端表单。

#### 6.2.3 Deployment YAML 预填规则

系统按用户所选参数生成基础 YAML：

- Deployment：镜像 `podName:commit短哈希`、副本数、`nodeSelector`（按架构选择写 `kubernetes.io/arch`）、命名空间。
- Service：ClusterIP，随 Deployment 一并生成。
- Ingress：勾选时追加，指向 Service。

用户在此基础上自由编辑，提交后以**用户编辑后的 YAML** 为准落库，部署时使用该份 YAML。

#### 6.2.4 提交校验（轻校验）

1. YAML 语法可解析（Hutool/Jackson/SnakeYAML 任选，遵循仓库工具约定）。
2. 命名空间非空、符合 K8s 命名规范、且在业务命名空间集合内。
3. 必填字段非空（资源名称、podName、版本号、git、分支、Dockerfile、Deployment YAML）。

不做：越权扫描、字段级语义检查。用户想干嘛都行，只要拼得出合法 YAML。

### 6.3 实时管理

实时管理列表数据来自 Java k8s client 实时查询，每行展示：

| 字段 | 说明 |
|---|---|
| pod 名称 | 配置的 podName |
| 配置版本号 | 当前展示的配置版本 |
| 命名空间 | tsk / test |
| 状态 | 按 Deployment 状态实时推导（见下） |
| 副本数 | 实际 / 期望（如 1/1、0/0、0/1） |
| 所在节点 + 架构 | 节点名 + AMD/ARM |
| 镜像 | `podName:短哈希` |
| 操作 | 查看日志+Events / 停用 / 启用 / 删除 |

**状态推导规则（不落库，k8s client 实时查询）**

| Deployment 状态 | 页面展示 |
|---|---|
| 存在且 readyReplicas == replicas > 0（如 1/1） | 运行中 |
| 存在且 replicas == 0（如 0/0） | 已停止 |
| 查不到（Deployment 不存在） | 已删除/不存在 |
| readyReplicas < replicas，或 Pod 非 Running（ContainerCreating、CrashLoopBackOff、ImagePullBackOff 等） | 异常/部署中，页面渲染错误态，点击可查看日志 + Events |

### 6.4 部署日志与事件

- 部署是异步执行：每次部署在挂载目录生成 `<configId>/deploy-<时间戳>.log`，构建/apply 输出追加写入；因挂载目录在 aiplatform 容器内，Java 直接读文件即可在页面回显部署过程日志。
- 运行期排查：Pod 日志与 K8s Events 全部通过 Java k8s client 实时查询（`logs` / `events`），不建表。

## 7. 数据模型

> 一张表。命名、分页、逻辑删除等遵循 AGENTS.md 第 11 节生成流程；DO/Model/Query 等由代码生成器生成后按仓库约定修剪。实时状态/日志/事件不落库，全部 k8s client 实时查询。

### 7.1 cluster_pod_config（业务 pod 配置表）

一行 = 一个业务 pod 的一个配置版本。

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| resource_name | VARCHAR(64) | 资源名称（中文名），业务展示用 |
| pod_name | VARCHAR(64) | 业务 pod 名称，镜像名 |
| version_no | VARCHAR(32) | 配置版本号（如 v1），与 pod_name 联合唯一 |
| namespace | VARCHAR(64) | 业务命名空间 |
| git_url | VARCHAR(512) | git 地址（可含 token，敏感） |
| git_branch | VARCHAR(128) | 分支 |
| dockerfile | TEXT | 用户必填的 Dockerfile 内容 |
| deploy_yaml | MEDIUMTEXT | 用户编辑后的最终 YAML |
| auto_refresh | TINYINT | 自动刷新开关 |
| last_built_commit | VARCHAR(64) | 上次构建 commit（自动刷新比对） |
| remark | VARCHAR(255) | 备注 |
| del_flag | TINYINT | 逻辑删除（按仓库约定 0/2） |
| create_by / create_time / update_by / update_time | - | 审计字段 |

唯一约束：`uk_pod_version (pod_name, version_no)`。

## 8. 接口草案

> 遵循仓库 web 层规范：`ApiResult<T>` + `ApiTemplate` + ParamChecker + Assembler。以下为草案，落地时按生成器产出修剪。

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /api/cluster/dashboard | 大盘数据 |
| GET | /api/cluster/namespaces | 业务命名空间列表 |
| GET | /api/cluster/pod-config/page | 配置分页查询 |
| POST | /api/cluster/pod-config | 新增配置版本 |
| PUT | /api/cluster/pod-config/{id} | 编辑配置 |
| DELETE | /api/cluster/pod-config/{id} | 删除配置（含 K8s 资源） |
| POST | /api/cluster/pod-config/{id}/deploy | 手动触发部署（异步受理，仅校验+返回受理结果） |
| POST | /api/cluster/pod-config/{id}/stop | 停用（缩容 0） |
| POST | /api/cluster/pod-config/{id}/start | 启用（扩容到副本数） |
| GET | /api/cluster/runtime/list | 实时管理列表 |
| GET | /api/cluster/runtime/{podName}/logs | 运行 Pod 日志（k8s client 实时查询） |
| GET | /api/cluster/runtime/{podName}/events | 运行事件 Events（k8s client 实时查询） |

## 9. 模块与包规划

按 AGENTS.md 依赖方向落地：

| 模块 | 内容 |
|---|---|
| common-integration | k8s client 封装、SSH 执行封装（含集成异常，日志用 LogFileEnum.INTEGRATION） |
| common-dal | ClusterPodConfigDO、Mapper + XML、DalQuery |
| core-model | ClusterPodConfig、QueryParam、枚举（PodArchEnum、TriggerTypeEnum）、常量 |
| core-repository | ClusterPodConfigRepository(Impl)、Convertor |
| core-service | ClusterPodConfigService（CRUD+校验+触发编排）、ClusterDashboardService（大盘）、ClusterRuntimeService（实时管理）、ClusterPodBizChecker |
| biz-service-impl | ClusterManager / ClusterManagerImpl（用例编排） |
| web | ClusterController、param/result/assembler/checker |
| bootstrap | 无需业务代码（如需 Bean 装配按仓库约定） |

## 10. 脚本契约

### 10.1 fetch_source.sh（拉源码）

入参：`<git_url> <branch> <dest_dir> <dockerfile_path> [log_dir]`。

执行逻辑：

1. 直接用用户填写的 git URL 浅克隆；公开仓库无需凭证，私有仓库由用户在地址中携带凭证（`https://用户名:token@域名/owner/repo.git`，GitHub/Gitee 通用）。
2. git 浅克隆单分支到 `dest_dir`；带低网速保护（`GIT_HTTP_LOW_SPEED_LIMIT=1`、`GIT_HTTP_LOW_SPEED_TIME=60`，持续 60s 低于 1KB/s 终止）。
3. 用用户配置的 Dockerfile 覆盖仓库里的 Dockerfile（保证构建用系统配置）。
4. 输出 commit 短哈希（镜像 tag）。
5. `log_dir` 非空时输出 tee 到 `${log_dir}/fetch.log`。

### 10.2 build_deploy.sh（构建 + 部署一体）

入参：`<image> <tag> <src_dir> <log_dir> <namespace> <yaml_path> <worker_host>`。

执行逻辑：

1. 全程输出 `exec > >(tee ${log_dir}/deploy-<时间戳>.log)`——**构建和部署每一步都实时写入日志文件**（一次 SSH 调用，日志完整可见）。
2. master 本机：`docker build → docker save → sudo ctr -n k8s.io image import`。
3. worker：同步源码 → 远端同步骤（各自架构，天然正确）。
4. `kubectl apply -f <yaml>` → `kubectl set image` → `kubectl rollout status`。
5. 任一节点失败即整体失败（日志记录原因）。

> image_tools.sh（构建导入）与 deploy.sh（apply）保留为底层组件，由 build_deploy.sh 内联调用，避免双重 tee 产生多份日志。

## 11. 安全与敏感信息

- git_url 若含 token：不打印日志、接口不回显明文（返回脱敏展示）。
- 部署日志可能含敏感内容，页面查看权限由现有 RBAC 控制，不在本文档范围。
- Java 侧不保存 master SSH 私钥到代码库，走现有密钥/Secret 体系。

## 12. 风险与待确认项

- worker 节点需具备完整构建环境（Java/Maven/Node 等），否则 ARM 部署失败——与"各节点本地构建"决策绑定，文档已记录。
- hostPath 挂载要求 aiplatform pod 固定调度在 master（现状 nodeAffinity 已满足）。
- 多版本并行时 Deployment 命名需按 `podName-versionNo` 区分，避免冲突（落地时确认命名规则）。
- 镜像 tag 用 commit 短哈希，节点 containerd 中旧镜像会累积，本期不做清理。
- 停用/启用依赖从落库 YAML 解析副本数；用户 YAML 未声明 `spec.replicas` 时，启用按默认 1 副本处理（用户 YAML 写什么都行，系统不强制校验副本数）。

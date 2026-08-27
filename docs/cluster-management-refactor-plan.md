# 集群管理改造方案（仅评审，未实施）

> 本文档基于接口实测与 PRD 梳理改造范围，供评审工作量与优先级，**不含实施**。

## 0. 现状问题摘要（实测）

| # | 问题 | 影响 |
|---|---|---|
| 1 | Deployment 名不统一：系统用 `podName-versionNo`，用户 YAML 写 `podName` | stop/start/日志/事件 404 |
| 2 | 逻辑删除 + 唯一约束 `uk_pod_version` | 删除后无法重建同版本 |
| 3 | 配置页有停用/启用，需求改为只在实例管理 | 页面操作与需求不符 |
| 4 | 无版本号字段设计，但 DB/前后端都带 versionNo | 需整体移除 |
| 5 | 批量删除：前端有勾选，后端 DELETE 只收单 id | 批量失败 |
| 6 | 配置无状态机 | 草稿/构建中/发布流转缺失 |

## 1. 需求明细与改动映射

### 1.1 配置管理：操作收敛为 增 / 删 / 改 / 部署 / 详情

**现状**：配置页有 新增/修改/删除/部署/停用/启用/详情。
**目标**：去掉配置页的"停用/启用"，只留 新增、修改、删除、部署、详情。

改动：
- 前端 `config/index.vue`：删除行内"停用/启用"按钮（handleStop/handleStart 及相关 API 调用）。
- 后端不动（stop/start 接口保留，供实例管理用）。

### 1.2 实时管理 → 实例管理

**现状**：路由 `/cluster/runtime`，页面名"实时管理"。
**目标**：改名"实例管理"，停用/启用/删除/日志/事件只在这里。

改动：
- 前端路由、菜单名、页面标题、API 路径前缀统一 `runtime → instance`（或保留路径只改显示名，二选一，建议保留后端路径只改前端文案，减少改动）。
- 菜单 SQL：`cluster_menu.sql` 中菜单名"实时管理"→"实例管理"。
- 若改路径则涉及：前端 api、页面路由、后端 Controller `@RequestMapping`、菜单 component 路径。

### 1.3 移除版本号（versionNo）

**现状**：DB 有 `version_no` 列 + 唯一约束；core-model / DO / DTO / 前端表单都有该字段。
**目标**：删除版本号，配置以 `podName` 为唯一标识。

改动（工作量大头）：
- **DB**：删 `version_no` 列；唯一约束改为 `uk_pod_name(pod_name)`。
- **generate.yaml**：移除 version_no 配置 → 重新生成或手工修剪 DO/Model/DalQuery/Mapper XML/Request/Response。
- **后端**：ClusterPodConfig(DO/Model/QueryParam/Request/Response/Assembler/Convertor) 全链路删字段。
- **前端**：config 表单删"版本号"输入框，列表列删"版本"。
- 同步影响：`deploymentName()` 的 fallback（`podName-versionNo`）改为纯 `podName`；`fillPodConfigId` 匹配逻辑简化。

### 1.4 移除逻辑删除，唯一约束改 podName

**现状**：`del_flag` + `uk_pod_version(pod_name, version_no)`。
**目标**：物理删除；唯一约束 `uk_pod_name(pod_name)`。

改动：
- **DB**：删 `del_flag` 列；`uk_pod_version` → `uk_pod_name(pod_name)`。
- **generate.yaml**：移除 logicDelete 配置。
- **Mapper XML**：删 `del_flag='0'` 过滤；`deleteById` 恢复物理 DELETE。
- **DO/Model**：若生成器因 logicDelete 生成 delFlag 字段则移除。
- **前端删除**：改为单条/批量物理删除，删除二次确认文案不变。

> 注意：删 version_no 后，同一个 podName 只能有一条配置；"多版本并行"能力移除，与 PRD 初版冲突，需确认是否接受（需求 3 明确"没有版本号"）。

### 1.5 批量删除兼容

**现状**：前端表格支持勾选（multiple），`handleDelete` 用 `ids`（数组）；后端 `DELETE /pod-config/{id}` 只收单 id。
**目标**：支持批量删除。

改动（推荐方案）：
- 后端：`DELETE /pod-config` 改为 body 传 `List<Long>` 或新增 `POST /pod-config/batch-delete`；Manager/Service 循环物理删除（单表单删，无需事务模板；若要原子可走 BizTemplate）。
- 前端：`handleDelete` 已传数组，适配新接口。

### 1.6 复制配置（新增功能）

**目标**：勾选一条配置 → 点击"复制" → 创建一份完全相同的新配置（除 podName 后缀），状态为草稿，方便下次编辑。

**语义**（类似 `cp`）：
- 复制的是**配置数据**（gitUrl/gitBranch/dockerfile/deployYaml/autoRefresh/命名空间等），不含运行态。
- 新配置 `podName = 原 podName + "-copy"`（如 `jianli` → `jianli-copy`）；若冲突再加序号（`jianli-copy1`）。
- 新配置 `status = DRAFT`，其余字段与原配置一致。
- 复制的配置不会触发任何部署，仅落库。

改动：
- 后端：`POST /pod-config/{id}/copy`（或复用创建接口加 copyFromId）；Manager 读取原配置 → 改 podName/status → 走创建逻辑（校验 uk_pod_name 唯一，冲突自动加序号）。
- 前端：配置页加"复制"按钮（单条勾选时可用），调用复制接口后刷新列表并提示。

### 1.7 配置状态机

**状态（5 个）**：

| 状态 | code | 可编辑 | 可删除 | 可部署/构建 | 可弃用 | 说明 |
|---|---|---|---|---|---|---|
| 草稿 | DRAFT | ✅ | ✅ | ✅（构建） | - | 创建后初始状态 |
| 构建中 | BUILDING | ❌ | ❌ | ✅（可重试构建） | - | 防资源残留，不可删 |
| 构建失败 | BUILD_FAILED | ✅ | ✅ | ✅（修改后再构建） | - | 失败后与草稿同权 |
| 发布 | PUBLISHED | ❌ | ❌ | ✅（可再次部署） | ✅ | 只有弃用按钮 |
| 弃用 | RETIRED | ❌ | ❌ | ❌ | - | 只有查看按钮，不支持恢复 |

**流转**：

```text
创建 → DRAFT
  DRAFT --部署--> BUILDING
  BUILDING --成功--> PUBLISHED
  BUILDING --失败--> BUILD_FAILED
  BUILD_FAILED --修改后部署--> BUILDING
  PUBLISHED --再次部署--> BUILDING
  PUBLISHED --弃用--> RETIRED
```

**要点**：
- 构建失败状态可拿到：部署是 @Async + @Retryable，重试耗尽后 catch 到异常即可置 BUILD_FAILED（实现上在 ClusterDeployServiceImpl.deploy 内部 try/catch，成功置 PUBLISHED、失败置 BUILD_FAILED）。
- 删除规则：**除 BUILDING 外均可删**；但 PUBLISHED/RETIRED 受产品规则限制不可删（PUBLISHED 只能弃用、RETIRED 只有查看）——即删除按钮只在 DRAFT / BUILD_FAILED 显示。
- 配置状态机与实例管理**完全解耦**：实例管理（K8s 运行态）的停用/启用/删除不读写配置状态；删配置**不联动删实例**。

**操作权限矩阵（前后端双校验）**：

| 操作 | DRAFT | BUILDING | BUILD_FAILED | PUBLISHED | RETIRED |
|---|---|---|---|---|---|
| 修改 | ✅ | ❌ | ✅ | ❌ | ❌ |
| 删除 | ✅ | ❌ | ✅ | ❌ | ❌ |
| 部署/构建 | ✅ | ✅ | ✅ | ✅ | ❌ |
| 弃用 | ❌ | ❌ | ❌ | ✅ | ❌ |
| 详情/查看 | ✅ | ✅ | ✅ | ✅ | ✅ |
| 复制 | ✅ | ❌ | ✅ | ❌ | ❌ |

改动：
- **DB**：`cluster_pod_config` 加 `status` 列（varchar(20)，DRAFT/BUILDING/BUILD_FAILED/PUBLISHED/RETIRED）。
- **core-model**：新增 `ClusterPodConfigStatusEnum`。
- **core-service**：`ClusterPodConfigService` 增加状态流转校验（update/delete 前置校验，按上表）；`ClusterDeployServiceImpl` 构建成功/失败回调置状态。
- **Manager/Controller**：create 默认 DRAFT；update/delete 状态校验；deploy 触发时先置 BUILDING；批量删除同样走状态校验。
- **前端**：配置页按状态控制操作栏按钮显隐，**严格对齐操作权限矩阵**（同一份矩阵，前端隐藏 + 后端校验双保险）。

**双校验说明**：
- 前端：操作栏按当前行 status 渲染按钮（隐藏即不可点），用户不感知被禁。
- 后端：Manager/Service 在 update/delete/deploy/retire/copy 入口处统一校验 status 与操作是否匹配，不匹配抛业务异常（如 `配置状态不允许该操作`），防止绕过前端直接调接口。
- 校验实现：core-service 抽一个状态校验方法（`checkStatusAllowed(status, operation)` 或按操作拆 `checkXxxAllowed`），Manager 和 Controller 入口都调，避免各处散落 if。

## 2. 涉及文件清单（估算）

### 后端

| 层 | 文件 | 改动 |
|---|---|---|
| DB | `sql/cluster_pod_config.sql` + 迁移 SQL | 删 version_no/del_flag，改唯一约束，加 status |
| 生成配置 | `generate.yaml` | 删 version_no/logicDelete，加 status |
| common-dal | `ClusterPodConfigDO` / `Mapper` / `Mapper.xml` / `DalQuery` | 删字段、物理删除、状态列 |
| core-model | `ClusterPodConfig` / `QueryParam` / 新 `StatusEnum` | 删字段、加状态 |
| core-repository | `ClusterPodConfigConvertor` / `RepositoryImpl` | 字段同步 |
| core-service | `ClusterPodConfigService(Impl)` / `ClusterDeployService(Impl)` / `ClusterK8sService(Impl)` | 状态机校验、部署回调、deploymentName 从 YAML 解析、删 versionNo 影响 |
| biz | `ClusterPodConfigManager(Impl)` | 批量删除、状态校验、复制配置、stop/start 归属实例管理 |
| web | `ClusterController` / Request/Response / Assembler / ParamChecker | 删字段、批量删除接口、复制接口、状态字段 |

### 前端（aiplatform-vue）

| 文件 | 改动 |
|---|---|
| `src/api/cluster/index.js` | 批量删除、删版本号参数、状态字段 |
| `src/views/cluster/config/index.vue` | 删停用/启用按钮、删版本号、加状态展示与流转控制、批量删除 |
| `src/views/cluster/config/index.vue` | 加"复制"按钮（单条勾选时可用） |
| `src/views/cluster/runtime/index.vue` | 改名实例管理（文案），保留停用/启用/删除/日志/事件 |
| `src/views/cluster/dashboard/index.vue` | 状态字段不影响，暂不动 |
| 路由/菜单 | 菜单名改"实例管理" |

## 3. 工作量估算（人天，单人）

| 模块 | 预估 |
|---|---|
| DB 迁移 + generate.yaml + 生成产物修剪（删版本号/逻辑删除/加状态） | 1 天 |
| 后端状态机 + 部署回调 + deploymentName 解析 + 批量删除 + 复制配置 | 1.5 天 |
| 前端配置页改造（去停用启用、删版本号、状态展示、批量删除） | 1 天 |
| 前端复制按钮 + 联调 | 0.5 天 |
| 前端实例管理改名 + 联调 + 回归测试 | 0.5 天 |
| **合计** | **约 4.5 天** |

> 不含：部署链路底层修复（Dockerfile 覆盖路径已修未部署）、大盘业务统计补齐（P2-4，本次需求未提）。

## 4. 已确认决策（评审结论）

| # | 决策 |
|---|---|
| 1 | PUBLISHED 可再次点击部署（再构建） |
| 2 | 构建失败新增状态 BUILD_FAILED，与草稿同权（可改可删），修改后点部署进入 BUILDING |
| 3 | 配置状态机与实例管理完全无关（实例管理只操作 K8s 运行态，不读写配置状态） |
| 4 | 旧多版本数据（v2-v5）迁移时**清除** |
| 5 | 删除规则：除 BUILDING 外均可删（BUILDING 防资源残留）；PUBLISHED/RETIRED 按产品规则不可删（PUBLISHED 只能弃用，RETIRED 只有查看） |
| 6 | 弃用（RETIRED）：不可编辑/删除/部署，只有查看按钮 |
| 7 | 删配置**不联动删实例** |
| 8 | 删实例**不联动删配置**（互相无联动删除） |
| 9 | RETIRED 弃用状态**不支持恢复**，只有查看 |
| 10 | 新增"复制配置"功能（podName + 副本后缀，状态草稿，仅复制配置数据） |

## 6. 建议实施顺序

1. DB 迁移 + 生成产物修剪（地基）
2. 后端状态机 + deploymentName 解析 + 批量删除
3. 前端配置页/实例管理改造
4. 部署新版本 + 回归

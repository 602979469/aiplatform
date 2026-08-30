# 文件管理模块 PRD

> 文档定位：开发阅读用。本文档描述"文件管理"功能的产品范围、业务流程、数据模型与实现约定，是 aiplatform 仓库新增该功能的唯一行为契约。
>
> 关联约定：[AGENTS.md](../AGENTS.md) 的模块职责、统一出口、命名规范全部适用，本文档只补充业务语义，不覆盖仓库级约束。

## 1. 背景与目标

aiplatform 需要一个通用文件管理模块，供各业务方按 namespace 隔离地管理文件，能力收敛为五个：

1. 上传文件；
2. 下载文件；
3. 按 namespace 查看文件列表；
4. 删除文件（通过 id）；
5. 更新文件（通过 id）。

核心产品形态：**每个文件归属于一个业务命名空间（namespace），所有操作必须携带 namespace，文件只能在所属 namespace 内被查询、下载、更新、删除**。namespace 是业务分组维度，不是用户权限维度。

## 2. 术语

| 术语 | 含义 |
|---|---|
| namespace | 业务命名空间，文件的分组标识（如 `avatar`、`docs`、`model`），由调用方自行命名，本期不建表维护 |
| 原始文件名 | 用户上传时的文件名（含扩展名），仅用于展示和下载 |
| 存储文件名 | 系统生成的 UUID，磁盘上的实际文件名，对调用方不可见 |
| 文件元信息 | 落库的文件描述数据（namespace、原始文件名、大小、类型、备注等），不含文件内容 |

## 3. 产品范围

本期做五件事：上传、下载、按 namespace 列表查询、删除、更新（元信息）。下载/删除/更新都必须通过 id + namespace 定位文件。

### 3.1 明确不做（本期非目标）

- 不做 namespace 管理表（可用命名空间由环境变量 `AIPLATFORM_FILE_NAMESPACES` 或 `FileNamespaceEnum` 枚举维护，默认 `aiplatform`、`jianli`）。
- 不做用户级 namespace 授权（不做"用户 A 只能访问 namespace X"的数据权限，权限沿用全局 RBAC 权限码）。
- 不做分片上传 / 断点续传 / 秒传（md5 去重列为增强项）。
- 不做文件类型白名单、病毒扫描、在线预览（下载一律 `application/octet-stream`）。
- 不做文件内容版本历史（文件被覆盖后旧内容不可恢复）。
- 不做逻辑删除（删除即物理删除 DB 行 + 磁盘文件，见 §5.5 与 §11 待确认项）。
- 不做对象存储（OSS/MinIO）接入，本期本地磁盘，存储能力在 core-service 收敛，后续可替换。

## 4. 总体架构

### 4.1 技术选型

| 关注点 | 选型 | 说明 |
|---|---|---|
| 文件存储 | **直接存数据库**：`file_info.file_content` 用 LONGBLOB，支持大文件 | 不依赖本地磁盘/外部存储，容器重启不丢文件；头像等统一走文件管理 |
| 元数据存储 | MySQL 单表 `file_info` | 走代码生成器 + 仓库分层 |
| 文件操作 | Hutool `FileUtil` + Spring `MultipartFile` 流式写入 | 大文件不整包读内存（transferTo 落盘） |
| 上传大小 | 业务侧不设上限（Spring multipart 配置为 -1） | 现为 5MB/10MB；无上限实际受磁盘空间与部署网关约束（见 §11） |
| 文件命名 | 存储文件名 = Hutool `IdUtil.fastSimpleUUID()` | 不可预测，天然防目录穿越、防文件名冲突 |

### 4.2 存储布局与隔离语义

- 文件内容直接存 `file_info.file_content`（LONGBLOB），DB 记录 namespace 字段做隔离。
- namespace 只允许 `[A-Za-z0-9_-]{1,64}`（禁止 `.`，从源头排除 `..` 路径穿越），且必须命中可用命名空间列表；列表校验在 Manager 层统一做（上传必校验、分页查询传了才校验），ParamChecker 不做列表校验。
- 所有按 id 的操作（下载/更新/删除/替换）必须携带 namespace，并校验记录归属：**namespace 不匹配一律按"文件不存在"处理**（不区分提示，防探测）。
- 列表/分页查询不加载 file_content 大字段，下载/头像展示时才按 id 单独查内容。

### 4.3 模块边界（对应 AGENTS.md）

- common-dal：只负责 `file_info` 表的 DO/Mapper/XML/DalQuery。
- core-repository：只负责元数据存取 + Convertor，不碰磁盘。
- core-service：磁盘读写 + 业务规则 + 元数据编排（参照现有 `AuthUserAdminServiceImpl` 直接写磁盘的先例）。
- web：收参 → ParamChecker → Manager → Assembler → ApiTemplate；下载接口为二进制流，Controller 注释说明无法包 ApiResult 的原因。

## 5. 业务流程

### 5.1 上传

```text
multipart 提交（namespace + file + 可选 remark）
  → ParamChecker：namespace 格式、file 非空、文件名合法
  → Service：生成 storageName(UUID) → 创建 {fileRoot}/{namespace} 目录
  → transferTo 流式落盘 → insert file_info 一行
  → 返回文件元信息（含 id）
失败兜底：落盘或落库任一步失败，清理已写磁盘文件，避免孤儿文件
```

### 5.2 下载

```text
GET /api/file/{id}/download?namespace=xxx
  → ParamChecker：id、namespace 非空且格式合法
  → BizChecker：文件存在且 namespace 匹配（不匹配 → FILE_NOT_FOUND）
  → Service 返回磁盘 File → Controller 流式输出（octet-stream + 原始文件名）
```

### 5.3 列表查询

```text
GET /api/file/page?namespace=xxx&fileName=xx&pageNum=1&pageSize=10
  → namespace 可选：不传查全部命名空间；传了必须命中可用列表（Manager 层校验）
  → fileName 可选模糊匹配
  → 分页返回文件元信息列表（不暴露 storageName / 磁盘路径）
```

### 5.4 更新（元信息）

```text
PUT /api/file/{id}（body：namespace + 可选 originalName + 可选 remark）
  → 校验文件存在且 namespace 匹配
  → 更新 original_name / remark（单表 update，不需要 BizTemplate）
  → 磁盘文件不动
```

### 5.5 删除

```text
DELETE /api/file/{id}?namespace=xxx
  → 校验文件存在且 namespace 匹配
  → 先删磁盘文件（文件已不存在视为成功，幂等），再删 DB 行
  → 磁盘删除失败 → 抛 DELETE_FAILED，DB 保留，可重试
物理删除，无恢复能力；与业务表逻辑删除约定不同，属有意取舍
```

### 5.6 内容替换

```text
POST /api/file/{id}/replace（multipart：namespace + 新 file）
  → 校验文件存在且 namespace 匹配
  → 更新 DB 行（file_content / original_name / file_size / file_type，remark 不动）
单行 UPDATE，无历史版本，替换后旧内容不可恢复
```

## 6. 功能详述

### 6.1 上传

| 参数 | 必填 | 说明 |
|---|---|---|
| namespace | 是 | 业务命名空间，格式 `[A-Za-z0-9_-]{1,64}` |
| file | 是 | MultipartFile，业务侧不设大小上限，受磁盘空间与部署网关约束 |
| remark | 否 | 备注，≤500 字符 |

文件名规则：

- 非空、≤255 字符、不含 `/`、`\` 与控制字符（防 Content-Disposition 头注入）；
- 原始文件名原样展示，扩展名取小写存入 `file_type`；
- 同 namespace 允许同名文件（不建唯一约束）。

响应（FileInfoResponse）：id、namespace、originalName、fileSize、fileType、remark、createBy、createTime、updateTime。

### 6.2 下载

- 响应头：`Content-Type: application/octet-stream`，`Content-Disposition: attachment; filename*=UTF-8''<URLEncode(originalName)>`（复用 AiMirrorController 写法）。
- 通过 Controller 流式输出，不整文件读内存。
- 文件不存在或 namespace 不匹配 → `FILE_NOT_FOUND`。

### 6.3 列表查询

| 参数 | 必填 | 说明 |
|---|---|---|
| namespace | 否 | 不传查全部命名空间；传了必须命中可用列表（Manager 层校验） |
| fileName | 否 | 原始文件名模糊查询 |
| pageNum / pageSize | 否 | 继承 PageQueryRequest 默认值 |

响应：`PageResult<FileInfoResponse>`（经 ConvertUtil.mapPage 转换）。

### 6.4 更新

| 参数 | 必填 | 说明 |
|---|---|---|
| namespace | 是 | 定位 + 归属校验 |
| originalName | 否 | 改名，规则同上传文件名规则 |
| remark | 否 | 改备注 |

- 不提供 namespace 迁移（v1 文件归属不可变）。
- 内容替换见 §6.6（本期做，替换后旧内容不可恢复）。

### 6.5 删除

- namespace + id 定位；物理删除，前端二次确认。

### 6.6 内容替换

| 参数 | 必填 | 说明 |
|---|---|---|
| namespace | 是 | 定位 + 归属校验 |
| file | 是 | 新文件，规则同 §6.1（业务侧不设大小上限） |

- 语义：同一文件记录换内容，original_name / file_size / file_type 跟随新文件，remark 不变（改名走 §6.4 的 PUT）。
- 无版本历史，替换后旧内容不可恢复；前端二次确认。
- 归属校验失败、记录不存在 → FILE_NOT_FOUND。
- 涉及一次 DB update（单表单写），不需要 BizTemplate。

## 7. 数据模型

> 一张表。命名、分页等遵循 AGENTS.md 第 11 节生成流程；DDL 落 `sql/file_info.sql`，generate.yaml 配置无需枚举列。

### 7.1 file_info（文件信息表）

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| namespace | VARCHAR(64) | 业务命名空间（隔离维度） |
| original_name | VARCHAR(255) | 原始文件名（含扩展名，展示/下载用） |
| file_size | BIGINT | 文件大小（字节） |
| file_type | VARCHAR(64) | 扩展名（小写，不含点，如 pdf） |
| file_content | LONGBLOB | 文件内容（直接存数据库，列表查询不加载） |
| remark | VARCHAR(500) | 备注 |
| create_by / create_time / update_by / update_time | - | 审计字段 |

普通索引：`idx_namespace_id (namespace, id)`。无 del_flag（物理删除，见 §5.5）。

DDL：

```sql
CREATE TABLE `file_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `namespace` varchar(64) NOT NULL COMMENT '业务命名空间',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名（含扩展名）',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(64) NOT NULL DEFAULT '' COMMENT '扩展名（小写，不含点）',
  `file_content` longblob NOT NULL COMMENT '文件内容（LONGBLOB，直接存数据库，支持大文件）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_namespace_id` (`namespace`, `id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件信息表';
```

## 8. 接口草案

> 遵循仓库 web 层规范：`ApiResult<T>` + ApiTemplate + ParamChecker + Assembler。下载为二进制流，Controller 必须注释说明原因（AGENTS.md §5.1）。权限码按现有 RBAC 体系落地（`@SaCheckPermission`）。

| 方法 | 路径 | 说明 | 权限码 |
|---|---|---|---|
| GET | /api/file/namespaces | 可用命名空间列表（下拉框数据源，环境变量/枚举） | file:list |
| POST | /api/file/upload | multipart 上传（namespace + file + remark） | file:upload |
| GET | /api/file/page | 分页查询列表（namespace 可选，传了必校验） | file:list |
| GET | /api/file/{id}/download | 下载文件二进制流 | file:download |
| PUT | /api/file/{id} | 更新元信息（originalName / remark） | file:update |
| DELETE | /api/file/{id} | 删除文件（物理删除） | file:delete |
| POST | /api/file/{id}/replace | 替换文件内容（保留记录，覆盖旧文件） | file:update |

## 9. 模块与包规划

按 AGENTS.md 依赖方向落地：

| 模块 | 内容 |
|---|---|
| common-dal | `FileInfoDO`、`FileInfoMapper` + `FileInfoMapper.xml`、`FileInfoDalQuery`（insert 全字段 / update 全量 / findPage / findById / deleteById，按生成器产出修剪） |
| core-model | `FileInfo` Model（含 fileContent）、`FileInfoQueryParam`、常量（namespace 正则收口 `FileConstants`）、`BizErrorCodeEnum` 新增 `FILE_NOT_FOUND` / `FILE_NAME_INVALID` |
| core-repository | `FileInfoRepository(Impl)`、`FileInfoConvertor` |
| core-service | `FileInfoService` + `FileInfoServiceImpl`（内容直接读写数据库 + 元数据编排）、`FileInfoBizChecker` |
| biz-service-impl | `FileManager` / `FileManagerImpl`（用例编排，输入输出 core-model） |
| web | `FileController`、`FileQueryRequest` / `FileUpdateRequest` / `FileInfoResponse`、`FileParamChecker`、`FileAssembler` |
| bootstrap | `application.yml`：multipart 上限设为 -1（不限） |

generate.yaml 增加：

```yaml
- db_table_name: file_info
  model_name: FileInfo
  model_comment: "文件信息表"
  generateController: true
```

## 10. 安全与敏感信息

- namespace 白名单字符集排除 `.`，文件定位只用 id + namespace，杜绝目录穿越。
- 原始文件名禁止控制字符，防 Content-Disposition 响应头注入。
- 响应不暴露 storage_name / 磁盘路径，前端只认 id。
- 下载一律 `application/octet-stream`，不 inline 渲染，防存储型 XSS。
- namespace 不匹配与文件不存在统一返回"文件不存在"，不泄露是否存在。
- 文件内容不做敏感扫描（本期不做病毒/敏感词扫描，属明确非目标）。

## 11. 风险与待确认项

| # | 事项 | 结论 / 建议 | 影响 |
|---|---|---|---|
| 1 | "更新"语义 | 已确认：PUT 改元信息 + POST /{id}/replace 换内容，均纳入本期 | 接口范围按 §8 落地 |
| 2 | 删除语义 | 已确认：物理删除（删 DB 行即删内容），不加 del_flag | 删除后不可恢复 |
| 3 | 上传大小 | 已确认：业务侧不设上限，multipart 配置 -1；实际受 MySQL `max_allowed_packet`（当前 64MB）与内存约束 | 影响 DB 服务端配置 |
| 4 | 存储介质 | 已确认：文件内容直接存 MySQL LONGBLOB，无本地磁盘依赖 | 大文件占用 DB 空间，需关注容量 |
| 5 | namespace 管理 | 已确认：环境变量 `AIPLATFORM_FILE_NAMESPACES`（逗号分隔）覆盖，未配置取 `FileNamespaceEnum`（`aiplatform`/`jianli`）；提供下拉列表接口，上传/查询在 Manager 层校验列表归属 | 无 |
| 6 | 同名文件 | 允许重复，不建唯一约束 | 列表页靠 create_time / id 区分 |
| 7 | 无上限上传滥用 | DB 膨胀风险靠运维监控；后续如需可加大小/频次限流 | 运维成本 |

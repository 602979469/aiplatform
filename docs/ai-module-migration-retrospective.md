# AI 模块移植复盘（生成器 → aiplatform 全链路）

> 范围：ruoyi-ai（RuoYi-Cloud 的 DeepSeek 对话 + 镜像加速器模块）移植到 aiplatform 多模块 DDD 工程。
> 从生成器产骨架开始，到六层代码落地、独立前端页面、真实对话打通的全过程问题与改造点记录。

## 一、总体结论

- 生成器骨架质量不错：六层结构、命名、Convertor 显式映射、逻辑删除可配删除值、分页、`AiPlatformTemplate` 风格 Controller 都符合项目规范，**数据/业务层骨架基本可保留**。
- 但骨架是"通用 CRUD"思维，**不是业务特征接口**：5 张表全部生成了 web 层，能力/系统表这种内部表也暴露了 Controller；聊天这种"流式业务"（排序、重试、上下文）必须手工补。
- 真正的移植成本不在代码量（原模块 ~3,900 行），而在**框架适配**：RuoYi 的 BaseController/AjaxResult/SecurityUtils/@Transactional/日志/异常体系全部要换成 aiplatform 约定。
- 过程中踩到 3 个会"静默出错"的生成器坑（主键不回填、默认值被显式写 NULL、updateByCondition 静默跳过），均已修复。

---

## 二、生成器的坑（按踩坑顺序）

| # | 问题 | 现象 | 原因 | 处理 |
|---|---|---|---|---|
| 1 | 强约束 `create_time`/`update_time` | `gen.sh` 生成到 `ai_chat_message` 直接中断 | 原表没有 `update_time`（`ai_chat_message`、`sys_ai_message` 缺） | ALTER TABLE 补列，并同步幂等 DDL 脚本 |
| 2 | 生成代码引用不存在的错误码 | 全量编译失败：`ErrorCodeEnum.RESULT_NOT_UNIQUE` 找不到符号 | 生成器默认 `findOne` 用该错误码，但项目枚举里没有 | 在 `ErrorCodeEnum` 补 `RESULT_NOT_UNIQUE(30005)` |
| 3 | 生成器接管 `sql/` 目录 | 手工放的 `ai_chat.sql`/`ai_mirror.sql` 被清掉 | 运行时清空 sql/ 并写入自己的 `{表名}.sql` | 确认这是生成器行为，DDL 以数据库为准，手工脚本不再放 sql/ |
| 4 | 生成器重写 `generate.yaml` | 带注释的完整配置被规范化重写，注释和 example 表配置丢失 | 运行后按"当前库表"重写配置 | 5 张 AI 表配置和 outputDir 被保留，接受重写结果 |
| 5 | 生成 insert 显式写有默认值的列 | 新消息 `status` 落库为 NULL（覆盖了表默认值 `0`） | 生成的 INSERT 固定带 `status` 列，值为 `#{status}`（null） | 业务代码显式 `setStatus("0")`；生成器应改进（见"改进建议"） |
| 6 | 生成 insert 不带时间戳 | `create_time`/`update_time` 永远 NULL，前端时间空白 | INSERT 列清单里没有时间字段 | 手改 XML：`create_time=NOW(), update_time=NOW()` |
| 7 | 自增主键不回填入参 | 失败消息的 `messageId` 为 null，`status=1` 标记不生效（静默 bug） | 仓储 insert 返回新 Model，调用方原对象不变 | 改为用返回值：`userMessage = service.createAiChatMessage(userMessage)` |
| 8 | 查询排序固定主键 DESC | 聊天消息列表顺序颠倒 | 生成的 `selectList` 固定 `ORDER BY 主键 DESC` | 手加 `selectBySessionAsc`（按消息 ID ASC） |
| 9 | `updateByCondition` 只含主键时静默跳过 | 想用"touch 会话刷新 update_time"发现没效果 | XML 用 `<if>` 包裹整个 UPDATE，无业务字段时整条不执行 | 放弃该用法；生成器应改为报错或允许更新时间 |
| 10 | 内部表也生成 Controller | 能力/系统表多出 21 个 web 文件 | 配置时没给内部表设 `generateController: false`（生成器已支持该开关） | 手工删除全部通用 CRUD web 文件，保留特征接口 |
| 11 | 打包残留旧 target | 启动报 `GenTableColumnDO` 找不到 | 之前生成器跑过旧库（有 gen_table 表），target/classes 残留了对应 XML | `mvn clean package`（clean 是必要的） |

> 另外两个环境类问题（非生成器）：
> - 镜像搜索外网（xuanyuan.cloud）在本机不可达/超慢，接口本身正常。
> - 本地 `aiplatform` 库原本是 RuoYi-Vue 风格（32 张系统表，sys_menu 16 列），按用户要求清空重建为 5 张 AI 业务表；原 ai_chat.sql 的菜单 SQL（20 列）与该库结构不匹配，决定不建菜单。

---

## 三、生成器可以做得更好的地方（改进建议）

1. **INSERT 语义**：
   - 有默认值的列不要显式写（或显式写 DB 默认值），避免 `null` 覆盖默认；
   - 时间字段默认带 `create_time=NOW(), update_time=NOW()`（或表结构强制 `DEFAULT CURRENT_TIMESTAMP`）。
2. **主键回填**：`insert` 方法文档高亮"必须使用返回值"，或直接回填到入参对象（当前返回新 Model 的写法很容易被调用方误解）。
3. **失败不中断**：单表生成失败（如强约束缺失）时，报错后应继续生成其余表，而不是整个中断；报错里附带修复建议（如 `ALTER TABLE xxx ADD COLUMN update_time ...`）。
4. **错误码预检**：生成 `findOne` 前检查项目 `ErrorCodeEnum` 是否有 `RESULT_NOT_UNIQUE`，没有则提示，或把该错误码作为骨架初始化的一部分。
5. **排序可配**：`selectList` 的 `ORDER BY` 支持按表配置（列 + 升降序），消息流这类表就不用手工加查询了。
6. **sql/ 目录策略**：默认不清空手工文件，或把生成结果放 `sql/generated/`；清空行为至少打日志提示。
7. **generate.yaml 重写保护**：规范化重写时保留注释与未知 key，或提供 `--dry-run` 预览；也可以不重写文件，只提示用户手工增删。
8. **内部表提醒**：配置了无业务特征的表时，提示是否 `generateController: false`。
9. **`updateByCondition` 静默问题**：只含主键时抛参数异常（提示"没有可更新字段"），而不是静默 no-op。
10. **审计字段可配**：`create_by`/`update_by` 支持开关（当前 BaseModel 只有时间，ruoyi 原模型带审计人，移植时丢了 create_by，靠 user_id/user_name 兜底）。
11. **日志降噪**：仓储层每个 update/delete 都打 info + 影响行数，业务量上来是噪音，建议可配置或默认不打。

---

## 四、环境适配改造点（RuoYi-Cloud → aiplatform）

### 4.1 框架替换

| ruoyi-ai 写法 | aiplatform 落法 |
|---|---|
| `extends BaseController` + 返回 `AjaxResult` | `AiPlatformResult<T>` + `AiPlatformTemplate.execute/executeWithoutResult` + Callback |
| `@RequiresPermissions("ai:chat:list")` | 无权限体系：`UserContextFilter` 从 `X-User-Id`/`X-User-Name` 头取用户（缺省 1/admin），后续接真登录只改 Filter |
| `SecurityUtils.getUserId()/getUsername()` | `UserContext.getUserId()/getUserName()`（core-model ThreadLocal） |
| `throw new ServiceException("...")` | `AiPlatformException(ErrorCodeEnum.XXX)` + `AiPlatformInvoker` 条件校验 |
| `@Transactional`（删会话连带删消息） | `AiPlatformTransactionTemplate.execute/executeWithoutResult`（core-model 定义，common-dal 装配 Spring TransactionTemplate 执行器） |
| `LoggerFactory.getLogger` | `AiPlatformLoggerUtil`（按 `LogFileEnum` 分文件）；补了带占位符的 `error` 重载 |
| commons-io `IOUtils` / `StringUtils` / `CommandUtils` | Hutool `IoUtil`/`StrUtil`；`CommandUtil` 迁到 common-util/tools |
| `Executors.newCachedThreadPool` | `ThreadPoolUtil` + `ThreadPoolEnum.MIRROR_DOWNLOAD` |
| Nacos 配置 / 网关 / Sentinel | 全部并入 `bootstrap/application.yml`（`ai.deepseek`/`ai.xuanyuan`/`ai.chat`） |

### 4.2 分层落位

- **common-integration**（外部数据源，自持异常）：`DeepSeekProperties`、`DeepSeekClient`、`DeepSeekChatMessage`、`XuanYuanProperties`、`XuanYuanWebClient`、`AiExternalRestTemplateConfig`、`AiIntegrationException` + `AiIntegrationErrorCode`。
- **common-util**：`CommandUtil`、`MirrorFileUtil`、`AiChatProperties`、镜像下载线程池。
- **core-model**：领域对象（`AiChatResult`、`MirrorSearchResponse`/`MirrorImageResult`/`MirrorDownloadTask`）、`UserContext`、`ErrorCodeCarrier`、`AiPlatformExceptionResolver`（Hutool 解包 + 错误码同名映射）、`AiPlatformTransactionTemplate`。
- **common-dal**：生成器骨架 + 手补 `updateStatusById`/`deleteBySessionId`/`selectBySessionAsc`/`selectBySceneAndCode`。
- **core-repository**：生成器骨架 + `deleteWithMessages`（组合消息/会话仓储，事务由上层模板包裹）。
- **core-service**：生成器骨架 + `AiChatService`（会话解析/30 条上下文/重试标记/改名/模拟模式）、`AiCapabilityService.invoke`（能力机制落库）、`AiMirrorSearchService`、`AiMirrorDownloadService`。
- **biz-service-impl**：只留 `AiChatManager`、`AiMirrorManager` 两个对外编排；删掉生成器为 5 张表产出的 10 个通用 CRUD Manager 文件。
- **web**：`AiChatController`（/ai/chat/** 6 接口）、`AiMirrorController`（/ai/mirror/** 4 接口）、DTO/Checker/Assembler/`UserContextFilter`；删除 33 个通用 CRUD web 文件（保留 `AiChatSessionResponse`/`AiChatMessageResponse` 复用）。

### 4.3 关键设计决策

- **外部异常与业务异常解耦**：common-integration 只抛 `AiIntegrationException`（错误码 `DEEPSEEK_API_ERROR`/`AUTH_ERROR`/`TIMEOUT`…）；core-model 模板通过 `ErrorCodeCarrier` + Hutool `EnumUtil` 把同名错误码解析成业务异常，web 层/事务模板统一兜底，业务代码不再手写 try-catch 转换。
- **core-model 红线调整**：允许引入基础外部库（hutool-all，用于模板异常解析），仍禁止 Spring/MyBatis 依赖；事务执行器以接口抽象，Spring 装配在 common-dal。
- **common 包独立可复制**：common-integration 的客户端 + DTO + 异常自成一体（fastjson2 依赖仅在该模块）。
- **DeepSeek Key 来源**：application.yml > 环境变量 `DEEPSEEK_API_KEY` > `~/.codex/config.toml`（`[model_providers.deepseek]` 的 `experimental_bearer_token`），密钥不进仓库。
- **镜像"非成功即异常"**：按用户原则，外部调用非成功一律抛集成异常；搜索时单个仓库 tags 查询失败仍跳过该仓库，不影响整体结果。
- **前端**：整个 ruoyi-ui 复制到 `ui/`；另建 `ui` 独立壳（Vue2 + Element UI，无登录/菜单），原两个页面原样复用，`/ai` 代理到 8080，`v-hasPermi` 恒放行、最小 vuex 提供 avatar、svg-icon 映射为 Element 图标。

---

## 五、与原 ruoyi-ai 模块对比

| 维度 | ruoyi-ai（原） | aiplatform（现） |
|---|---|---|
| 结构 | 单微服务，37 个 Java 文件 ~3,556 行，service+mapper+domain 平铺 | 六层多模块，~70+ 文件（生成器骨架 + 手写特征代码），分层清晰 |
| 外部调用 | DeepSeekClient / XuanYuanWebClient 在 service.impl | common-integration 独立包，自持 DTO 与异常，可复制 |
| 异常 | `ServiceException` 字符串消息，调用方自己处理 | `AiPlatformException` + 错误码 + `AiIntegrationException(ErrorCodeCarrier)` + 模板自动解析转换 |
| 事务 | `@Transactional` | `AiPlatformTransactionTemplate`（跨表多写统一走模板） |
| 用户体系 | 网关透传 + `SecurityUtils` + `@RequiresPermissions` | `UserContext`（请求头，缺省 1/admin），无权限，接真登录只改 Filter |
| 配置 | Nacos（ruoyi-ai-dev.yml + 网关路由 + XSS 白名单） | `application.yml` 三段配置，无网关 |
| 数据库 | ry-cloud 库 5 张表 + sys_menu/角色授权 | aiplatform 库 5 张表 + 3 条能力种子数据（无菜单体系） |
| 前端 | ruoyi-ui 全量 SPA（登录/动态菜单/权限） | `ui` 独立双页面（无登录/菜单），直连后端 |
| 聊天功能 | 会话 CRUD / 30 条上下文 / 失败重试标记 / 首次提问改名 / 模拟模式 | 全部等价保留 ✓ |
| 镜像功能 | 搜索 + AI 版本匹配 + 下载任务 + 文件下载 | 全部等价保留 ✓（外网依赖相同） |
| 能力机制 | `sys_ai_capability`/`sys_ai_session`/`sys_ai_message` + invoke | 等价保留 ✓ |
| 行为差异 | 消息列表按 create_time asc；失败时返回 null 容错 | 按 message_id asc（等价）；外部失败抛集成异常（非成功即异常） |
| 审计字段 | 模型带 create_by/update_by | 用 user_id/user_name（生成器 BaseModel 无 create_by），如需可后续补 |

---

## 六、经验沉淀（给下一个模块）

1. 生成前先核对表结构满足强约束（主键、create_time/update_time），避免生成中断。
2. 内部表配置 `generateController: false`，web 层不会多出 7 个文件。
3. 生成后先 `mvn clean package` 再跑，避免旧 target 残留（尤其换过数据库）。
4. 生成器的 insert 不会回填主键到入参，统一"用返回值"。
5. 有默认值的列（status 等）在业务代码显式赋值，不要依赖 DB 默认（生成器 insert 会写 null）。
6. 外部客户端一律放 common-integration 并抛 `AiIntegrationException`，错误码与 `ErrorCodeEnum` 同名即被模板自动解析。
7. 跨表多写用 `AiPlatformTransactionTemplate`，禁止 `@Transactional`。

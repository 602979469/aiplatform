# 全仓代码审查修复报告（军规违规 + 设计问题 + 军规修订）

> 日期：2026-08-14　状态：**待执行**（本文档为修复方案，未改业务代码）
> 审查基准：AGENTS.md 当前工作区版本（含未提交的新增条款「Repository 之间禁止互相调用」）
> 范围：全仓 261 个 Java 文件（common-util / common-dal / common-integration / core-model / core-repository / core-service / biz-service-impl / web / bootstrap）

## 0. 结论摘要

机械合规度较高：无 `@Transactional`、`@Valid`、`@Autowired`、`var`、手写 LoggerFactory、空 catch、分层越界 import、Repository 互调。

问题集中在四类：

| 级别 | 类别 | 数量 |
|---|---|---|
| P0 | 敏感信息明文进日志；Mapper XML `.code` 查询 String 字段（运行期 bug） | 2 |
| P1 | 军规违规（嵌套调用/手写判空/手写 if+throw/多写无事务/Manager 组装 Model/魔法值/断言取反/javadoc） | 10 |
| P2 | 设计问题（重复代码/长事务/内存泄漏/副作用顺序/死代码） | 13 |
| R | 军规本身不合理，需修订 AGENTS.md | 9 |

---

## 1. P0 高危（建议立即修）

### 1.1 敏感信息明文进日志

**军规**：敏感数据严禁打印（§8 / §10「敏感信息泄漏」）。

**违规点**：

| 文件 | 行 | 泄漏内容 |
|---|---|---|
| `web/.../template/ApiTemplate.java` | 44 | 请求日志打印整个 `param`，`AuthLoginRequest`/`AuthRegisterRequest`/用户创建请求含 `password` 明文 |
| `web/.../template/ApiTemplate.java` | 74 | 结果日志打印整个 `result`，`AuthLoginResponse` 含 `tokenValue` |

**修复方案**：

1. 在 `common-util` 新增脱敏工具 `SensitiveLogUtil`（或复用现有 JsonUtil 扩展）：
   - 按字段名黑名单掩码：`password`、`oldPassword`、`newPassword`、`tokenValue`、`apiKey`、`authorization`，替换为 `******`；
   - 提供 `String mask(Object obj)`：Jackson 序列化 + 字段名遍历掩码，序列化失败兜底为类型名。
2. `ApiTemplate` 两处日志改为：

```java
LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "请求开始 接口信息={} 时间={} 请求参数={}",
        caller, startTime, SensitiveLogUtil.mask(param));
// ...
LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "请求结束 接口信息={} 时间={} 耗时={}ms 是否成功={} 返回值={}",
        caller, startTime, cost, success, SensitiveLogUtil.mask(result));
```

**验收**：登录/注册后日志中 `password`、`tokenValue` 均为 `******`；登录接口响应正常返回明文 token。

### 1.2 Mapper XML 用 `#{status.code}` 查询 String 字段

**军规**：§5.5「XML 直接引用 DalQuery 字段，不出现 `.code`」；§7「DalQuery 用数据库原始类型」。

**违规点**：DalQuery 中 `status`/`menuType`/`visible` 均为 `String`，XML 却生成 `#{status.code}`，一旦带过滤条件查询即触发 MyBatis 反射异常。

| 文件 | 行 |
|---|---|
| `common/dal/src/main/resources/mapper/AuthUserMapper.xml` | 27 |
| `common/dal/src/main/resources/mapper/AuthRoleMapper.xml` | 24 |
| `common/dal/src/main/resources/mapper/AuthMenuMapper.xml` | 30、33、36 |
| `common/dal/src/main/resources/mapper/AuthLoginLogMapper.xml` | 27 |

**修复方案**：

1. 上述 XML 全部改回直接字段引用：`#{status}`、`#{menuType}`、`#{visible}`。
2. 同步修正生成器模板（生成器实现不受军规约束，但产物必须一致），避免下次生成回退。
3. 在仓库自查清单 §9 增加一条：XML 中禁止出现 `#{}` 内带 `.code` 的写法（生成后检查）。

**验收**：`mvn -pl common/dal -am compile` 通过；启动后带 `status`/`menuType`/`visible` 过滤条件的查询（用户分页、菜单列表、登录日志分页）正常返回。

---

## 2. P1 军规违规修复

### 2.1 AssertUtil 条件里直接调 Repository / 手写 `!= null`

**军规**：§4.2 嵌套调用（禁止在 AssertUtil 条件参数里直接调用 Repository/Service/Manager/Convertor）；§4.1 判空豁免。

**违规点**：

| 文件 | 行 |
|---|---|
| `core/service/.../AuthServiceImpl.java` | 75 |
| `core/service/.../AuthUserAdminServiceImpl.java` | 65 |

**修复**：先局部变量接收，再用 Hutool 判空：

```java
AuthUser exists = authUserRepository.findByUsername(username);
AssertUtil.throwErrWhenTrue(ObjectUtil.isNotNull(exists), ErrorCodeEnum.USERNAME_EXISTS);
```

### 2.2 手写 if+throw 合并判空

**军规**：§10「手写 if+throw」；§4.1 判空豁免。

**违规点**：`AuthServiceImpl.java:62`：`if (user == null || !BCrypt.checkpw(...)) { writeLoginLog(...); throw ...; }`

**修复**：拆开「副作用」与「抛异常」：

```java
boolean loginFailed = ObjectUtil.isNull(user) || !BCrypt.checkpw(password, user.getPassword());
if (loginFailed) {
    writeLoginLog(null, username, LoginLogStatusEnum.FAIL, "用户名或密码错误");
}
AssertUtil.throwErrWhenTrue(loginFailed, ErrorCodeEnum.LOGIN_FAILED);
```

### 2.3 手写判空（批量）

**军规**：§4.1 判空豁免仅三处；其余一律 Hutool。

| 文件 | 行 | 修复 |
|---|---|---|
| `AuthRoleAdminServiceImpl.java` | 51-52 | `role.setRoleSort(ObjectUtil.defaultIfNull(role.getRoleSort(), 0)); role.setStatus(ObjectUtil.defaultIfNull(role.getStatus(), EnableStatusEnum.ENABLE));` |
| `AuthMenuAdminServiceImpl.java` | 60-67 | 同上模式（parentId=0L、orderNum=0、visible=SHOW、status=ENABLE） |
| `AuthUserAdminServiceImpl.java` | 70 | `user.setStatus(ObjectUtil.defaultIfNull(user.getStatus(), EnableStatusEnum.ENABLE));` |
| `AuthLoginListener.java` | 47、81、87-88 | `ObjectUtil.isNull(user)`；可空三元改 `ObjectUtil.defaultIfNull(...)` 或显式 Hutool 判空 |
| `UserContextInterceptor.java` | 15 | `if (ObjectUtil.isNotNull(loginId))` |
| `AuthUserRepositoryImpl.java` | findOne | `CollUtil.isEmpty(doList)` |

### 2.4 多写未走 BizTemplate

**军规**：§5.3「一个用例超过一次 INSERT/UPDATE/DELETE 必须使用 BizTemplate」。

**违规点**：`AiCapabilityServiceImpl.invoke`：1 次会话 insert + 3 次消息 insert，共 4 次写，无事务。

**修复方案（推荐两段式，避免外部调用占事务）**：

1. `BizTemplate.execute` 事务内：创建系统会话 + 写 system 消息 + 写 user 消息；
2. 事务提交后调 `deepSeekClient.chat(...)`；
3. 第二次写：成功后补 assistant 消息；失败则补一条失败 assistant 消息或记录失败状态（与 `AiChatServiceImpl.doChat` 的失败标记思路一致）。

> 若短期不想动结构，最低限度是整段包 `BizTemplate.execute`，但需同步接受「外部 HTTP 调用在事务内」的代价（见 P2-6）。

### 2.5 Manager 层 new Model + setter（DTO/Model 组装）

**军规**：§4.0 转换统一出口（业务层禁止 `new XxxModel()` + setter）；§5.2 Manager 不做 DTO 组装。

**违规点**：

| 文件 | 行 | 对象 |
|---|---|---|
| `biz/.../AiChatManagerImpl.java` | 50、57、72 | `AiChatSessionQueryParam`、`AiChatSession`（新建/更新） |
| `biz/.../AuthManagerImpl.java` | 39 | `AuthUserInfo` |

**修复**：下沉 core-service：

- `AiChatSessionService` 新增 `AiChatSession createDefaultSession(Long userId, String userName)`（内含默认名称/状态赋值）；
- `AuthService` 新增 `AuthUserInfo getCurrentUserInfo()`（内部完成 `AuthUserInfo` 组装）；
- Manager 只做编排与传参。

### 2.6 魔法值 / 裸错误码 / 常量位置

**军规**：§7 角色 key、默认值收口 core-model.constant；错误码集中 ErrorCodeEnum；禁止字符串裸错误。

| 文件 | 行 | 修复 |
|---|---|---|
| `AuthStpInterfaceImpl.java` | 34 | `"admin"` → core-model.constant 新增 `AuthRoleKeys.ADMIN` |
| `AuthServiceImpl.java` | DEFAULT_ROLE_KEY | `"common"` → 同收口到 `AuthRoleKeys.DEFAULT` |
| `AiMirrorDownloadServiceImpl.java` | 206 附近 | `"UNKNOWN"/"TIMEOUT"` → 新增 core-model 枚举（如 `MirrorTaskFailReasonEnum`）；`"docker.xuanyuan.run"` → 常量或 `XuanYuanProperties` 默认值 |
| `AiMirrorSearchServiceImpl.java` | 多处 | `"其他"`、`"多架构"`、`"支持 "` → 常量 |

### 2.7 断言取反

**军规**：§8「禁止断言参数取反」；§9 自查 #4。

**违规点**：`AuthRoleAdminServiceImpl.java:103`：`exists != null && !Objects.equals(...)`。

**修复**：判空改用 Hutool，业务取反显式命名：

```java
boolean roleKeyConflict = ObjectUtil.isNotNull(exists) && !Objects.equals(exists.getRoleId(), excludeRoleId);
AssertUtil.throwErrWhenTrue(roleKeyConflict, ErrorCodeEnum.ROLE_KEY_EXISTS, "角色标识已存在");
```

> 军规修订建议见 R6：业务语义的取反（如「排除自身」）应允许显式 `!`，禁止的是 `throwErrWhenFalse(!cond)` 这类双否定。

### 2.8 javadoc 缺失

**军规**：§8「非 @Override 方法，含 private，必须有 javadoc」。

**违规点**：`AuthUserConvertor.toDalQuery`、`AuthOnlineRepositoryImpl.toRedisDO/toSnapshot` 等无 javadoc；`AiMirrorDownloadServiceImpl` 多处 javadoc 首行缺 ` * `。

**修复**：补齐；同步修格式。若采纳 R6 修订，本批仅补 public 方法与有业务语义的私有方法。

---

## 3. P2 设计问题修复（不违反军规，按需执行）

| # | 位置 | 问题 | 建议方案 |
|---|---|---|---|
| 1 | `ApiTemplate.resolveCaller()` | 每请求遍历堆栈 | Controller 显式传接口名，或去掉该方法 |
| 2 | AuthRole/AuthMenu/AuthUser 三个 AdminServiceImpl | `checkResult()` 重复 3 份 | common-util 增加 `ResultUtil.throwIfFailed(Result)`，或 BizTemplate 提供 `executeOrThrow` |
| 3 | `AuthMenuServiceImpl` / `AuthMenuAdminServiceImpl` | `buildTree/buildChildren` 重复 2 份 | 抽 `MenuTreeUtil`（core-service 包内） |
| 4 | `AuthUserAdminServiceImpl` / `AvatarFileController` | `AVATAR_EXTS` 重复，且校验逻辑在 Controller | 常量收口 core-model.constant；文件名校验下沉 ParamChecker（二进制接口也在 beforeService 校验） |
| 5 | `AuthServiceImpl.register` | 事务内才查默认角色 | 事务外先查默认角色并校验，再开事务 |
| 6 | `AiChatServiceImpl` / `AiCapabilityServiceImpl` | 外部 HTTP 调用在事务内，长事务占连接 | 两段式重构（见 P1-2.4）：先落库，事务外调模型，再补写结果/失败标记 |
| 7 | `AuthSessionServiceImpl.listOnline` | 手写内存分页 + 全量拉取 | 抽 common-util `PageUtil.paginate(list, pageNum, pageSize)`；大数据量改 Redis SCAN 分批 |
| 8 | `AiMirrorDownloadServiceImpl.tasks` | 内存 Map 无 TTL/上限，重启丢状态 | 加 TTL 清理（定时任务或惰性清理），或落 Redis/DB |
| 9 | `AuthUserAdminServiceImpl.updateAvatar` | 先写文件后更 DB，失败留孤儿文件 | DB 更新成功后再写文件；或 catch 后删除已写文件补偿 |
| 10 | `AiPlatformExceptionHandler.handleException` | 先 ERROR+堆栈再解包业务异常 | 先沿 cause 解包；命中 CommonException 走业务分支不记 ERROR |
| 11 | `ErrorCodeEnum` | DEEPSEEK/XUANYUAN/AUTH/TIMEOUT/EXTERNAL_ERROR 与 AiIntegrationErrorCode 重复且零引用 | 删除 5 个死常量 |
| 12 | `RedisKeyConstant` vs `AiPlatformConstant` | `SATOKEN_TOKEN_PREFIX` 重复定义 | 保留 core-model 一份，删除 dal 重复项（或按 R8 明确归属） |
| 13 | `AuthUserAdminServiceImpl` / `AvatarFileController` | `/uploads/avatar/` 路径散落拼接 | 收口 core-model.constant（与 Controller mapping 保持一致） |

---

## 4. 军规修订建议（AGENTS.md）

| # | 条款 | 现状问题 | 修订建议 |
|---|---|---|---|
| R1 | §4.1 判空豁免 | 只列三个位置；common-dal/core-repository/integration 是否属「业务代码」未定义；可空映射三元强转 Hutool 反而绕 | 改为按语义场景豁免：可空字段映射、外部响应判空、参数默认值；并明确 common-dal/common-integration 判空可用直判 |
| R2 | §5.5 + §7 + 生成器模板 | 「XML 不出现 .code」与生成产物 `#{status.code}` 矛盾；DalQuery String + `.code` 运行必炸 | 三处对齐：DalQuery 保持原始类型 String，XML 用 `#{status}`，生成器模板同步改 |
| R3 | §5.3 事务 | 「一次用例超过一次写必须 BizTemplate」未区分数据源：Redis 写无法与 DB 同事务；外部调用包进事务是反模式 | 明确「多写」仅指同一 DB 连接资源；补充「外部 HTTP/RPC 调用不得置于事务内」 |
| R4 | §5.4 Repository | 「禁止互调」但 Repository 聚合粒度未定义（AuthUserRepositoryImpl 已跨 auth_user_role 多写） | 明确：一个 Repository 可管理一张主表及其从属关联表，禁止注入/调用其他 Repository |
| R5 | §12 测试 | 「业务模块不写 Mockito 单测」导致纯逻辑（版本匹配、pull 计数、密码校验）无快速反馈 | 允许 common-util/core-service 纯函数写 JUnit 单测；业务集成测试照旧走独立测试模块 |
| R6 | §8 javadoc | 「非 @Override 方法含 private 必须有 javadoc」过于教条，噪音注释多 | 改为：public/接口方法必须有 javadoc；私有方法仅「有业务语义」时要求 |
| R7 | §4 分页 | 只覆盖 DB 分页，内存/Redis 分页无统一出口 | 补充内存分页统一出口（`PageUtil.paginate`） |
| R8 | §5.5 / 新增 | 文件/IO 资源归属未定义（avatar 读写散落 service 与 Controller） | 新增条款：文件系统访问统一收口（FileStorageService 或工具类），web 只做流式响应 |
| R9 | §11 generate.yaml | 要求枚举列配置 `columns.type: enum`，但现有 generate.yaml 全部表无 columns 配置 | 明确生成器配置是否受军规约束；若保留条款则补全配置 |

---

## 5. 落地顺序与验收

### 批次划分

| 批次 | 内容 | 验证 |
|---|---|---|
| 1 | P0-1 敏感日志脱敏、P0-2 XML `.code` 修复 | 编译 + 登录/注册冒烟（日志无明文）+ 带过滤条件查询正常 |
| 2 | P1-2.1/2.2/2.3/2.6/2.7/2.8（判空、断言、常量、javadoc） | 编译 + 全量 `git diff` 复核 |
| 3 | P1-2.4/2.5（多写事务、Manager 下沉） | 编译 + AI 对话/能力调用联调（含失败重试） |
| 4 | P2 按需（重复代码、内存分页、任务 TTL、副作用顺序） | 编译 + 对应功能回归 |
| 5 | AGENTS.md 修订（R1-R9） | 文档评审 |

### 每次提交前自查（对齐 §9）

1. 是否新增了抛异常方式？—— 统一 AssertUtil / ofThrow。
2. 是否手写判空且不在豁免内？—— 统一 Hutool。
3. 是否在断言/三元里直接调业务 Bean？—— 先赋值局部变量。
4. 是否用 `!` 翻转断言？—— 显式命名条件变量。
5. 是否新增魔法值？—— 收口常量/枚举。
6. 是否跨模块 import 未声明依赖？—— 查 pom。
7. 是否在非允许层 new Model + setter？—— 下沉 service / Convertor。
8. 是否绕过 Repository 碰 Mapper/DO/Redis？—— 收口仓储。
9. 是否重复造工具？—— 先查 common-util / Hutool。
10. 用例是否多次写？—— 必须 BizTemplate（同一 DB 资源）。
11. 命名、注释、包结构是否合规。
12. 是否照抄同仓合规实现。
13. 是否在 Repository 里注入或调用其他 Repository。


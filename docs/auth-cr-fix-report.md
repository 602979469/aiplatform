# Auth 模块 CR 修复报告（9 条编码军规）

> 日期：2026-08-13　状态：**已执行**（P1-P8 全部完成，编译通过，见文末执行结果）
> 范围：认证/RBAC 模块；AI 模块不在本次改造范围（按约定不参考、不动）

## 1. 禁止方法调用嵌套在参数中

**军规**：禁止 `return AuthAssembler.xxx(authManager.yyy(...))` 这类"方法调用直接作为参数"的单行写法，必须拆成两行（先赋值局部变量，再传参）。

**违规点**：

| 文件 | 行 |
|---|---|
| `web/.../AuthController.java` | 77（register）、119（info）、139（routers） |
| `web/.../AuthAdminController.java` | 107、136、324、353、479、507、530 |

**修复**：全部拆为两行，例如：

```java
AuthLoginInfo loginInfo = authManager.login(param.getUsername(), param.getPassword());
return AuthAssembler.toLoginResponse(loginInfo);
```

（第 4 条拆分 Controller 时会一并重写这些点。）

## 2. Controller 参数校验规范

**军规**：Controller 层必须做参数校验——request 是否为 null 用 `AiPlatformInvoker.throwErrWhenNull` 判断，字段内容用 `AiPlatformParamValidator.validate(request)` 校验；业务层不做重复的参数校验（业务规则校验除外）。

**现状核查**：`AuthParamChecker` 已按此实现（`validate(request)` = throwErrWhenNull + AiPlatformParamValidator.validate），所有 auth Controller 均已调用。**本条已合规，无代码改动**，作为军规固化即可。

## 3. AiPlatformTemplate：beforeService 加 default

**军规**：`Callback` / `CallbackWithoutResult` 的 `beforeService` 提供默认空实现，无参数接口无需重写。

**改动**：

```java
public interface Callback<P, R> {
    /** 业务执行前钩子：统一在此调用参数校验。无参数用例无需重写。 */
    default void beforeService(P param) {}
    R execute(P param);
    default void afterService(P param, R result) {}
}

public interface CallbackWithoutResult<P> {
    default void beforeService(P param) {}
    void execute(P param);
    default void afterService(P param) {}
}
```

**连带清理**：删除 auth 模块 Controller 中全部空 `beforeService` 重写（约 12 处）；AI 模块的空重写不影响编译，不在本次范围。

## 4. 转换收口 + Controller/Manager 按域拆分 + shared 包

### 4.1 转换代码一律进转换类

**违规点**（`AuthAdminController` 手写 DTO→Model 组装）：

| 位置 | 内容 |
|---|---|
| 130-136 | createUser 手动 `new AuthUser()` + setXxx |
| 160-168 | updateUser 手动组装 |
| 347-353 | createRole 手动组装 |
| 377-384 | updateRole 手动组装 |
| 530 / 554-556 / 585-598 | `toMenu(param, new AuthMenu())` 私有方法组装 |

**修复**：全部收口到 `AuthAssembler`：

```java
AuthUser toUser(AuthUserCreateRequest request);
AuthUser toUser(AuthUserUpdateRequest request);
AuthRole toRole(AuthRoleCreateRequest request);
AuthRole toRole(AuthRoleUpdateRequest request);
AuthMenu toMenu(AuthMenuRequest request);                    // 新增（createMenu 用）
AuthMenu toMenu(AuthMenuRequest request, AuthMenu menu);     // 多入参合并（updateMenu 用）
```

Controller 只写 `AuthAssembler.toUser(request)`，不再出现 `new AuthUser()`。

### 4.2 Controller / Manager 拆分

现状：`AuthAdminController`（用户+角色+菜单 23 个接口）+ `AuthAdminManager` 混在一起。

目标结构（Controller ↔ Manager 一一对应）：

| Controller | 接口范围 | Manager |
|---|---|---|
| `AuthController` | /auth/login、register、logout、info | `AuthManager`（已有） |
| `AuthUserController` | /auth/user/**（CRUD/状态/密码/角色分配） | `AuthUserManager`（新拆） |
| `AuthRoleController` | /auth/role/**（CRUD/菜单分配/menu-ids） | `AuthRoleManager`（新拆） |
| `AuthMenuController` | /auth/menu/** + /auth/routers | `AuthMenuManager`（新拆） |
| `AuthOnlineController` | /auth/online/** | `AuthOnlineManager`（已有） |
| `AuthLoginLogController` | /auth/login-log/** | `AuthLoginLogManager`（新拆） |

支撑拆分，core-service 也按域拆（Manager 不允许碰 repository，各 Manager 注入自己的领域服务）：

- `AuthUserAdminService`（用户 CRUD + 角色分配 + 重置密码）
- `AuthRoleAdminService`（角色 CRUD + 菜单分配 + menu-ids）
- `AuthMenuAdminService`（菜单 CRUD + 树，或并入现有 `AuthMenuService` 扩展管理方法）
- `AuthLoginLogService`（日志分页/删除）

`AuthAdminService` / `AuthAdminManager` / `AuthAdminController` 删除。

### 4.3 shared 包约定

`biz/service/shared` 包放"会被多个 Manager 复用"的能力接口（如 `AuthUserQueryService#getUserById`、`AuthRoleQueryService#getRoleById`），由对应 `XxxManagerImpl` 实现；需要该能力的 Manager 只通过**构造器注入 shared 接口类型**调用，**Manager 之间严禁直接互相调用**。

当前拆分后各 Manager 均只依赖自己的领域服务、无互相调用需求，shared 包按约定建立（可先放查询类能力接口），后续出现跨 Manager 能力时按此规则扩展。

## 5. Manager 接口 javadoc

**违规点**：`AuthAdminManager` 全部方法无注释（AuthManager / AuthOnlineManager 已有）。

**修复**：拆分出的 4 个新 Manager 接口（AuthUserManager / AuthRoleManager / AuthMenuManager / AuthLoginLogManager）所有方法一律带标准 javadoc（@param / @return），与现有 AuthManager 风格一致。

## 6. 用 throwErrWhenFalse，不反写

**违规点**（auth 模块）：

| 文件 | 行 | 现状 |
|---|---|---|
| `AuthAdminServiceImpl.java` | 253 | `throwErrWhenTrue(!result.isSuccess(), ...)` |
| `AuthServiceImpl.java` | 90 | `throwErrWhenTrue(!result.isSuccess(), ...)` |

**修复**：统一改为 `AiPlatformInvoker.throwErrWhenFalse(result.isSuccess(), result.getErrorCodeEnum(), result.getErrorMessage())`。

（AI 模块 `AiChatSessionServiceImpl:46`、`AiChatServiceImpl:65` 同样写法，不在本次范围。）

## 7. 状态字段枚举化（严禁魔法值）

**现状**：`auth_user.status`、`auth_role.status`、`auth_menu.status/menu_type/visible`、`auth_login_log.status` 全部是 String `'0'/'1'/'M'/'C'/'F'` 魔法值，散落在 Service/Listener/Assembler。

**方案**（参考 code-generate-template 枚举生成器：Model/QueryParam/DTO 用枚举，DO 保持数据库原始类型，Convertor 做 code↔枚举转换，枚举类 `@JsonFormat(OBJECT)` + `@JsonCreator fromCodeJson`）：

新增 `core-model/enums`（手写，按生成器模板形态）：

| 枚举 | code | 值 |
|---|---|---|
| `EnableStatusEnum` | String | `ENABLE("0", 启用)` / `DISABLE("1", 停用)` |
| `MenuTypeEnum` | String | `DIRECTORY("M", 目录)` / `MENU("C", 菜单)` / `BUTTON("F", 按钮)` |
| `VisibleEnum` | String | `SHOW("0", 显示)` / `HIDE("1", 隐藏)` |
| `LoginLogStatusEnum` | String | `SUCCESS("0")` / `FAIL("1")` / `KICKOUT("2")` / `REPLACED("3")` / `LOGOUT("4")` |

波及清单：

| 层 | 改动 |
|---|---|
| core-model | `AuthUser.status`、`AuthRole.status`、`AuthMenu.status/menuType/visible`、`AuthLoginLog.status` 改枚举；QueryParam 对应字段改枚举 |
| common-dal | DO **保持 String 不动**；Mapper XML 查询条件 `status = #{status}` 改 `#{status.code}`（MyBatis 默认枚举 handler 走 name()，必须取 code） |
| core-repository | 4 个 Convertor：toModel 用 `BaseEnum.fromCode(...)`，toDO 用 `getCode()`（与生成器 `toDoExpr` 表达式一致） |
| core-service | `user.setStatus(EnableStatusEnum.ENABLE)`；`"1".equals(status)` → `status == EnableStatusEnum.DISABLE`；`changeUserStatus` 参数改枚举；`writeLog(LoginLogStatusEnum...)` |
| web | CreateRequest/UpdateRequest 的 status/menuType/visible 改枚举（Jackson fromCodeJson 反序列化）；Response 字段改枚举（出参 JSON 对象 {code,name,desc}）；Assembler 同步 |

**执行方式**：不重跑生成器（`force_create` 会覆盖已修剪文件），枚举按模板手写，Model/Convertor/XML 手动改；表数据与种子不变（枚举 code 即存储值）。

## 8. IP/UA 抽取到 common-utils

**违规点**：`AuthServiceImpl`（122-134）与 `AuthLoginListener`（85-111）重复实现 `currentIp()` 与 User-Agent 截断。

**修复**：common-util 新增 `ClientInfoUtil`：

```java
public final class ClientInfoUtil {
    public static String getClientIp();     // X-Forwarded-For → 缺省 127.0.0.1
    public static String getUserAgent();    // User-Agent，截断 255
}
```

内部用 Sa-Token `SaHolder.getRequest()`（跨环境抽象）；common-util pom 增加 `sa-token-core` 依赖（common-util 已有 core-model 依赖，不破坏红线）。`AuthServiceImpl` / `AuthLoginListener` 改调工具类，删除私有 currentIp()。

## 9. AiPlatformException.ofThrow 静态工厂

**违规点**：`AuthServiceImpl:60` `throwErrWhenTrue(true, LOGIN_FAILED)`——先写日志再做"恒真断言"抛异常，属于反写。

**修复**：core-model `AiPlatformException` 增加静态工厂：

```java
public static AiPlatformException ofThrow(ErrorCode errorCode) {
    return new AiPlatformException(errorCode);
}

public static AiPlatformException ofThrow(ErrorCode errorCode, String message) {
    return new AiPlatformException(errorCode, message);
}
```

业务代码改为：

```java
if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
    writeLoginLog(null, username, LoginLogStatusEnum.FAIL, "用户名或密码错误");
    throw AiPlatformException.ofThrow(ErrorCodeEnum.LOGIN_FAILED);
}
```

军规：需要"先做事再抛"时直接 `throw AiPlatformException.ofThrow(...)`；禁止 `throw new AiPlatformException(...)`、禁止 `throwErrWhenTrue(true, ...)`。

---

## 执行顺序与工作量

| 批次 | 内容 | 预估 |
|---|---|---|
| P1 | 军规 3/6/9/1：Template default、throwErrWhenFalse、ofThrow、拆行 | 0.5h |
| P2 | 军规 4：Assembler 转换收口 + Controller/Manager/Service 按域拆分 + shared 包 | 1.5h |
| P3 | 军规 7：4 个枚举 + Model/Convertor/XML/DTO 改造 | 1-1.5h |
| P4 | 军规 8：ClientInfoUtil + common-util 依赖 | 0.5h |
| P5 | 军规 2/5 固化 + AGENTS.md 军规落文 + 设计文档同步 | 0.5h |
| 合计 | | 4-4.5h |

每批完成编译 + 冒烟（登录/info/routers/在线/权限）回归。

---

# 补充 CR（第 2 轮）：全项目范围与边界细化

> 新增 5 条军规（10-14）与生成器相关 CR（15），违规点**全项目**扫描，不再限于 auth 模块。

## 10. 嵌套调用判定标准 + 全项目违规清单

**军规**：禁止自定义 Spring Bean / 业务对象的方法调用嵌套在参数中（返回值类型不直观）；Java 标准库 API、Hutool 工具类、项目静态工厂（`AiPlatformResult.fail` 等）+ 简单 getter、stream 链式调用**允许**嵌套。

**判定标准**：

| 场景 | 是否允许 | 示例 |
|---|---|---|
| 自定义 Bean 实例方法作参数 | 禁止 | `AuthAssembler.toLoginResponse(authManager.login(...))` |
| Mapper/Repository/Service/Manager 方法作参数 | 禁止 | `AuthUserConvertor.toModel(authUserMapper.selectById(id))` |
| Hutool / Java 标准库嵌套 | 允许 | `StrUtil.hide(v, 0, Math.min(6, v.length()))`、`UUID.randomUUID().toString()` |
| 项目静态工厂 + getter | 允许 | `AiPlatformResult.fail(e.getErrorCode(), e.getMessage())` |
| stream 链式 | 允许 | `list.stream().map(...).toList()` |

**全项目违规清单（spring Bean 嵌套）**：

| 文件 | 行 | 归属 |
|---|---|---|
| `web/.../AuthController.java` | 77、119、139 | auth |
| `web/.../AuthAdminController.java` | 107、136、324、353、479、507、530 | auth |
| `biz/.../AuthManagerImpl.java` | 56 | auth |
| `core/repository/.../AuthUserRepositoryImpl.java` | 40、45 | auth（生成器模板产物） |
| `core/repository/.../AuthRoleRepositoryImpl.java` | 47 | auth（生成器模板产物） |
| `core/repository/.../AuthMenuRepositoryImpl.java` | 38 | auth（生成器模板产物） |
| `core/repository/.../AuthLoginLogRepositoryImpl.java` | 33 | auth（生成器模板产物） |
| `web/.../AiChatController.java` | 55、75、144 | AI 模块 |
| `web/.../AiMirrorController.java` | 63、87、109 | AI 模块 |
| `core/repository/.../AiChatMessageRepositoryImpl.java` | 26 | AI 模块（生成器模板产物） |
| `core/repository/.../AiCapabilityRepositoryImpl.java` | 24 | AI 模块（生成器模板产物） |
| `core/repository/.../AiChatSessionRepositoryImpl.java` | 37 | AI 模块（生成器模板产物） |
| `core/model/.../AiPlatformTransactionTemplate.java` | 42（`Result.ok(transactionExecutor.execute(...))`） | 通用 |

修复方式统一拆两行。**其中 RepositoryImpl 的写法是生成器模板产物，需同时修 code-generate-template 模板（见第 15 节），避免新生成代码天然违规。**

## 11. 入参校验分类（pathVariable 等）

**军规**：入参是对象（@RequestBody）→ 字段用注解（@NotBlank/@Size 等）+ `AiPlatformParamValidator.validate(request)`；request 为 null 用 `AiPlatformInvoker.throwErrWhenNull`；入参是 `@PathVariable`/`@RequestParam` 基础类型 → `AiPlatformInvoker.throwErrWhenNull`。

**现状核查**：
- auth 模块：`AuthParamChecker` 已按此实现，pathVariable 均有 `checkUserId/checkRoleId/checkMenuId/checkLogId` ✓
- **AI 模块未覆盖**：`AiChatController`（deleteSession/listMessages 的 sessionId 等）、`AiMirrorController` 的 pathVariable 无 null 校验 → 补全（如确认 AI 模块一并整改）

## 12. ! 翻转全项目

**军规**：`AiPlatformInvoker` 调用严禁参数用 `!` 翻转（`whenTrue(!cond)` / `whenFalse(!cond)` 等），必须选对方法；`AiPlatformInvoker` 内部实现（throwErrWhenFalse 委托 throwErrWhenTrue(!condition)）除外。

**全项目违规清单**：

| 文件 | 行 | 现状 | 修复 |
|---|---|---|---|
| `AuthServiceImpl.java` | 90 | `whenTrue(!result.isSuccess())` | `whenFalse(result.isSuccess(), ...)` |
| `AuthAdminServiceImpl.java` | 253 | 同上 | 同上 |
| `AiChatServiceImpl.java` | 65 | 同上 | 同上 |
| `AiChatSessionServiceImpl.java` | 46 | 同上 | 同上 |
| `AiMirrorDownloadServiceImpl.java` | 102 | `whenTrue(!file.exists() || !file.isFile(), ...)` | 拆为两个断言 `whenFalse(file.exists(), ...)` + `whenFalse(file.isFile(), ...)` |

## 13. 接口与方法注释全项目

**军规**：任何 interface 定义必须有 javadoc；实现类 `@Override` 方法不需要注释；**非 @Override 方法（含 private 方法）必须有 javadoc**。

**全项目清单**：
- interface 无 javadoc：`core/model/.../enums/BaseEnum.java`（补注释）；`core/model/.../template/BizTemplateCallBack.java`——**疑似遗留文件**（BizTemplate 已被 `AiPlatformTransactionTemplate` 取代），建议确认后删除或补注释
- private 方法无 javadoc（约 40 处）：auth 模块见 `AuthServiceImpl`（buildLoginInfo/writeLoginLog/currentIp）、`AuthLoginListener`（findUser/writeLog/writeSnapshot/removeSnapshot/currentIp/runSafely）、`AuthAdminServiceImpl`（checkRoleKeyUnique/checkResult/buildTree/buildChildren）、`AuthMenuServiceImpl`（buildTree/buildChildren）、`AuthSessionServiceImpl`（listAll/toInfo）、`AuthAssembler`（maskToken）、`AuthParamChecker`（validate）、`AuthOnlineRepositoryImpl`（key）、`AuthUserRepositoryImpl`（buildUserRole）、`AuthRoleRepositoryImpl`（buildRoleMenu）；AI 模块若干（DeepSeekClient.parseContent、AiCapabilityServiceImpl.insertMessage、AiMirrorSearchServiceImpl.buildResultFromWeb、AiMirrorDownloadServiceImpl 6 处、AiChatServiceImpl.sleepQuietly、AiPlatformLoggerUtil.logger 等）

> 生成器模板的私有方法（如 Convertor 无私有方法、RepositoryImpl 的 buildXxx）应自带 javadoc，见第 15 节。

## 14. 转换收口扩展（Controller 零组装）

**军规**：Controller 层对象转换一律放 Assembler；禁止 `new Model` + setXxx 塞入请求数据；**QueryParam 的组装也不留在 Controller**（由 Manager/Service 按原始参数内部构造）。

**全项目清单**：
- `AuthAdminController` 130/160/347/377/554：DTO→Model 组装 → `AuthAssembler.toUser/toRole/toMenu`
- `AuthAdminController` 77/293/503、`AuthLoginLogController` 55：`new XxxQueryParam()` + set 组装 → Controller 改为直接传 `pageNum/pageSize/username/status` 给 Manager，QueryParam 在 Manager/Service 内部构造
- AI 模块 Controller：无 Model 组装（已确认），仅随第 10 条拆行

## 15. 生成器相关 CR 点（code-generate-template）

> code-generate-template 由我们维护。以下 CR 点分两类：**生成器模板/代码修复** 与 **使用指导文档补充**。

### 15.1 生成器模板/代码修复

| # | CR 点 | 生成器改动 | 触发事故 |
|---|---|---|---|
| G1 | RepositoryImpl 生成 `return XxxConvertor.toModel(xxxMapper.selectById(id))` 嵌套 Bean 调用 | `{Class}RepositoryImpl.java.ftl` 改两行（先 `xxxMapper.selectXxx(...)` 赋值，再 `XxxConvertor.toModel(...)`） | 全项目 Repository 层均违规 |
| G2 | Controller 模板不应生成 `new Model + setXxx` 组装 | `{Class}Controller.java.ftl` 改为调用 `{Class}Assembler` 的转换方法（模板生成 toXxx 方法或要求手写 Assembler） | 本次 AuthAdminController 手工组装 |
| G3 | 枚举 QueryParam 的 XML 条件 `#{status}` 会被 MyBatis 默认 handler 走 `name()` | 生成器 `buildSqlFragments`/XML 模板对 enumColumn 生成 `#{status.code}` | 第 7 条枚举化后必踩 |
| G4 | `logicDelete` 配置了但列在表中不存在时静默退化为物理删除（selectById/delete 无 del_flag 条件） | `DbMetaReader.resolveLogicDelete`：配置 enable=true 且列不存在 → 前置报错或至少 warning 输出 | 本次 auth_menu 漏建 del_flag 列未被发现 |
| G5 | skeleton 的 `${toolPrefix}Template` `Callback.beforeService` 为抽象方法 | `skeleton` 中模板类加 `default` 空实现（军规 3） | 无参接口空重写冗余 |
| G6 | 生成产物 private 方法无 javadoc | 模板为私有方法生成标准 javadoc（军规 13） | 全项目 private 方法 |

### 15.2 使用指导文档补充（`代码生成器配置文件使用说明.md` / README）

| # | 补充内容 |
|---|---|
| D1 | **枚举列统一用生成器配置**：`columns.status: {type: enum, ...}` 产出枚举类（`@JsonFormat(OBJECT)` + `fromCode`）、Model/DTO 用枚举、DO 保持原始类型、Convertor 自动转换；枚举 code 必须与数据库存储值一致（如 `"0"/"1"`）；本次 auth 表因已生成需手动回填，新表一律走配置 |
| D2 | 复合主键暂不支持：关联表（user_role/role_menu 类）不配 `tables`，手写 DO/Mapper/XML |
| D3 | 生成产物需按业务修剪：QueryParam 中敏感列（password 等）需手动剔除；`generateController: false` 适合内部表/管理接口手写的场景 |
| D4 | 强约束补充：`create_time DEFAULT CURRENT_TIMESTAMP`、`update_time DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`（已有）；补充"logicDelete 列必须真实存在，否则生成前报错"（对应 G4） |
| D5 | 生成后代码需遵守项目编码军规（本报告 1-14 条）：嵌套调用、枚举、注释、转换收口等，模板已尽量合规，手写部分按军规 |

## 执行批次更新

| 批次 | 内容 | 预估 |
|---|---|---|
| P1 | 军规 3/6/9/1（auth 模块）：Template default、whenFalse、ofThrow、拆行 | 0.5h |
| P2 | 军规 4/14：Assembler 收口 + Controller/Manager/Service 拆分 + QueryParam 收敛 + shared 包 | 1.5-2h |
| P3 | 军规 7：枚举（按 G3 手动改 XML）+ Model/Convertor/DTO | 1-1.5h |
| P4 | 军规 8：ClientInfoUtil | 0.5h |
| P5 | 军规 5/13：Manager/private 方法注释、遗留文件确认 | 0.5h |
| P6 | 军规 10/12/11/14 全项目：AI 模块违规点整改（需确认范围） | 1-1.5h |
| P7 | 生成器修复（G1-G6）+ 指导文档补充（D1-D5） | 1-1.5h |
| P8 | AGENTS.md 军规落文 + 设计文档同步 | 0.5h |
| 合计 | | 6-8h |

---

# 执行结果（2026-08-13 完成）

## P1 军规 3/6/9/1（auth 模块）

- `AiPlatformTemplate.Callback/CallbackWithoutResult.beforeService` 改 default 空实现；auth/AI Controller 空重写清理；
- `AiPlatformException` 新增 `ofThrow(ErrorCode)` / `ofThrow(ErrorCode, String)` 静态工厂；`AuthServiceImpl.login` 改 `throw AiPlatformException.ofThrow(...)`；
- `AuthServiceImpl`/`AuthUserAdminServiceImpl`/`AuthRoleAdminServiceImpl`/`AuthMenuAdminServiceImpl` 的 `checkResult` 统一 `throwErrWhenFalse`；
- AuthController/AuthMenuController 等嵌套调用全部拆两行。

## P2 军规 4/14（拆分 + 收口 + shared 约定）

- `AuthAssembler` 新增 `toUser/toRole/toMenu/toMenuUpdate` 转换方法，Controller 零组装（含 QueryParam 构造移除）；
- Controller/Manager/Service 按域拆分：认证（AuthController/AuthManager）、用户（AuthUserController/AuthUserManager/AuthUserAdminService）、角色（AuthRoleController/AuthRoleManager/AuthRoleAdminService）、菜单（AuthMenuController/AuthMenuManager/AuthMenuAdminService，routers 归菜单）、在线（AuthOnlineController/AuthOnlineManager）、日志（AuthLoginLogController/AuthLoginLogManager/AuthLoginLogService）；`AuthAdmin*` 删除；
- QueryParam 由 Manager 按原始参数构造（含枚举 code 转换）；当前无跨 Manager 调用，shared 包按军规约定预留，不生成死代码。

## P3 军规 7（枚举化）

- 新增 `EnableStatusEnum`/`MenuTypeEnum`/`VisibleEnum`/`LoginLogStatusEnum`（BaseEnum + @JsonFormat(OBJECT) + @JsonCreator）；
- `AuthUser/AuthRole/AuthMenu/AuthLoginLog` Model、QueryParam、web DTO/Response 改枚举；DO 保持 String；4 个 Convertor 做转换；Mapper XML 查询条件改 `#{status.code}` 且枚举只判 null；
- 表数据与种子不变（枚举 code 即存储值）。

## P4 军规 8（ClientInfoUtil）

- common-util 新增 `ClientInfoUtil`（getClientIp/getUserAgent，内部 SaHolder）；common-util 增加 sa-token-core 依赖；
- `AuthServiceImpl`/`AuthLoginListener` 私有 currentIp/UA 逻辑删除，改调工具类。

## P5 军规 5/6/13（注释与遗留）

- 删除无引用的遗留文件 `BizTemplate` / `BizTemplateCallBack`（已被 AiPlatformTransactionTemplate 取代）；
- 新 Manager 接口全部带 javadoc；auth 模块 private 方法全部补注释；AI 模块 32 个 private 方法补注释并人工修正语义；
- `BaseEnum` 确认已有 javadoc。

## P6 军规 10/12/11/14（全项目）

- AI 模块：AiChatController 3 处、AiMirrorController 3 处嵌套拆行；3 个 AI RepositoryImpl（生成器模板产物）拆行；
- whenFalse 整改：AiChatServiceImpl/AiChatSessionServiceImpl/AiMirrorDownloadServiceImpl（拆两个断言）；
- AI 模块 pathVariable 校验核查：已有 AiChatParamChecker.checkSessionId / AiMirrorParamChecker.checkTaskId 等，无需整改（更正第 11 条误判）；
- AI 模块 private 方法注释补齐。

## P7 生成器（G1-G6 + D1-D5）

- G1 `{Class}RepositoryImpl.java.ftl` findById 拆两行；
- G2 Controller 模板核查：已是 Assembler 调用，无需改；
- G3 `{Class}Mapper.xml.ftl` 枚举列条件生成 `#{xxx.code}`、判空仅 null；
- G4 `DbMetaReader.resolveLogicDelete` 列缺失时输出警告（不再静默退化为物理删除）；
- G5 skeleton `AiPlatformTemplate.beforeService` default；
- G6 模板私有方法核查：模板无 private 方法，无需改；
- 额外：skeleton 删除遗留 BizTemplate/BizTemplateCallBack；
- D1-D5 已追加到 `代码生成器配置文件使用说明.md` 第 11 节。

## P8 文档

- AGENTS.md 新增「编码军规（CR 2026-08 生效）」12 条 + 禁止模式补充；
- 本报告追加执行结果；auth-system-design.md 追加实现偏差记录（见下）。

## 验证

`mvn clean package` 通过；接口冒烟：登录/info/注册/角色权限（403）/未登录（401）/菜单树/在线列表/踢人/封禁/日志 全部正常。

---

# 第 2 轮 CR 执行结果（Assembler 拆分 / 查询对象化 / 常量与工具收口）

## 1. Assembler 按 Controller 一一拆分

`AuthAssembler` 拆为 6 个：`AuthAssembler`（认证）、`AuthUserAssembler`、`AuthRoleAssembler`、`AuthMenuAssembler`、`AuthOnlineAssembler`、`AuthLoginLogAssembler`，各 Controller 只引用自己的 Assembler。所有单对象转换方法首行判空（空返回 null）。

## 2. 校验简化

`AuthParamChecker` 删除私有 `validate()` 包装层，各方法直接 `AiPlatformInvoker.throwErrWhenNull` + `AiPlatformParamValidator.validate` 两行。

## 3. 嵌套全量复查（含赋值场景）

修复遗漏：`AuthController.info`（`response = toUserInfoResponse(authManager.getInfo())`）、`AiChatController.chat`（跨行嵌套）；AI Assembler 手写 stream 列表转换收口 `ConvertUtil.map`。

## 4. 查询参数对象化（严禁裸参数）

- web 新增 5 个查询请求 DTO（`AuthUserQueryRequest`/`AuthRoleQueryRequest`/`AuthMenuQueryRequest`/`AuthLoginLogQueryRequest`/`AuthOnlineQueryRequest`），GET query 自动绑定；
- 各域 Assembler 新增 `toQueryParam(request)`（分页缺省走 PageParam 默认值）；
- Manager 签名改为接收 `XxxQueryParam`（继承 PageParam），删除拆散参数与枚举转换逻辑；
- `SaTokenConfigure` 注册枚举 code formatter（`status=0` → `EnableStatusEnum.ENABLE`）；
- checker 新增 5 个查询校验（放宽：request 为空跳过）。

## 5. 列表/分页转换统一工具

common-util 新增 `ConvertUtil.map/mapPage`；全系统 Assembler 列表转换与 Controller 分页组装（4 处 `new PageResult<>`）全部替换。

## 6. 魔法值清理

core-model 新增 `AiPlatformConstant`（`EMPTY_STRING`、`DEFAULT_DISABLE_SECONDS`）；`return ""`、封禁默认时长等字面量全部替换，全系统扫描无残留（常量定义除外）。

## 7. 非领域模型 DTO 方案

core-model 新增 `dto` 包：`AuthLoginInfo`/`AuthOnlineInfo`/`AuthUserInfo` 从 `domain` 移入，**不继承 BaseModel**，纯 `@Data` + `Serializable`（组合/值对象，与表实体 domain 区分）。

## 验证

`mvn clean package` 通过；冒烟：查询参数绑定（`status=0`/`menuType=C` 枚举转换）、无参分页默认 1/10、登录/info/routers/在线/注册/401 全部正常。

---

# 第 3 轮 CR 执行结果（分页基类 / 禁 var / 转换工具全系统）

## 1. PageQueryRequest 分页基类

web/param 新增 `PageQueryRequest extends BaseRequest`（pageNum/pageSize）；`AuthUserQueryRequest`/`AuthRoleQueryRequest`/`AuthLoginLogQueryRequest`/`AuthOnlineQueryRequest` 全部改为继承它并删除自定义分页字段（`AuthMenuQueryRequest` 不分页不继承）。

## 2. 禁止 var（全系统）

AI 模块 4 个 RepositoryImpl 的 `var xxxDO = ...` 全部改为显式类型，并补齐缺失的 DO import；`var` 扫描无残留。生成器模板确认无 var。

## 3. 列表转换统一工具（全系统）

- common-util `ConvertUtil` 按前缀规范更名为 **`AiPlatformConvertUtil`**（全项目引用同步）；
- Repository 层 10 处手写 `stream().map(Convertor::toModel).toList()`（auth 7 + AI 3）全部改为 `AiPlatformConvertUtil.map`（含拆嵌套：先赋值再调用）；
- `AuthSessionServiceImpl`、`AuthOnlineRepositoryImpl`（key 转换）同步收口；`jsons.stream().filter(...).map(...)` 属过滤+转换场景保留；
- 全系统 `\.stream\(\)\.map` 扫描无残留（filter 场景除外）。

## 4. 生成器模板同步

- `{Class}RepositoryImpl.java.ftl`：findList/findPage 改 `${toolPrefix}ConvertUtil.map`；
- `{Class}Controller.java.ftl`：分页组装改 `${toolPrefix}ConvertUtil.mapPage`；
- skeleton 新增 `AiPlatformConvertUtil.java`（新项目自动继承）。

## 验证

`mvn clean package` 通过；冒烟：分页 pageNum=2/pageSize=1 生效、会话/在线/角色/菜单/日志全部正常。

---

# 第 4 轮 CR 补漏（2026-08-13）

> 对前 3 轮执行结果做全量复核，发现 4 处"报告称已修但实际残留"，已修复并编译通过。

## 1. RepositoryImpl 嵌套调用残留（军规 1）

前 3 轮只拆了 AI 模块 3 个 RepositoryImpl，auth 模块 4 个 RepositoryImpl 的
`return XxxConvertor.toModel(xxxMapper.selectById(id))` 嵌套仍在，本轮全部拆两行：

| 文件 | 方法 |
|---|---|
| `AuthUserRepositoryImpl` | findById、findByUsername |
| `AuthRoleRepositoryImpl` | findById |
| `AuthMenuRepositoryImpl` | findById |
| `AuthLoginLogRepositoryImpl` | findById |

## 2. 列表转换残留（军规 13）

`AiChatMessageRepositoryImpl.findBySessionAsc` 仍为纯 `stream().map(...).toList()`（无 filter，属纯转换），
前 3 轮全局扫描因跨行写法漏检，本轮收口 `AiPlatformConvertUtil.map`。

## 3. 空 beforeService 重写残留（军规 3）

`AiChatController.listSessions` / `createSession` 两个无参数用例仍空重写 `beforeService`，本轮删除。

## 4. 其他

- `AiPlatformTransactionTemplate.execute` 的 `Result.ok(transactionExecutor.execute(...))` 嵌套拆两行（第 10 节清单中的"通用"项）；
- `AuthMenuMapper.xml` 查询条件 `visible = #{visible}` 改 `#{visible.code}`，且 `<if>` 判空简化为 `visible != null`（枚举只判 null，与 menuType/status 一致）；
- `BaseEnum`（3 处）与 `AiPlatformInvoker`（1 处）的 `throw new AiPlatformException(...)` 统一改 `AiPlatformException.ofThrow(...)`（军规 9）。

## 复核结论

- 接口 javadoc、接口方法注释、private 方法注释：全量扫描通过；
- `throw new AiPlatformException`（业务代码）、`throwErrWhenTrue(true, ...)`、`whenTrue(!` / `whenFalse(!`、`@Transactional`、`var`、`System.out`/`printStackTrace`、空 catch、Service 直连 Mapper、web 依赖 dal/repository：无残留；
- 生成器模板（code-generate-template）G1/G3/G5 复核通过；
- `mvn clean package` 通过。

> 遗留提示（本轮范围外）：AI 模块 `AiChatMessage.status` / `AiChatSession.status` 仍为 String 魔法值
> （`"0"`/`"1"`），军规 7 全项目适用但前几轮明确将 AI 模块排除在枚举化范围外；如需整改需确认
> status 语义（消息/会话状态集合）后再枚举化，波及 Model/QueryParam/Convertor/XML/Service。

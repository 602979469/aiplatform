# REFACTOR_PLAN.md — AGENTS.md 改造点清单

> 本文件是当前代码迁移到新 `AGENTS.md` 的工作清单，不是军规。
> 执行代码迁移时，业务代码必须遵守 `AGENTS.md`；本文件只说明“从现状改到哪里”。

## 目标

把现有 aiplatform 代码从旧分层迁移到新 `AGENTS.md` 定义的依赖方向和职责边界。

核心原则：

- `common-util` 成为最底层基础模块，不依赖任何内部业务模块。
- `core-model` 依赖 `common-util`。
- `common-dal` 不再依赖 `core-model`。
- `common-integration` 不再依赖 `core-model`。
- `core-repository` 只对外暴露 core-model。
- `core-service` 承载业务规则和 `BizChecker`。
- web 统一 `ApiTemplate + ApiResult + ParamChecker + Assembler`。

## 额外代码改造点（暂不写入 AGENTS）

- `Assembler`、`Convertor` 所有方法第一行先判空，空则直接返回 null。
- 查询类转换方法若入参为空，返回一个 `new` 空对象。
- 列表转换不要写 `toXxxList` 这类方法；Controller 直接使用 `ConvertUtil.map(...)`。

## 实施顺序建议

1. 先落地 common-util 基础类型。
2. 再迁移 common-dal。
3. 再调整 core-model。
4. 再调整 core-repository。
5. 再调整 core-service。
6. 再调整 common-integration。
7. 最后调整 biz 和 web。

## 改造点

### R1：common-util 成为底层基础模块

现状：

- `common-util` 依赖 `core-model`。

目标：

- `common-util` 不依赖 `core-model`。
- `core-model` 依赖 `common-util`。

涉及：

- `common/util/pom.xml`
- `core/model/pom.xml`
- 各模块 pom 依赖方向。

验收：

- `common-util` 中不存在 `com.jakt.aiplatform.core.model` import。
- `core-model` 可依赖 `common-util`，不形成循环。

### R2：common-util 承载通用基础类型

迁移到 common-util：

- `ErrorCode`
- `CommonErrorCode`
- `CommonException`
- `Result<T>`
- `PageResult<T>`
- `LogFileEnum`
- `LoggerUtil`
- `AssertUtil`
- `ConvertUtil`
- `ParamValidator`
- `TransactionTemplate`
- `BizTemplate`

现状对应：

- `AiPlatformInvoker` → `AssertUtil`
- `AiPlatformLoggerUtil` → `LoggerUtil`
- `AiPlatformConvertUtil` → `ConvertUtil`
- `AiPlatformParamValidator` → `ParamValidator`
- `AiPlatformTransactionTemplate` → `BizTemplate`
- `Result/PageResult` 从 core-model 迁入 common-util
- `LogFileEnum` 从 core-model 迁入 common-util

验收：

- common-util 没有 `AiPlatform` 前缀的基础工具。
- 工具方法签名符合军规。

### R3：错误码和异常改为字符串 code

现状：

- `ErrorCodeEnum` 使用 int code。
- 异常对象持有 ErrorCode。
- 存在 `ErrorCodeCarrier` 和 `AiPlatformExceptionResolver` 字符串映射。

目标：

- `ErrorCode.getCode()` 返回 String，且等于枚举名。
- `CommonException` 只保存 `String errorCode` 和 `String errorMessage`。
- `ErrorCodeEnum` 常量名即对外 code。
- 移除 `ErrorCodeCarrier`、`AiPlatformExceptionResolver`。
- `ApiResult.errorCode` 为 String。

涉及：

- `common-util` 错误码/异常基础类型
- `core-model` `ErrorCodeEnum`
- `core-model` `AiPlatformException`
- `common-integration` `AiIntegrationException`
- web `ApiResult` / `ApiTemplate` / `GlobalExceptionHandler`

验收：

- 前端响应中 `errorCode` 为 `"USER_NOT_EXIST"` 这类枚举名，不再是数字。

### R4：BizTemplate 纯静态，事务能力由 TransactionTemplate 提供

现状：

- `core-model` 中 `AiPlatformTransactionTemplate` 同时做事务和异常包装。

目标：

- `common-util.TransactionTemplate`：事务接口。
- `common-dal`：Spring `TransactionTemplate` 实现。
- `common-util.BizTemplate`：纯静态，负责 callback、异常分类、Result 包装。

方法：

```java
execute(callback)
execute(transactionTemplate, callback)
executeWithoutResult(callback)
executeWithoutResult(transactionTemplate, callback)
```

验收：

- 业务代码不再直接 new 或注入 `BizTemplate`。
- 单写直接 Repository，多写用 `BizTemplate.execute(transactionTemplate, callback)`。

### R5：common-dal 不再依赖 core-model

现状：

- Mapper 依赖 core-model `XxxQueryParam`。
- 事务配置依赖 core-model `AiPlatformTransactionTemplate`。

目标：

- `common-dal` 只依赖 common-util 和持久化框架。
- 查询参数改为 `XxxDalQuery`，多表结果用 `XxxDalResult`，分页用 `DalPageQuery`。
- 事务配置实现 common-util `TransactionTemplate`。

涉及：

- `common/dal/pom.xml`
- `common/dal` 下所有 Mapper、DO、XML
- `common/dal/config`

验收：

- `common-dal` 无 `core-model` import。
- Mapper/XML 只引用 `XxxDalQuery` 和 DO/DalResult。

### R6：common-dal Redis 只做通用 KV

现状：

- `RedisKeyConstant` 含业务 key。
- Redis 方法直接使用业务前缀。

目标：

- common-dal 提供通用 `RedisClient`。
- key 和 TTL 由上层传入。
- 业务 key 常量由 core-service 传入，或在 core-model 定义。

验收：

- common-dal 没有 `aiplatform:online:` 这类业务 key。

### R7：core-model 只保留领域语义

目标：

- 保留 `domain/enums/param/dto/exception/context/constant`。
- 移除 `result/template/util` 等基础设施类。
- 依赖 common-util。

验收：

- core-model 无 Spring/MyBatis/Redis import。
- core-model 无 `Result/PageResult/LoggerUtil/BizTemplate`。

### R8：core-repository 只对外暴露 core-model

目标：

- Repository 接口出入参全部 core-model。
- `XxxConvertor` 负责：
  - `DO ↔ Model`
  - `DalQuery ↔ QueryParam`
  - `DalResult ↔ Model`
- Convertor 允许互相调用。
- update/delete 返回 int，insert 按表主键类型返回。
- findOne 多条才抛 `RESULT_NOT_UNIQUE`。

验收：

- Repository 接口无 DO/DalQuery/DalResult 暴露。
- RepositoryImpl 不写字段映射。

### R9：core-service 增加 BizChecker

目标：

- 业务校验集中到 `core.service.checker`。
- `XxxService` → `XxxBizChecker`。
- BizChecker 是 Spring Bean，方法 void。
- 支持 `checkXxx(id)` 和 `checkXxx(entity)` 重载。
- 条件失败用 `AssertUtil`，无条件失败用 `AiPlatformException.ofThrow`。

验收：

- BizChecker 不返回数据、不回填。
- 业务规则不散落在 Service/Manager/Controller。

### R10：common-integration 不依赖 core-model

目标：

- `AiIntegrationException extends CommonException`。
- 集成错误码枚举 `getCode()` 返回枚举名。
- 所有集成异常记 `LogFileEnum.INTEGRATION`。
- 日志统一 `LoggerUtil`。

验收：

- common-integration 无 core-model import。

### R11：web 层统一 ApiTemplate/ApiResult

目标：

- `ApiTemplate` 统一 Controller 执行。
- `ApiResult<T>` 作为统一返回体。
- 字段：
  - success
  - errorCode
  - errorMessage
  - data
- Controller 不写 `@Valid`。
- 参数校验统一在 beforeService 调 ParamChecker。
- DTO 可持有 core-model 领域对象和枚举。
- Assembler 只做转换。

验收：

- Controller 无 try-catch、业务判断、`new Model`、读 Header。

### R12：配置与日志落地

目标：

- 配置类保持现状，配置值集中在 bootstrap 配置中。
- 日志代码只传 `LogFileEnum`，落库/落文件由 logback 配置。
- `INTEGRATION` 同时抄送 `COMMON_ERROR`。

验收：

- 业务代码不写日志落盘逻辑。

## 检查方式

每个改造点完成后至少检查：

1. 模块 pom 依赖是否符合新方向。
2. import 是否符合模块边界。
3. 命名是否符合军规。
4. 异常、日志、分页、转换是否走统一出口。
5. 是否出现魔法值或等价绕过。

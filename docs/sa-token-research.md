# Sa-Token 调研与接入方案（aiplatform）

> 调研时间：2026-08-13。目标：评估 Sa-Token 是否满足本项目「登录注册 + 安全框架 + 基础 RBAC（用户/角色/菜单，无数据隔离）」需求，并给出逐模块接入工作量。

## 一、结论速览

1. **Sa-Token 满足认证 + 授权校验层需求**：登录/登出、会话管理、权限/角色注解校验、无状态 token 等能力齐全且成熟；**v1.45.0（2026-03-08）官方新增 `sa-token-spring-boot4-starter`，正式支持 Spring Boot 4**，与项目 Spring Boot 4.0.6 完全匹配，无兼容风险。
2. **Sa-Token 不提供「菜单管理」和「用户/角色/菜单的表 + CRUD + 管理页」**：它只做"登录态 + 权限校验"（StpInterface 由业务实现加载角色/权限码）。菜单树、角色分配、管理页面属于业务代码。
3. **登录注册**：Sa-Token 管"登录态"（`StpUtil.login()` 签发 token），注册是纯业务（插用户表 + 密码加密 + 默认角色）。
4. **RBAC**：Sa-Token 提供 `@SaCheckPermission`/`@SaCheckRole`/`@SaCheckLogin` 注解 + `StpInterface`（getRoleList/getPermissionList）做校验层；数据模型（5 张表）与分配逻辑自己建。
5. **可借鉴参考**：dromara/RuoYi-Vue-Plus（MIT，基于 Sa-Token + MyBatis-Plus + Vue3）有完整的用户/角色/菜单管理实现，可借鉴表模型与 getRouters 思路；但它技术栈与我们有差异（MyBatis-Plus vs 原生 MyBatis、Vue3+TS vs Vue2），照搬代码不划算。

## 二、能力清单

| 能力 | Sa-Token 提供 | 成熟度 |
|---|---|---|
| 登录/登出 | `StpUtil.login/logout`、token 签发与校验 | 核心能力，稳定 |
| 会话管理 | 同账号多端/单端策略、踢人下线、封禁、记住我 | 稳定（v1.45 新增重复登录处理策略） |
| 无状态 token | 默认内存/Redis 存储；`sa-token-jwt` 插件可无状态 | 稳定 |
| 权限校验 | `@SaCheckPermission`/`@SaCheckRole`/`SaInterceptor` 路由拦截 | 稳定 |
| 角色/权限数据源 | `StpInterface` SPI（业务实现从库加载） | 稳定 |
| 菜单管理 | **不提供**（业务自己建菜单表 + CRUD + 页面） | — |
| 用户/角色管理 | **不提供**（业务自己建） | — |
| 注册 | **不提供**（业务代码：插用户 + BCrypt + 默认角色） | — |
| 密码加密 | 不内置 BCrypt；可用 Hutool `BCrypt`（项目已有 hutool-all） | — |
| 验证码/防爆破 | 不内置；简单验证码可手写或用 Hutool Captcha | 可选 |
| SSO/OAuth2/多租户 | 插件齐全（sa-token-sso / sa-token-oauth2），本项目不需要 | — |

## 三、Spring Boot 4 兼容性（关键结论）

- Sa-Token v1.45.0（2026-03-08）发布说明明确：**新增 `sa-token-spring-boot4-starter` / `sa-token-reactor-spring-boot4-starter` 集成包，支持 Spring Boot 4 环境**，并附带 `sa-token-demo-springboot4` 示例。
  - 来源：https://github.com/dromara/Sa-Token/releases/tag/v1.45.0
- 官方文档（SpringBoot 集成示例）写明：Boot 3.x 用 `sa-token-spring-boot3-starter`，**Boot 4.x 用 `sa-token-spring-boot4-starter`**。
  - 来源：https://sa-token.cc/doc.html#/start/example
- 版本/License：当前最新 1.45.0（Maven Central），**Apache 2.0**（来源：repo1.maven.org sa-token-parent POM；deps.dev sa-token-core）。
- 结论：直接引入 `cn.dev33:sa-token-spring-boot4-starter:1.45.0` 即可，无需手工适配。

## 四、RBAC 边界：Sa-Token 提供什么、业务要写什么

Sa-Token 的授权模型：
1. 业务实现 `StpInterface`（`getPermissionList` / `getRoleList`），从数据库（用户→角色→菜单 perms）加载。
2. 业务在接口上标注 `@SaCheckLogin` / `@SaCheckPermission("ai:chat:add")` / `@SaCheckRole("admin")`，或配置 SaInterceptor 路由规则。
3. 未登录抛 `NotLoginException`，无权限抛 `NotPermissionException` / `NotRoleException`，全局异常处理器转成统一返回体。

**表模型与菜单树、角色分配、管理页面全部是业务**。参考实现：
- RuoYi-Vue-Plus（MIT）：https://github.com/dromara/RuoYi-Vue-Plus
- 其 RBAC 表模型（sys_user / sys_role / sys_menu / sys_user_role / sys_role_menu）+ getRouters 动态路由 + 菜单管理页是成熟范式；本项目去 sys_ 前缀、去掉部门/数据权限，5 张表即可。

## 五、注册与密码方案

- Sa-Token 无注册接口：注册 = 校验用户名唯一 → 密码用 **Hutool `BCrypt.hashpw`** 加密 → 插 `auth_user` → 绑定默认角色（`auth_user_role`）。
- 登录 = 按用户名查用户 → `BCrypt.checkpw` 校验 → `StpUtil.login(userId)` → 返回 token。

## 六、本项目接入工作量（逐模块）

表：`auth_user` / `auth_role` / `auth_menu` / `auth_user_role` / `auth_role_menu`（5 张，可抽本地 RuoYi 单机版 SQL 改前缀 + 去部门/岗位列），加进 generate.yaml 生成数据层骨架。

| 模块 | 新增文件 | 手写量 | 内容 |
|---|---|---|---|
| bootstrap | pom + yml | ~0.5h | `sa-token-spring-boot4-starter:1.45.0`；`sa-token.token-name/timeout/is-concurrent` 等配置 |
| core-model | 5 Model + 5 QueryParam（生成器）+ 错误码 | 1-2h | ErrorCodeEnum 加未登录/无权限/账号密码错误等；模型生成器产出 |
| common-dal | 15 个生成（DO/Mapper/XML）+ 手写 SQL | 2-3h | `selectByUsername`、`selectRolesByUserId`、`selectPermsByUserId`、`selectMenusByUserId`（join 角色） |
| core-repository | 15 个生成 + 手写包装 | 2h | 按用户名查用户、按用户查角色/权限/菜单树 |
| core-service | ~5 个 | 4-6h | AuthService（登录/注册/登出）、MenuService（菜单树/perms/路由）、**StpInterface 实现** |
| biz-service-impl | 2 个 | 1-2h | AuthManager 编排（login/register/logout/getInfo/getRouters） |
| web | ~10 个 | 4-6h | AuthController（/auth/login、/register、/logout、/info、/routers）、SaTokenConfigure（SaInterceptor + 放行 /auth/**）、异常处理（NotLogin→401、NotPermission→403）、DTO/Checker/Assembler、**UserContextFilter 数据源改为 StpUtil**（AI 模块零改动） |
| ui-standalone | ~8-12 个 | 6-10h | login/register 页、侧边菜单 Layout、request.js 带 token + 401 跳转、store、路由守卫、v-hasPermi/$auth 接真实权限；AI 两个页面零改动 |
| 用户/角色/菜单管理页（可选） | 后端 ~15 个 + 前端 3 页 | 1-1.5 人日 | 菜单树 CRUD、角色分配菜单、用户分配角色；自己用可先用 SQL 维护 |

**合计**：核心（登录注册 + 权限校验 + 菜单树接口 + 前端外壳）约 **1.5-2.5 人日**（后端手写 ~2,000-2,500 行 + 生成器白给 ~35 文件，前端 ~1,200-2,000 行）；管理页另加 **1-1.5 人日**。

## 七、落地步骤

1. 建 5 张 auth_* 表 + 种子（admin 用户/角色/AI 菜单），加 generate.yaml 生成骨架
2. bootstrap 引入 sa-token-spring-boot4-starter + yml 配置
3. common-dal/repository 手写 4 个查询 + 包装
4. core-service：AuthService + MenuService + StpInterface 实现
5. biz：AuthManager；web：AuthController + SaTokenConfigure + 异常处理 + UserContextFilter 改造
6. ui-standalone：登录/注册/布局/守卫，AI 两页面挂菜单
7. 现有 AI 接口加 `@SaCheckLogin`（或 SaInterceptor 统一拦截），AI 页面 token 化

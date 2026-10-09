# Dify 工作流接入（AI 能力提供方扩展）

> 一句话：**把 Dify 当"函数服务器"，不要当业务平台**。一个 Dify 工作流 = 一个函数（输入 JSON → 输出 JSON），
> 通过能力表接进 aiplatform，业务代码只认 `sceneCode + capabilityCode`。

## 1. 为什么这样接

aiplatform 原本只有一个 AI 出口：

```
业务层 → AiCapabilityService.invoke(sceneCode, capabilityCode, input)
            └─ sys_ai_capability（skill_rules = system 提示词）→ DeepSeekClient
```

接入 Dify 后，`sys_ai_capability` 多了一列 `provider`，同一个出口按 provider 分发：

```
业务层 → AiCapabilityService.invoke(sceneCode, capabilityCode, input)
            ├─ provider=DEEPSEEK → DeepSeekClient.chat(...)      （原来的路，不动）
            └─ provider=DIFY     → DifyWorkflowClient.runBlocking(...) → POST {base}/v1/workflows/run
```

好处：**换提供方不用改业务代码**，前端也不用动；判分、推荐这类业务只认能力码。

## 2. 配置

### 2.1 应用配置（application.yml）

```yaml
ai:
  dify:
    base-url: ${DIFY_BASE_URL:https://dify.jakt.online/v1}   # Service API 地址，注意带 /v1
    api-key: ${DIFY_API_KEY:}                                # 默认 API Key（app-xxx），可留空
    connect-timeout: 10
    read-timeout: 180                                        # 工作流可能多步调模型，给宽一点
```

### 2.2 能力表（sys_ai_capability）

| 列 | 说明 |
|---|---|
| `provider` | `DEEPSEEK`（默认）/ `DIFY` |
| `provider_config` | DIFY 时的 JSON 配置；**可能含 API Key，禁止出现在响应/日志里** |

`provider_config` 结构（对应 `DifyCapabilityConfig`）：

```json
{
  "apiKey": "app-xxxx",          // 可留空，留空用 ai.dify.api-key
  "inputVariable": "topic",      // 我们的字符串入参映射到工作流哪个变量，默认 input
  "outputVariable": "report",    // 取哪个输出变量作为返回值；留空取第一个
  "fixedInputs": { "audience": "开发者", "count": 3 }   // 除入参外固定传入的变量
}
```

## 3. 接入一个新 Dify 工作流（3 步）

1. **在 Dify 里建 Workflow**（不是 Chatflow、不是 Agent）：
   - 开始节点定好输入变量（例如 `question` / `answer` / `reference`）
   - 结束节点定好输出变量（例如 `score` / `comment`）
   - 输出尽量约束成 JSON，后端解析更稳
   - 在「运行」面板手动喂一组样例，确认输出对
2. **建 API Key**：应用 → 访问 API → API 密钥 → 创建；生产建议放到环境变量 `DIFY_API_KEY`，不要写进仓库
3. **插一行能力**：

```sql
INSERT INTO sys_ai_capability
  (scene_code, capability_code, capability_name, description, provider, provider_config, status, create_by, create_time, update_time)
VALUES
  ('EXAM', 'DIFY_ANSWER_GRADING', '解答题判分（Dify）', '走 Dify 工作流判分',
   'DIFY', '{"inputVariable":"question","outputVariable":"comment"}', '0', 'admin', NOW(), NOW());
```

业务侧把能力码指过去即可（例如 `KbExamGradingServiceImpl` 用 `EXAM/ANSWER_GRADING`，切 Dify 只需把这一行的 `provider` 改成 `DIFY` 并补 `provider_config`）。

## 4. 自检（链路验证）

```bash
# 需要登录态（satoken）；示例用 admin 登录拿 token
TOKEN=$(curl -s -X POST "$BASE/auth/login" -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}' | python3 -c "import sys,json;print(json.load(sys.stdin)['data']['tokenValue'])")

curl -s -X POST "$BASE/ai/capabilities/invoke" -H "satoken: $TOKEN" -H 'Content-Type: application/json' \
  -d '{"sceneCode":"SYSTEM","capabilityCode":"DIFY_DEMO_WORKFLOW","input":"家庭装修预算怎么控制"}'
```

内置了 `SYSTEM / DIFY_DEMO_WORKFLOW`（指向 Dify 自带「演示·工作流」）作为链路自检能力。

## 5. 哪些能力适合搬去 Dify

| 能力 | 建议 | 理由 |
|---|---|---|
| `EXAM / ANSWER_GRADING` 解答题判分 | ✅ 优先 | 适合挂参考答案/评分标准知识库，能可视化调 prompt 与模型 |
| `HOME_PURCHASE / PRODUCT_RECOMMEND` 产品推荐 | ✅ 可以 | 挂"在售型号/价格库"比纯大模型编型号靠谱 |
| `HOME_PURCHASE / ITEM_PARSE` 一句话录入 | ❌ 不必 | 纯结构化抽取，DeepSeek 直连 1 秒内返回，过 Dify 只多一跳开销 |
| 多步 RAG / 多工具编排 | ✅ | Dify 强项 |

## 6. 踩坑记录

1. **Dify 返回值里的嵌套数组**：解析模型输出时按「第一个 `[` 到最后一个 `]`」截取，用第一个 `]` 会被嵌套数组截断（`PRODUCT_RECOMMEND` 踩过）。
2. **不要用 `X-Real-IP` 判断调用来源**：frp 链路下它是内网地址，真实 IP 在 `X-Forwarded-For` 最左侧。
3. **API Key 不要进仓库**：`sql/z_init_data.sql` 里刻意不在 `ON DUPLICATE KEY UPDATE` 覆盖 `provider_config`，避免反复初始化把线上配的 Key 冲掉。
4. **超时**：工作流多步调用模型容易超过 60s，`ai.dify.read-timeout` 默认给到 180s。
5. **不需要用 Dify 的 UI 做业务**：不要在 Dify 里配业务菜单、不要在 Dify 里存业务数据；它只负责"输入 → 输出"。

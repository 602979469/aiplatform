<template>
  <div class="chat-container">
    <!-- 左侧会话列表 -->
    <div class="chat-sidebar">
      <div class="sidebar-header">
        <div class="sidebar-brand">
          <svg-icon icon-class="message" />
          <span>AI 对话</span>
        </div>
        <el-button
          v-hasPermi="['ai:chat:add']"
          type="primary"
          size="small"
          icon="el-icon-plus"
          @click="handleNewSession"
        >新建会话</el-button>
      </div>

      <div v-loading="sessionLoading" class="session-list">
        <div
          v-for="session in sessions"
          :key="session.sessionId"
          class="session-item"
          :class="{ active: session.sessionId === currentSessionId }"
          @click="handleSelectSession(session)"
        >
          <div class="session-title-row">
            <span class="session-name" :title="session.sessionName">{{ session.sessionName }}</span>
            <span class="session-actions">
              <i
                v-hasPermi="['ai:chat:add']"
                class="el-icon-edit session-action"
                title="设置标题"
                @click.stop="handleRenameSession(session)"
              />
              <i
                v-hasPermi="['ai:chat:remove']"
                class="el-icon-delete session-action danger"
                title="删除会话"
                @click.stop="handleDeleteSession(session)"
              />
            </span>
          </div>
          <div class="session-sub-row">
            <span class="session-question" :title="session.lastQuestion || session.sessionName">
              {{ session.lastQuestion || session.sessionName }}
            </span>
            <span class="session-time">{{ parseTime(session.lastMessageTime || session.createTime, '{m}-{d} {h}:{i}') }}</span>
          </div>
        </div>
        <div v-if="!sessionLoading && sessions.length === 0" class="session-empty">
          <p>暂无会话</p>
          <p class="session-empty-tip">点击「新建会话」开始对话</p>
        </div>
      </div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-main">
      <div class="chat-header">
        <span class="chat-header-title">{{ currentSessionName }}</span>
      </div>

      <div ref="chatBody" class="chat-body">
        <div v-if="messages.length === 0 && !sending" class="chat-welcome">
          <div class="welcome-avatar">AI</div>
          <h2>你好，我是 AI 助手</h2>
          <p>你可以问我任何问题，我会快速为你解答</p>
        </div>

        <div v-for="message in messages" :key="message.messageId" class="message-row" :class="message.role">
          <div class="message-avatar" :class="message.role">
            <img v-if="message.role === 'user' && userAvatar" :src="userAvatar" class="avatar-img" alt="avatar">
            <span v-else>{{ message.role === 'user' ? '我' : 'AI' }}</span>
          </div>
          <div class="message-content">
            <div class="message-bubble" :class="message.role">
              <i class="el-icon-doc-copy bubble-copy" title="复制" @click="copyContent(message)" />
              <span>{{ message.content }}</span>
            </div>
            <i
              v-if="message.role === 'user' && message.status === '1'"
              class="el-icon-warning message-failed"
              :title="messageError(message)"
              @click="handleRetry(message)"
            />
          </div>
        </div>

        <div v-if="sending" class="message-row assistant">
          <div class="message-avatar assistant">AI</div>
          <div class="message-bubble assistant thinking">
            <span class="thinking-text">模型思考中</span>
            <span class="dot" />
            <span class="dot" />
            <span class="dot" />
          </div>
        </div>
      </div>

      <div class="chat-input-area">
        <div class="chat-input-box">
          <el-input
            v-model="input"
            type="textarea"
            :rows="3"
            resize="none"
            maxlength="4000"
            show-word-limit
            :disabled="sending"
            placeholder="输入你的问题，Enter 发送，Shift + Enter 换行"
            @keydown.enter.native="handleKeydown"
          />
          <div class="chat-input-footer">
            <span class="chat-input-tip">仅支持文本对话</span>
            <el-button
              type="primary"
              :loading="sending"
              :disabled="!input.trim()"
              @click="handleSend"
            >发送</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { listSession, addSession, updateSession, delSession, listMessage, sendChat } from '@/api/ai/chat'
import { parseTime } from '@/utils/ruoyi'

export default {
  name: 'AiChat',
  data() {
    return {
      sessions: [],
      sessionLoading: false,
      currentSessionId: null,
      currentSessionName: '开始新的对话',
      messages: [],
      input: '',
      sending: false,
      failedErrors: {}
    }
  },
  computed: {
    /** 用户头像：未设置头像时为空，聊天框渲染默认占位 */
    userAvatar() {
      const raw = this.$store.state.user && this.$store.state.user.avatar
      return raw || ''
    }
  },
  created() {
    this.getSessions()
  },
  methods: {
    parseTime,

    /** 失败消息的提示文案 */
    messageError(message) {
      return this.failedErrors[message.messageId] || '发送失败，点击重试'
    },

    /** 查询会话列表 */
    getSessions() {
      this.sessionLoading = true
      listSession().then(response => {
        this.sessions = response.data || []
        if (this.currentSessionId) {
          const current = this.sessions.find(item => item.sessionId === this.currentSessionId)
          if (current) {
            this.currentSessionName = current.sessionName
          }
        }
      }).finally(() => {
        this.sessionLoading = false
      })
    },

    /** 新建会话 */
    handleNewSession() {
      if (!this.$auth.hasPermi('ai:chat:add')) {
        return
      }
      addSession({}).then(response => {
        const session = response.data
        this.sessions.unshift(session)
        this.currentSessionId = session.sessionId
        this.currentSessionName = session.sessionName
        this.messages = []
        this.input = ''
        this.$nextTick(() => this.scrollToBottom())
      })
    },

    /** 选择会话 */
    handleSelectSession(session) {
      if (this.sending || this.currentSessionId === session.sessionId) {
        return
      }
      this.currentSessionId = session.sessionId
      this.currentSessionName = session.sessionName
      this.input = ''
      this.getMessages(session.sessionId)
    },

    /** 设置会话标题 */
    handleRenameSession(session) {
      this.$prompt('请输入会话标题，保存后会显示在标题位置', '设置标题', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: session.sessionName,
        inputPattern: /\S+/,
        inputErrorMessage: '标题不能为空'
      }).then(({ value }) => {
        return updateSession({
          sessionId: session.sessionId,
          sessionName: value.trim()
        })
      }).then(() => {
        this.$message.success('标题已更新')
        this.getSessions()
      }).catch(() => {})
    },

    /** 删除会话 */
    handleDeleteSession(session) {
      this.$confirm(`确定删除会话「${session.sessionName}」吗？会话下的所有消息将一并删除。`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delSession(session.sessionId)
      }).then(() => {
        this.$message.success('删除成功')
        const index = this.sessions.findIndex(item => item.sessionId === session.sessionId)
        if (index > -1) {
          this.sessions.splice(index, 1)
        }
        if (this.currentSessionId === session.sessionId) {
          this.currentSessionId = null
          this.currentSessionName = '开始新的对话'
          this.messages = []
        }
      }).catch(() => {})
    },

    /** 查询会话消息 */
    getMessages(sessionId) {
      listMessage(sessionId).then(response => {
        this.messages = response.data || []
        this.$nextTick(() => this.scrollToBottom())
      })
    },

    /** 发送消息：提问立即上屏，思考中展示伪气泡，失败保留提问并标记 */
    handleSend() {
      const content = this.input.trim()
      if (!content || this.sending) {
        return
      }
      const tempId = 'temp_' + Date.now() + '_' + Math.floor(Math.random() * 1000)
      this.messages.push({
        messageId: tempId,
        role: 'user',
        content: content,
        status: '0',
        createTime: new Date()
      })
      this.input = ''
      this.sending = true
      this.$nextTick(() => this.scrollToBottom())

      sendChat({
        sessionId: this.currentSessionId,
        content: content
      }).then(response => {
        const data = response.data || {}
        // 首次对话后端自动建会话
        if (!this.currentSessionId && data.sessionId) {
          this.currentSessionId = data.sessionId
          this.currentSessionName = data.sessionName || content.slice(0, 30)
          this.sessions.unshift({
            sessionId: data.sessionId,
            sessionName: this.currentSessionName,
            createTime: new Date()
          })
        }
        if (data.failed) {
          const failedMsg = this.messages.find(message => message.messageId === tempId)
          if (failedMsg) {
            failedMsg.messageId = data.userMessageId || failedMsg.messageId
            failedMsg.status = '1'
            this.$set(this.failedErrors, failedMsg.messageId, data.error || '模型调用失败')
          }
          this.$message.error(data.error || '模型调用失败，请点击红色感叹号重试')
        } else {
          this.getMessages(this.currentSessionId)
        }
        this.getSessions()
      }).catch(() => {
        const failedMsg = this.messages.find(message => message.messageId === tempId)
        if (failedMsg) {
          failedMsg.status = '1'
          this.$set(this.failedErrors, failedMsg.messageId, '网络异常，消息可能未送达')
        }
        this.getSessions()
      }).finally(() => {
        this.sending = false
      })
    },

    /** 重试失败消息 */
    handleRetry(message) {
      if (message.role !== 'user') {
        return
      }
      const tip = this.failedErrors[message.messageId] || '消息发送失败'
      this.$confirm(`${tip}，是否重试？`, '发送失败', {
        confirmButtonText: '重试',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.sending = true
        const realId = typeof message.messageId === 'number' ? message.messageId : null
        sendChat({
          sessionId: this.currentSessionId,
          content: message.content,
          messageId: realId
        }).then(response => {
          const data = response.data || {}
          if (data.failed) {
            message.messageId = data.userMessageId || message.messageId
            message.status = '1'
            this.$set(this.failedErrors, message.messageId, data.error || '重试失败')
            this.$message.error(data.error || '重试失败，请稍后再试')
          } else {
            this.getMessages(this.currentSessionId)
            this.getSessions()
          }
        }).catch(() => {
          message.status = '1'
          this.$set(this.failedErrors, message.messageId, '网络异常，重试失败')
          this.$message.error('网络异常，重试失败')
        }).finally(() => {
          this.sending = false
        })
      }).catch(() => {})
    },

    /** 复制消息内容 */
    copyContent(message) {
      const text = message.content || ''
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(text)
          .then(() => this.$message.success('复制成功'))
          .catch(() => this.fallbackCopy(text))
      } else {
        this.fallbackCopy(text)
      }
    },
    fallbackCopy(text) {
      const textarea = document.createElement('textarea')
      textarea.value = text
      textarea.style.position = 'fixed'
      textarea.style.opacity = '0'
      document.body.appendChild(textarea)
      textarea.select()
      try {
        document.execCommand('copy')
        this.$message.success('复制成功')
      } catch (e) {
        this.$message.error('复制失败')
      }
      document.body.removeChild(textarea)
    },

    /** Enter 发送，Shift + Enter 换行 */
    handleKeydown(event) {
      if (event.shiftKey || event.isComposing) {
        return
      }
      event.preventDefault()
      this.handleSend()
    },

    /** 滚动到底部 */
    scrollToBottom() {
      const body = this.$refs.chatBody
      if (body) {
        body.scrollTop = body.scrollHeight
      }
    }
  }
}
</script>

<style scoped lang="scss">
.chat-container {
  display: flex;
  height: calc(100vh - 124px);
  min-height: 480px;
  background: #f7f8fa;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

/* 左侧会话列表 */
.chat-sidebar {
  display: flex;
  flex-direction: column;
  width: 280px;
  flex-shrink: 0;
  background: #ffffff;
  border-right: 1px solid #ebeef5;
}

.sidebar-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 16px 14px;
  border-bottom: 1px solid #f0f2f5;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.sidebar-brand .svg-icon {
  font-size: 20px;
  color: #409eff;
}

.sidebar-header .el-button {
  width: 100%;
}

.session-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.session-item {
  padding: 10px 12px;
  margin-bottom: 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.session-item:hover {
  background: #f5f7fa;
}

.session-item.active {
  background: #ecf5ff;
}

.session-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.session-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-item.active .session-name {
  color: #409eff;
}

.session-actions {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  margin-left: 6px;
  visibility: hidden;
}

.session-item:hover .session-actions {
  visibility: visible;
}

.session-action {
  font-size: 13px;
  color: #c0c4cc;
  cursor: pointer;
  padding: 2px;
}

.session-action + .session-action {
  margin-left: 4px;
}

.session-action:hover {
  color: #409eff;
}

.session-action.danger:hover {
  color: #f56c6c;
}

.session-sub-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 4px;
  min-width: 0;
}

.session-question {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-time {
  font-size: 12px;
  color: #c0c4cc;
  flex-shrink: 0;
  margin-left: 6px;
}

.session-empty {
  padding: 40px 0;
  text-align: center;
  color: #909399;
  font-size: 13px;
}

.session-empty-tip {
  margin-top: 8px;
  color: #c0c4cc;
  font-size: 12px;
}

/* 右侧聊天区域 */
.chat-main {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 48px;
  flex-shrink: 0;
  background: #ffffff;
  border-bottom: 1px solid #f0f2f5;
}

.chat-header-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px 32px;
}

.chat-welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.welcome-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff, #53a8ff);
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
}

.chat-welcome h2 {
  margin: 16px 0 8px;
  font-size: 20px;
  color: #303133;
}

.chat-welcome p {
  margin: 0;
  font-size: 14px;
}

.message-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.message-row.user {
  justify-content: flex-end;
}

.message-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  margin-right: 12px;
  border-radius: 50%;
  flex-shrink: 0;
  font-size: 13px;
  font-weight: 600;
  overflow: hidden;
}

.message-avatar.assistant {
  background: linear-gradient(135deg, #409eff, #53a8ff);
  color: #ffffff;
}

.message-avatar.user {
  order: 2;
  margin-right: 0;
  margin-left: 12px;
  background: #909399;
  color: #ffffff;
}

.avatar-img {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.message-content {
  display: flex;
  align-items: flex-start;
  max-width: 76%;
}

.message-row.user .message-content {
  justify-content: flex-end;
}

.message-bubble {
  position: relative;
  max-width: 100%;
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.7;
  word-break: break-word;
  white-space: pre-wrap;
}

.message-bubble.assistant {
  background: #ffffff;
  border: 1px solid #ebeef5;
  color: #303133;
}

.message-bubble.user {
  background: #409eff;
  color: #ffffff;
}

.bubble-copy {
  position: absolute;
  top: 6px;
  right: 8px;
  font-size: 13px;
  color: inherit;
  opacity: 0;
  cursor: pointer;
  transition: opacity 0.2s;
}

.message-bubble:hover .bubble-copy {
  opacity: 0.6;
}

.message-bubble:hover .bubble-copy:hover {
  opacity: 1;
}

.message-failed {
  margin: 10px 0 0 6px;
  font-size: 17px;
  color: #f56c6c;
  cursor: pointer;
  flex-shrink: 0;
}

.message-failed:hover {
  transform: scale(1.15);
}

.message-bubble.thinking {
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 96px;
}

.thinking-text {
  font-size: 13px;
  color: #909399;
  margin-right: 4px;
}

.message-bubble.thinking .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #909399;
  animation: thinking 1.2s infinite ease-in-out;
}

.message-bubble.thinking .dot:nth-child(3) {
  animation-delay: 0.2s;
}

.message-bubble.thinking .dot:nth-child(4) {
  animation-delay: 0.4s;
}

@keyframes thinking {
  0%, 80%, 100% {
    opacity: 0.3;
    transform: translateY(0);
  }
  40% {
    opacity: 1;
    transform: translateY(-4px);
  }
}

/* 输入区域 */
.chat-input-area {
  flex-shrink: 0;
  padding: 12px 32px 20px;
  background: #ffffff;
  border-top: 1px solid #f0f2f5;
}

.chat-input-box {
  max-width: 900px;
  margin: 0 auto;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 8px;
  transition: border-color 0.2s;
}

.chat-input-box:focus-within {
  border-color: #409eff;
}

.chat-input-box .el-textarea {
  padding: 0 4px;
}

.chat-input-box ::v-deep .el-textarea__inner {
  border: none;
  box-shadow: none;
  padding: 0;
  font-size: 14px;
}

.chat-input-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 4px 0;
}

.chat-input-tip {
  font-size: 12px;
  color: #c0c4cc;
}
</style>

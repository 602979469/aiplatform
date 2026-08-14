<template>
  <div class="app-container">
    <el-card shadow="never" class="welcome-card">
      <div class="welcome-title">{{ title }}</div>
      <div class="welcome-desc">欢迎使用 {{ title }}，请从左侧菜单进入功能页面。</div>
      <div class="welcome-actions">
        <el-button type="primary" @click="go('/ai/chat')">AI 对话</el-button>
        <el-button type="success" @click="go('/ai/mirror')">镜像加速器</el-button>
        <el-button type="warning" @click="go('/system/menu')">菜单管理</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
/**
 * 首页工作台：登录后落地页，提供功能快捷入口。
 */
export default {
  name: 'Index',
  data() {
    return {
      title: process.env.VUE_APP_TITLE || 'aiplatform'
    }
  },
  methods: {
    /**
     * 跳转到指定页面。
     *
     * @param path 路由路径
     */
    go(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style scoped>
.welcome-card {
  margin-top: 80px;
  text-align: center;
}
.welcome-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 12px;
}
.welcome-desc {
  color: #909399;
  margin-bottom: 24px;
}
.welcome-actions .el-button {
  margin: 0 8px;
}
</style>

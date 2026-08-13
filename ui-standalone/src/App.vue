<template>
  <div id="app">
    <div class="top-nav">
      <router-link to="/chat" class="nav-link">AI 对话</router-link>
      <router-link to="/mirror" class="nav-link">镜像加速器</router-link>
    </div>
    <router-view />
  </div>
</template>

<style>
body {
  margin: 0;
  background: #f5f7fa;
}
.top-nav {
  display: flex;
  gap: 24px;
  align-items: center;
  padding: 12px 24px;
  background: #409eff;
}
.nav-link {
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  font-size: 15px;
}
.nav-link:hover,
.nav-link.router-link-active {
  color: #fff;
  font-weight: 600;
}
</style>

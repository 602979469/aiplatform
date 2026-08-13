/**
 * 权限插件（独立版）：无登录/权限体系，hasPermi / hasRole 恒返回 true。
 * 后续接入 RBAC 后替换为从后端用户信息判断。
 */
export default {
  install(Vue) {
    Vue.prototype.$auth = {
      hasPermi: () => true,
      hasRole: () => true
    }
  }
}

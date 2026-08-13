/**
 * v-hasPermi 权限指令（独立版）：无登录/权限体系，恒放行。
 */
export default {
  inserted(el, binding) {
    const { value } = binding
    if (!(value instanceof Array) || value.length === 0) {
      console.warn('v-hasPermi 未设置权限值')
    }
  }
}

import store from '@/store'

/**
 * 字符权限校验
 * @param {Array} value 校验值
 * @returns {Boolean}
 */
export function checkPermi(value) {
  if (value && value instanceof Array && value.length > 0) {
    const permissions = store.getters && store.getters.permissions
    const permissionDatas = value
    // Aiplatform 后端对管理员返回 "*"，RuoYi 习惯为 "*:*:*"，两者都视为全部权限
    const isAllPermission = (permission) => permission === "*" || permission === "*:*:*"

    const hasPermission = permissions.some(permission => {
      return isAllPermission(permission) || permissionDatas.includes(permission)
    })

    return hasPermission

  } else {
    console.error(`need roles! Like checkPermi="['system:user:add','system:user:edit']"`)
    return false
  }
}

/**
 * 角色权限校验
 * @param {Array} value 校验值
 * @returns {Boolean}
 */
export function checkRole(value) {
  if (value && value instanceof Array && value.length > 0) {
    const roles = store.getters && store.getters.roles
    const permissionRoles = value
    const super_admin = "admin"

    const hasRole = roles.some(role => {
      return super_admin === role || permissionRoles.includes(role)
    })

    return hasRole

  } else {
    console.error(`need roles! Like checkRole="['admin','editor']"`)
    return false
  }
}

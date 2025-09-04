/**
 * Created by PanJiaChen on 16/11/18.
 */

/**
 * @param {string} path
 * @returns {Boolean}
 */
export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * @param {string} str
 * @returns {Boolean}
 */
export function validUsername(str) {
  // 修改为更通用的用户名验证逻辑
  // 允许3-20位字符，包含字母、数字、下划线
  const username = str.trim()
  if (!username) {
    return false
  }

  // 用户名长度检查
  if (username.length < 3 || username.length > 20) {
    return false
  }

  // 用户名格式检查：只允许字母、数字、下划线，且必须以字母开头
  const usernameRegex = /^[a-zA-Z][a-zA-Z0-9_]*$/
  return usernameRegex.test(username)
}

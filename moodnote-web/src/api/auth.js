/**
 * 认证模块 API 封装
 * 统一管理登录、注册、验证码等接口调用
 */

import api from '../utils/request'

/**
 * 获取图形验证码
 * @returns {Promise} 返回包含 captchaKey 和 captchaImg 的对象
 */
export const getCaptcha = () => {
  return api.get('/auth/captcha')
}

/**
 * 用户登录
 * @param {Object} data - 登录数据
 * @param {string} data.username - 用户名或邮箱
 * @param {string} data.password - 密码
 * @param {string} data.captcha - 图形验证码
 * @param {string} data.captchaKey - 验证码key
 * @returns {Promise} 返回包含 token 和 user 的对象
 */
export const login = (data) => {
  return api.post('/auth/login', {
    username: data.username,
    password: data.password,
    captcha: data.captcha,
    captchaKey: data.captchaKey
  })
}

/**
 * 用户注册
 * @param {Object} data - 注册数据
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} data.email - 邮箱
 * @param {string} data.code - 邮箱验证码
 * @param {string} [data.nickname] - 昵称（可选）
 * @returns {Promise} 返回注册结果
 */
export const register = (data) => {
  return api.post('/auth/register', {
    username: data.username,
    password: data.password,
    email: data.email,
    code: data.code,
    nickname: data.nickname || data.username
  })
}

/**
 * 发送邮箱验证码
 * @param {Object} data - 发送验证码数据
 * @param {string} data.email - 邮箱地址
 * @param {string} data.type - 类型：register(注册) / reset(重置密码) / login(登录)
 * @param {string} data.captcha - 图形验证码
 * @param {string} data.captchaKey - 验证码key
 * @returns {Promise} 返回发送结果
 */
export const sendCode = (data) => {
  return api.post('/auth/send-code', {
    email: data.email,
    type: data.type,
    captcha: data.captcha,
    captchaKey: data.captchaKey
  })
}

/**
 * 用户登出
 * @returns {Promise} 返回登出结果
 */
export const logout = () => {
  return api.post('/auth/logout')
}

/**
 * 重置密码
 * @param {Object} data - 重置密码数据
 * @param {string} data.email - 邮箱地址
 * @param {string} data.code - 邮箱验证码
 * @param {string} data.captcha - 图形验证码
 * @param {string} data.captchaKey - 验证码key
 * @param {string} data.newPassword - 新密码
 * @returns {Promise} 返回重置结果
 */
export const resetPassword = (data) => {
  return api.post('/auth/reset-password', {
    email: data.email,
    code: data.code,
    captcha: data.captcha,
    captchaKey: data.captchaKey,
    newPassword: data.newPassword
  })
}

/**
 * 获取当前用户信息
 * @returns {Promise} 返回用户信息对象
 */
export const getUserInfo = () => {
  return api.get('/user/info')
}

/**
 * 更新用户信息
 * @param {Object} data - 用户信息
 * @param {string} [data.nickname] - 昵称
 * @param {string} [data.avatar] - 头像URL
 * @param {string} [data.phone] - 手机号
 * @param {number} [data.gender] - 性别：0未知/1男/2女
 * @returns {Promise} 返回更新结果
 */
export const updateUserInfo = (data) => {
  return api.put('/user/profile', data)
}
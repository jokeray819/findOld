import request from './request.js'

/**
 * 微信小程序登录：wx.login 取得 code 后调用
 * @param {string} code wx.login 返回的 code
 * @returns {Promise<{ token, user }>}
 */
export function wechatMiniLogin(code) {
  return request({
    url: '/api/auth/wechat/mini-login',
    method: 'POST',
    needAuth: false,
    data: { code },
  })
}

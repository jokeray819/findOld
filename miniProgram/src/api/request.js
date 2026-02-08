/**
 * 统一请求封装：baseURL、Authorization、401 处理
 * 微信小程序中优先使用 wx，避免 uni 在部分编译环境下未注入导致请求不发
 */

const BASE_URL = 'http://localhost:8080'

const TOKEN_KEY = 'findold_token'

const g = typeof wx !== 'undefined' ? wx : (typeof uni !== 'undefined' ? uni : null)

export function getToken() {
  return g ? g.getStorageSync(TOKEN_KEY) || '' : ''
}

export function setToken(token) {
  if (g) g.setStorageSync(TOKEN_KEY, token)
}

export function clearToken() {
  if (g) g.removeStorageSync(TOKEN_KEY)
}

function handle401() {
  clearToken()
  const toast = typeof uni !== 'undefined' ? uni : (typeof wx !== 'undefined' ? wx : null)
  if (toast) toast.showToast({ title: '请重新登录', icon: 'none' })
  setTimeout(() => {
    if (typeof uni !== 'undefined') uni.reLaunch({ url: '/pages/index/index' })
  }, 1500)
}

/**
 * @param {string} url 相对路径，如 /api/posts
 * @param {object} options method, data, header 等，同 uni.request
 * @returns {Promise<any>}
 */
export function request(options) {
  const { url, needAuth = true, ...rest } = options
  const fullUrl = url.startsWith('http') ? url : BASE_URL + url
  const header = {
    'Content-Type': 'application/json',
    ...rest.header,
  }
  if (needAuth) {
    const token = getToken()
    if (token) header.Authorization = `Bearer ${token}`
  }

  const requestApi = typeof wx !== 'undefined' ? wx : uni
  if (!requestApi || !requestApi.request) {
    return Promise.reject(new Error('request API 不可用'))
  }

  return new Promise((resolve, reject) => {
    requestApi.request({
      url: fullUrl,
      method: rest.method || 'GET',
      data: rest.data,
      header,
      success: (res) => {
        if (res.statusCode === 401) {
          handle401()
          reject(new Error('未登录或登录已过期'))
          return
        }
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(res.data)
        } else {
          const msg = res.data?.message || res.data?.msg || `请求失败 ${res.statusCode}`
          const toast = typeof uni !== 'undefined' ? uni : wx
          if (toast) toast.showToast({ title: msg, icon: 'none' })
          reject(new Error(msg))
        }
      },
      fail: (err) => {
        const toast = typeof uni !== 'undefined' ? uni : wx
        if (toast) toast.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      },
    })
  })
}

export default request

import { reactive } from 'vue'
import { getToken, setToken, clearToken } from '@/api/request.js'
import { wechatMiniLogin } from '@/api/auth.js'

const USER_KEY = 'findold_user'

const state = reactive({
  token: '',
  user: null,
})

function getStoredUser() {
  try {
    const raw = uni.getStorageSync(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

function setStoredUser(user) {
  if (user) {
    uni.setStorageSync(USER_KEY, JSON.stringify(user))
  } else {
    uni.removeStorageSync(USER_KEY)
  }
}

export function useUserStore() {
  return {
    get token() {
      return state.token
    },
    get user() {
      return state.user
    },
    get isLoggedIn() {
      return !!state.token
    },

    initFromStorage() {
      state.token = getToken()
      state.user = getStoredUser()
    },

    setLogin(token, user) {
      state.token = token
      state.user = user
      setToken(token)
      setStoredUser(user)
    },

    logout() {
      state.token = ''
      state.user = null
      clearToken()
      setStoredUser(null)
    },

    /**
     * 执行微信登录：wx.login 取 code，调后端，写入 store
     * @returns {Promise<boolean>} 是否登录成功
     */
    async doWechatLogin() {
      return new Promise((resolve) => {
        uni.login({
          provider: 'weixin',
          success: async (loginRes) => {
            const code = loginRes.code
            if (!code) {
              uni.showToast({ title: '获取登录码失败', icon: 'none' })
              resolve(false)
              return
            }
            try {
              const res = await wechatMiniLogin(code)
              this.setLogin(res.token, res.user)
              resolve(true)
            } catch (e) {
              resolve(false)
            }
          },
          fail: () => {
            uni.showToast({ title: '登录失败', icon: 'none' })
            resolve(false)
          },
        })
      })
    },
  }
}

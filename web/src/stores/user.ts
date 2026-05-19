import { defineStore } from 'pinia'
import { ref } from 'vue'
import request, { TOKEN_KEY } from '@/utils/request'

interface LoginResponse {
  code: number
  data: {
    token: string
  }
}

interface MeResponse {
  code: number
  data: {
    username: string
    role: string
  }
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const username = ref<string>('')

  const login = async (usernameInput: string) => {
    const { data } = await request.post<LoginResponse>('/user/auth/login', {
      username: usernameInput,
    })

    if (data.code === 200) {
      token.value = data.data.token
      localStorage.setItem(TOKEN_KEY, data.data.token)
      username.value = usernameInput
    } else {
      throw new Error('登录失败')
    }
  }

  const fetchMe = async () => {
    const { data } = await request.get<MeResponse>('/user/auth/me')

    if (data.code === 200) {
      username.value = data.data.username
    } else {
      throw new Error('获取用户信息失败')
    }
  }

  const logout = () => {
    token.value = ''
    username.value = ''
    localStorage.removeItem(TOKEN_KEY)
  }

  return {
    token,
    username,
    login,
    fetchMe,
    logout,
  }
})

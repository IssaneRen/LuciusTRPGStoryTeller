import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref('')

  async function login(user: string, pass: string) {
    const res = await api.post('/api/auth/login', { username: user, password: pass })
    token.value = res.data.data.token
    localStorage.setItem('token', token.value)
  }

  async function fetchMe() {
    const res = await api.get('/api/auth/me')
    username.value = res.data.data.username
  }

  function logout() {
    token.value = ''
    username.value = ''
    localStorage.removeItem('token')
  }

  return { token, username, login, fetchMe, logout }
})

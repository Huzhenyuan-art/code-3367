import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const username = ref(localStorage.getItem('username') || '')
  const role = ref(localStorage.getItem('role') || '')
  const balance = ref(parseFloat(localStorage.getItem('balance')) || 0)

  function setUser(data) {
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    role.value = data.role
    balance.value = data.balance

    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('username', data.username)
    localStorage.setItem('role', data.role)
    localStorage.setItem('balance', data.balance)
  }

  function updateBalance(newBalance) {
    balance.value = newBalance
    localStorage.setItem('balance', newBalance)
  }

  function logout() {
    token.value = ''
    userId.value = ''
    username.value = ''
    role.value = ''
    balance.value = 0

    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    localStorage.removeItem('balance')
  }

  const isAdmin = () => role.value === 'ADMIN'
  const isLoggedIn = () => !!token.value

  return {
    token,
    userId,
    username,
    role,
    balance,
    setUser,
    updateBalance,
    logout,
    isAdmin,
    isLoggedIn
  }
})

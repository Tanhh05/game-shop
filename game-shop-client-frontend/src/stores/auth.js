import { defineStore } from "pinia"
import { computed, ref } from "vue"
import { getBalance } from "@/services/wallet.service"
import { logout as logoutApi } from "@/services/auth.service"
import { clearAuth, getToken, setAuth } from "@/utils/auth"

const readLocalAuth = () => ({
  token: localStorage.getItem("token"),
  userId: localStorage.getItem("userId"),
  username: localStorage.getItem("username") || "",
  role: localStorage.getItem("role") || "",
  balance: Number(localStorage.getItem("balance") || 0)
})

export const useAuthStore = defineStore("auth", () => {
  const token = ref(null)
  const userId = ref(null)
  const username = ref("")
  const role = ref("")
  const balance = ref(0)

  const isAuthenticated = computed(() => !!token.value)

  const initFromStorage = () => {
    const raw = readLocalAuth()
    token.value = raw.token
    userId.value = raw.userId
    username.value = raw.username
    role.value = raw.role
    balance.value = Number.isNaN(raw.balance) ? 0 : raw.balance
  }

  const setSession = (payload = {}) => {
    setAuth(payload)
    initFromStorage()
  }

  const refreshBalance = async () => {
    if (!getToken()) return
    const res = await getBalance()
    balance.value = Number(res.data || 0)
    localStorage.setItem("balance", String(balance.value))
  }

  const logout = async () => {
    try {
      await logoutApi()
    } catch (_) {
      // ignore API errors when logging out
    }

    clearAuth()
    token.value = null
    userId.value = null
    username.value = ""
    role.value = ""
    balance.value = 0
  }

  initFromStorage()

  return {
    token,
    userId,
    username,
    role,
    balance,
    isAuthenticated,
    initFromStorage,
    setSession,
    refreshBalance,
    logout
  }
})

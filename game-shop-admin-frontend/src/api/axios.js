import axios from "axios"
import router from "@/router"
import { clearAuth, getToken, setAuth } from "@/utils/auth"

const instance = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL || "http://localhost:8080"}/api`,
  headers: {
    "Content-Type": "application/json"
  }
})

instance.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let isRefreshing = false
let waiting = []

const flushWaiting = (token) => {
  waiting.forEach((cb) => cb(token))
  waiting = []
}

instance.interceptors.response.use(
  (res) => res,
  async (error) => {
    const originalRequest = error.config
    const status = error.response?.status

    if (!originalRequest || status !== 401 || originalRequest._retry) {
      if (status === 401) {
        clearAuth()
        await router.push("/login")
      }
      return Promise.reject(error)
    }

    if (String(originalRequest.url || "").includes("/auth/refresh-token")) {
      clearAuth()
      await router.push("/login")
      return Promise.reject(error)
    }

    originalRequest._retry = true

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        waiting.push((newToken) => {
          if (!newToken) return reject(error)
          originalRequest.headers.Authorization = `Bearer ${newToken}`
          resolve(instance(originalRequest))
        })
      })
    }

    try {
      isRefreshing = true
      const token = getToken()
      if (!token) throw new Error("No token")

      const refreshRes = await instance.post(
        "/auth/refresh-token",
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      )

      const newToken = refreshRes.data?.token
      if (!newToken) throw new Error("Refresh token failed")

      setAuth({ token: newToken })
      flushWaiting(newToken)

      originalRequest.headers.Authorization = `Bearer ${newToken}`
      return instance(originalRequest)
    } catch (refreshErr) {
      flushWaiting(null)
      clearAuth()
      await router.push("/login")
      return Promise.reject(refreshErr)
    } finally {
      isRefreshing = false
    }
  }
)

export default instance

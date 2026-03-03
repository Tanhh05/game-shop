import axios from "axios"
import router from "@/routers"
import { clearAuth, getToken, setAuth } from "@/utils/auth"

const api = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api`,
  headers: {
    "Content-Type": "application/json"
  }
})

api.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let isRefreshing = false
let pendingQueue = []

const processQueue = (token) => {
  pendingQueue.forEach((cb) => cb(token))
  pendingQueue = []
}

api.interceptors.response.use(
  (response) => response,
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
        pendingQueue.push((newToken) => {
          if (!newToken) {
            reject(error)
            return
          }
          originalRequest.headers.Authorization = `Bearer ${newToken}`
          resolve(api(originalRequest))
        })
      })
    }

    try {
      isRefreshing = true
      const currentToken = getToken()
      if (!currentToken) throw new Error("No token")

      const refreshRes = await api.post(
        "/auth/refresh-token",
        {},
        {
          headers: { Authorization: `Bearer ${currentToken}` }
        }
      )

      const newToken = refreshRes.data?.token
      if (!newToken) throw new Error("Refresh failed")

      setAuth({ token: newToken })
      processQueue(newToken)

      originalRequest.headers.Authorization = `Bearer ${newToken}`
      return api(originalRequest)
    } catch (refreshErr) {
      processQueue(null)
      clearAuth()
      await router.push("/login")
      return Promise.reject(refreshErr)
    } finally {
      isRefreshing = false
    }
  }
)

export default api

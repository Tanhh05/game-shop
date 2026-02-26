import axios from "axios"
import router from "@/routers"

const api = axios.create({
    baseURL: `${import.meta.env.VITE_API_URL}/api`,
    headers: {
        "Content-Type": "application/json"
    }
})

// 🔐 Request Interceptor
api.interceptors.request.use(config => {
    const token = localStorage.getItem("token")
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

// 🚨 Response Interceptor
api.interceptors.response.use(
    response => response,
    async error => {
        if (error.response?.status === 401) {
            localStorage.removeItem("token")
            await router.push("/login")
        }
        return Promise.reject(error)
    }
)

export default api
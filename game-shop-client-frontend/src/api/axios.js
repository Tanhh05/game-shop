import axios from "axios"
import router from "@/routers"

const api = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: {
        "Content-Type": "application/json"
    }
})

// 🔐 Request Interceptor (gắn token)
api.interceptors.request.use(config => {
    const token = localStorage.getItem("token")
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

// 🚨 Response Interceptor (handle 401)
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
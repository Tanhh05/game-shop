import api from "@/api/axios"

export const register = (data) => api.post("/auth/register", data)

export const login = (data) => api.post("/auth/login", data)

export const logout = () => api.post("/auth/logout")

export const getProfile = (userId) => api.get(`/user/profile/${userId}`)

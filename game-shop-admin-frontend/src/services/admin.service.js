import api from "@/api/axios"

export const getUsers = (page = 0, size = 20) =>
  api.get("/admin/users", { params: { page, size } })

export const getUserDetail = (id) => api.get(`/admin/users/${id}`)

export const getUserOrders = (id) => api.get(`/admin/users/${id}/orders`)

export const getUserWallet = (id) => api.get(`/admin/users/${id}/wallet`)

export const changeUserStatus = (id, status) =>
  api.patch(`/admin/users/${id}/status`, null, { params: { status } })

export const getOrders = (page = 0, size = 20) =>
  api.get("/admin/orders", { params: { page, size } })

export const getOrderDetail = (id) => api.get(`/admin/orders/${id}`)

export const getInventoryStats = () => api.get("/admin/inventory/stats")

export const importKeys = (payload) => api.post("/admin/inventory/import-keys", payload)

export const importAccounts = (payload) => api.post("/admin/inventory/import-accounts", payload)

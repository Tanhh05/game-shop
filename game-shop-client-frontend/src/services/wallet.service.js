import api from "@/api/axios"

export const getBalance = (userId) => {
    return api.get(`/wallet/balance?userId=${userId}`)
}

export const getWalletLogs = (userId) => {
    return api.get(`/wallet/logs?userId=${userId}`)
}

export const topup = (data) => {
    return api.post(`/wallet/topup`, data)
}
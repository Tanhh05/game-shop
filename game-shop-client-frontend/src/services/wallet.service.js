import api from "@/api/axios"

export const getBalance = () => api.get("/wallet/balance")

export const getWalletLogs = () => api.get("/wallet/logs")

export const getDepositInfo = () => api.get("/webhook/wallet/deposit-info")

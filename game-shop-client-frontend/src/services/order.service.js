import api from "@/api/axios"

export const getCart = (userId) => {
    return api.get(`/orders/history?userId=${userId}`)
}

export const buyNow = (userId, data) => {
    return api.post(`/orders/buy-now?userId=${userId}`, data)
}
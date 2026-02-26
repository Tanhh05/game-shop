import api from "@/api/axios"

export const getPurchaseHistory = (userId, page = 0, size = 10) => {
    return api.get("/orders/history", {
        params: {
            page,
            size
        }
    })
}

export const buyNow = (userId, data) => {
    return api.post(`/orders/buy-now?userId=${userId}`, data)
}
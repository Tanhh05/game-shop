import api from "@/api/axios"

export const getCart = (userId) => {
    return api.get(`/orders/cart?userId=${userId}`)
}

export const buyNow = (userId, items) => {
    return api.post(`/orders/buy-now?userId=${userId}`, {
        items
    })
}
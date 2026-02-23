import api from "@/api/axios"

export const getProducts = (page = 0, size = 10) => {
    return api.get(`/products?page=${page}&size=${size}`)
}

export const getProductBySlug = (slug) => {
    return api.get(`/products/slug/${slug}`)
}

export const getProductsByGame = (gameId) => {
    return api.get(`/products/game/${gameId}`)
}
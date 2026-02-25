import api from "@/api/axios"

export const getProducts = (params) => {
    return api.get("/products", { params })
}

export const getProductBySlug = (slug) => {
    return api.get(`/products/slug/${slug}`)
}

export const getProductsByGame = (gameId) => {
    return api.get(`/products/game/${gameId}`)
}
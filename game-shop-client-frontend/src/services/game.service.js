import api from "@/api/axios"

export const getGames = (page = 0, size = 10) => {
    return api.get(`/games?page=${page}&size=${size}`)
}

export const getGameBySlug = (slug) => {
    return api.get(`/games/slug/${slug}`)
}
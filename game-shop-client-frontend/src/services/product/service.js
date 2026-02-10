import axios from 'axios'

const API_URL = 'http://localhost:8080/api/products'

export const getAllProducts = async () => {
    const res = await axios.get(API_URL)
    return res.data
}

export const getProductsByGame = async (gameId) => {
    const res = await axios.get(`${API_URL}/game/${gameId}`)
    return res.data
}

export const getProductBySlug = async (slug) => {
    const res = await axios.get(`${API_URL}/slug/${slug}`)
    return res.data
}

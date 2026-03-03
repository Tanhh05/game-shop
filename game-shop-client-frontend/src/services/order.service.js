import api from "@/api/axios"

export const getPurchaseHistory = (page = 0, size = 10) => {
  return api.get("/orders/history", {
    params: { page, size }
  })
}

export const buyNow = (data) => {
  return api.post("/orders/buy-now", data)
}

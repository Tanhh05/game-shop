import api from "@/api/axios"

const getProducts = (params) =>
  api.get("/products", {
    params: {
      page: params.page,
      size: params.size,
      sortBy: params.sortBy,
      direction: params.direction
    }
  })

const createProduct = (formData) => api.post("/products", formData)

const changeProductStatus = (id, status) =>
  api.patch(`/products/${id}/status`, null, { params: { status } })

export default {
  getProducts,
  createProduct,
  changeProductStatus
}

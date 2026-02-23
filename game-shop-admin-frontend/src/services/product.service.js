import axios from "./axios";

export default {
    getProducts(params) {
        return axios.get("/products", {
            params: {
                page: params.page,
                size: params.size,
                sortBy: params.sortBy,
                direction: params.direction
            }
        });
    },

    // Gửi FormData (multipart/form-data) tới API tạo sản phẩm
    createProduct(formData) {
        // Không set Content-Type thủ công — axios sẽ tự set boundary
        return axios.post("/products", formData);
    }
};

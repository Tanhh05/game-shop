<script setup>
import { ref, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import { getProductBySlug } from "@/services/product.service"
import { buyNow } from "@/services/order.service"

const route = useRoute()
const router = useRouter()

const product = ref(null)
const loading = ref(false)
const orderResult = ref(null)
const error = ref("")

// Load sản phẩm
onMounted(async () => {
  try {
    const res = await getProductBySlug(route.params.slug)
    product.value = res.data
  } catch {
    error.value = "Không tải được sản phẩm."
  }
})

// Mua ngay
const handleBuyNow = async () => {
  if (loading.value) return

  const token = localStorage.getItem("token")
  const userId = localStorage.getItem("userId")

  if (!token) {
    router.push("/login")
    return
  }

  try {
    loading.value = true
    error.value = ""
    orderResult.value = null

    const res = await buyNow(userId, [
      {
        productId: product.value.id,
        quantity: 1
      }
    ])

    orderResult.value = res.data

  } catch (err) {
    error.value =
        err.response?.data?.message ||
        "Mua thất bại. Vui lòng thử lại."
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section v-if="product" class="detail-section">
    <div class="container">
      <div class="detail-wrapper">

        <!-- IMAGE -->
        <div class="image-box">
          <img :src="product.thumbnail" />
        </div>

        <!-- INFO -->
        <div class="info-box">

          <h1 class="title">{{ product.title }}</h1>

          <div class="price">
            {{ product.price.toLocaleString() }} đ
          </div>

          <p class="description">
            {{ product.description }}
          </p>

          <button
              class="buy-btn"
              @click="handleBuyNow"
              :disabled="loading"
          >
            {{ loading ? "Đang xử lý..." : "MUA NGAY" }}
          </button>

          <!-- Lỗi -->
          <p v-if="error" class="error">
            {{ error }}
          </p>

          <!-- Thành công -->
          <div v-if="orderResult" class="success-box">
            <h3>🎉 Đặt hàng thành công</h3>

            <p><strong>Mã đơn:</strong> #{{ orderResult.id }}</p>
            <p><strong>Tổng tiền:</strong>
              {{ orderResult.totalAmount.toLocaleString() }} đ
            </p>
            <p><strong>Trạng thái:</strong>
              {{ orderResult.status }}
            </p>

            <div
                v-for="(item, index) in orderResult.items"
                :key="index"
                class="order-item"
            >
              {{ item.productName }}
              - {{ item.quantity }} x
              {{ item.price.toLocaleString() }} đ
            </div>
          </div>

        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.detail-section {
  background: #ffffff;
  padding: 60px 0;
}

.container {
  max-width: 1200px;
  width: 92%;
  margin: auto;
}

.detail-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 50px;
}

.image-box img {
  width: 100%;
  border: 1px solid #e5e5e5;
}

.title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 20px;
}

.price {
  font-size: 26px;
  font-weight: 700;
  color: #ff9800;
  margin-bottom: 20px;
}

.description {
  font-size: 15px;
  color: #555;
  margin-bottom: 25px;
  line-height: 1.6;
}

.buy-btn {
  padding: 14px 30px;
  background: linear-gradient(45deg, #ff9800, #f57c00);
  color: white;
  border: none;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}

.buy-btn:hover {
  opacity: 0.9;
}

.buy-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.error {
  margin-top: 15px;
  color: red;
  font-weight: 600;
}

.success-box {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid #e5e5e5;
  background: #f9f9f9;
}

.order-item {
  margin-top: 8px;
}

@media (max-width: 768px) {
  .detail-wrapper {
    grid-template-columns: 1fr;
  }
}
</style>
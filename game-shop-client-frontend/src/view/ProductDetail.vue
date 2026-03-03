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

          <h1 class="title">
            {{ product.title }}
          </h1>

          <!-- Dropdown chọn gói -->
          <div class="package-box">

            <select v-model="selectedPackageId" class="package-select">
              <option disabled value="">--Chọn Gói--</option>
              <option
                  v-for="pkg in product.packages"
                  :key="pkg.id"
                  :value="pkg.id"
              >
                {{ pkg.name }}
              </option>
            </select>

            <button
                class="rent-btn"
                @click="handleBuyNow"
                :disabled="loading"
            >
              {{ loading ? "Đang xử lý..." : "THUÊ LUÔN" }}
            </button>

          </div>

          <!-- Hiển thị giá -->
          <div v-if="selectedPackage" class="price">
            {{ selectedPackage.price.toLocaleString() }} đ
          </div>

          <p class="description">
            {{ product.description }}
          </p>

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
          </div>

        </div>

      </div>
    </div>
  </section>
</template>
<script setup>
import { ref, onMounted, computed } from "vue"
import { useRoute, useRouter } from "vue-router"
import { getProductBySlug } from "@/services/product.service"
import { buyNow } from "@/services/order.service"
import { parseApiError } from "@/utils/api-error"
import { useAuthStore } from "@/stores/auth"
const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const product = ref(null)
const selectedPackageId = ref("")
const loading = ref(false)
const error = ref("")
const orderResult = ref(null)

onMounted(async () => {
  try {
    const res = await getProductBySlug(route.params.slug)
    product.value = res.data
  } catch {
    error.value = "Không tải được sản phẩm."
  }
})

const selectedPackage = computed(() => {
  if (!product.value || !selectedPackageId.value) return null
  return product.value.packages.find(
      p => p.id === Number(selectedPackageId.value)
  )
})

const handleBuyNow = async () => {

  if (!selectedPackageId.value) {
    error.value = "Vui lòng chọn gói trước khi mua."
    return
  }

  if (!auth.isAuthenticated) {
    await router.push("/login")
    return
  }

  try {
    loading.value = true
    error.value = ""
    orderResult.value = null

    // 1️⃣ Gọi API mua hàng
    const res = await buyNow({
      items: [
        {
          productId: product.value.id,
          packageId: Number(selectedPackageId.value),
          quantity: 1
        }
      ]
    })

    orderResult.value = res.data
    await auth.refreshBalance()

  } catch (err) {
    error.value = parseApiError(err, "Mua thất bại. Vui lòng thử lại.")
  } finally {
    loading.value = false
  }
}

</script>
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
  margin-bottom: 25px;
}

.package-box {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.package-select {
  flex: 1;
  padding: 10px;
  border: 1px solid #ccc;
}

.rent-btn {
  background: #28a745;
  color: white;
  padding: 10px 25px;
  border: none;
  font-weight: bold;
  cursor: pointer;
}

.rent-btn:hover {
  opacity: 0.9;
}

.price {
  font-size: 24px;
  font-weight: 700;
  color: #ff9800;
  margin-bottom: 15px;
}

.description {
  font-size: 15px;
  color: #555;
  line-height: 1.6;
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

@media (max-width: 768px) {
  .detail-wrapper {
    grid-template-columns: 1fr;
  }
}
</style>

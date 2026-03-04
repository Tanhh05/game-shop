<template>
  <section v-if="product" class="detail">
    <div class="container">
      <div class="detail__grid">
        <div class="media-card">
          <img :src="product.thumbnail" :alt="product.title" />
        </div>

        <div class="info-card">
          <p class="eyebrow">Chi tiết sản phẩm</p>
          <h1 class="title">{{ product.title }}</h1>

          <p class="description">{{ product.description }}</p>

          <div class="package">
            <label class="field">
              <span>Chọn gói</span>
              <select v-model="selectedPackageId">
                <option disabled value="">Chọn gói phù hợp</option>
                <option v-for="pkg in product.packages" :key="pkg.id" :value="pkg.id">
                  {{ pkg.name }}
                </option>
              </select>
            </label>

            <div class="price" v-if="selectedPackage">
              {{ selectedPackage.price.toLocaleString() }} đ
            </div>

            <button class="primary-btn" @click="handleBuyNow" :disabled="loading">
              {{ loading ? "Đang xử lý..." : "Mua ngay" }}
            </button>
          </div>

          <p v-if="error" class="error">{{ error }}</p>

          <div v-if="orderResult" class="success-box">
            <h3>Đặt hàng thành công</h3>
            <p>Mã đơn: <strong>#{{ orderResult.id }}</strong></p>
            <p>Tổng tiền: <strong>{{ orderResult.totalAmount.toLocaleString() }} đ</strong></p>
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
.detail {
  background: #f8fafc;
  padding: 64px 0 80px;
  font-family: "Manrope", "Segoe UI", sans-serif;
  color: #0f172a;
}

.container {
  max-width: 1200px;
  width: 92%;
  margin: 0 auto;
}

.detail__grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: 40px;
}

.media-card {
  background: #ffffff;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  padding: 20px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
}

.media-card img {
  width: 100%;
  border-radius: 16px;
  object-fit: cover;
}

.info-card {
  background: #ffffff;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  padding: 28px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  color: #94a3b8;
  margin: 0 0 12px;
}

.title {
  font-family: "Space Grotesk", "Manrope", sans-serif;
  font-size: clamp(24px, 3vw, 32px);
  margin: 0 0 16px;
}

.description {
  font-size: 15px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 24px;
}

.package {
  display: grid;
  gap: 16px;
}

.field {
  display: grid;
  gap: 8px;
  font-size: 13px;
  color: #64748b;
}

.field select {
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  font-size: 14px;
}

.price {
  font-size: 22px;
  font-weight: 700;
  color: #f97316;
}

.primary-btn {
  background: linear-gradient(135deg, #fb923c, #f97316);
  color: #ffffff;
  padding: 12px 20px;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
}

.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error {
  margin-top: 16px;
  color: #dc2626;
  font-weight: 600;
}

.success-box {
  margin-top: 20px;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid rgba(34, 197, 94, 0.3);
  background: rgba(34, 197, 94, 0.08);
  color: #166534;
}

@media (max-width: 900px) {
  .detail__grid {
    grid-template-columns: 1fr;
  }
}
</style>

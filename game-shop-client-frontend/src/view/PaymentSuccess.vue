<template>
  <section class="payment">
    <div class="payment__card">
      <p class="eyebrow">Thanh toán</p>
      <h2 v-if="loading">Đang xác nhận giao dịch...</h2>
      <h2 v-else-if="success">Nạp tiền thành công</h2>
      <h2 v-else>Giao dịch thất bại</h2>

      <p class="message" v-if="!loading">
        {{ success ? "Số dư của bạn đã được cập nhật." : errorMessage }}
      </p>

      <div class="actions" v-if="!loading">
        <router-link to="/" class="btn btn--primary">Về trang chủ</router-link>
        <router-link to="/orders" class="btn btn--ghost">Xem lịch sử</router-link>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue"
import { useRoute } from "vue-router"
import { capturePaypalOrder } from "@/services/payment.service"
import { parseApiError } from "@/utils/api-error"
import { useAuthStore } from "@/stores/auth"

const route = useRoute()
const auth = useAuthStore()
const loading = ref(true)
const success = ref(false)
const errorMessage = ref("")

onMounted(async () => {
  const orderId = route.query.token

  if (!orderId) {
    errorMessage.value = "Thiếu orderId từ PayPal"
    loading.value = false
    return
  }

  try {
    await capturePaypalOrder(orderId)
    await auth.refreshBalance()
    success.value = true
  } catch (err) {
    errorMessage.value = parseApiError(err, "Nạp tiền thất bại")
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>

.payment {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at top, #fff4e6 0%, #f8f5f0 50%, #edf2f7 100%);
  padding: 60px 0;
  font-family: "Manrope", "Segoe UI", sans-serif;
}

.payment__card {
  background: #ffffff;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  padding: 32px;
  text-align: center;
  width: min(520px, 92vw);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.12);
}

.eyebrow {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.28em;
  color: #94a3b8;
  margin: 0 0 12px;
}

h2 {
  font-family: "Space Grotesk", "Manrope", sans-serif;
  margin: 0 0 12px;
  font-size: clamp(22px, 3vw, 28px);
}

.message {
  margin: 0 0 24px;
  color: #64748b;
  font-size: 14px;
  line-height: 1.6;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.btn {
  text-decoration: none;
  padding: 10px 18px;
  border-radius: 999px;
  font-weight: 600;
  font-size: 14px;
}

.btn--primary {
  background: #f97316;
  color: #ffffff;
}

.btn--ghost {
  border: 1px solid #e2e8f0;
  color: #0f172a;
}
</style>

<template>
  <div>
    <h2 v-if="loading">Đang xác nhận...</h2>
    <h2 v-else-if="success">Nạp tiền thành công 🎉</h2>
    <h2 v-else>{{ errorMessage }}</h2>
  </div>
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

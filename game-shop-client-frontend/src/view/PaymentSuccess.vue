<template>
  <div>
    <h2 v-if="loading">Đang xác nhận...</h2>
    <h2 v-if="success">Nạp tiền thành công 🎉</h2>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue"
import { useRoute } from "vue-router"
import { capturePaypalOrder } from "@/services/payment.service"

const route = useRoute()
const loading = ref(true)
const success = ref(false)
const error = ref(false)
window.dispatchEvent(new Event("balance-updated"))

success.value = true
onMounted(async () => {
  const orderId = route.query.token

  try {
    await capturePaypalOrder(orderId)
    success.value = true
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
})
</script>
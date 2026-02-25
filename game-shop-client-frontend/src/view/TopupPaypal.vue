<template>
  <div class="topup-container">
    <h2>Nạp tiền bằng PayPal</h2>

    <input
        type="number"
        v-model.number="amountVnd"
        placeholder="Nhập số tiền muốn nạp (VND)"
        min="1000"
        class="input"
    />

    <button
        class="pay-btn"
        :disabled="!isValidAmount || loading"
        @click="handleTopup"
    >
      {{ loading ? "Đang chuyển sang PayPal..." : `Nạp ${formatVnd(amountVnd)} VND` }}
    </button>
  </div>
</template>

<script setup>
import { ref, computed } from "vue"
import { createPaypalOrder } from "@/services/payment.service"

const amountVnd = ref(null)
const loading = ref(false)

const isValidAmount = computed(() => {
  return amountVnd.value && amountVnd.value >= 1000
})

const formatVnd = (value) => {
  if (!value) return 0
  return new Intl.NumberFormat("vi-VN").format(value)
}

const handleTopup = async () => {
  if (!isValidAmount.value || loading.value) return

  try {
    loading.value = true

    const res = await createPaypalOrder(amountVnd.value)

    const approveUrl = res?.data?.approveUrl

    if (!approveUrl) {
      alert("Không lấy được link thanh toán từ PayPal")
      loading.value = false
      return
    }

    window.location.href = approveUrl

  } catch (err) {
    console.error(err)
    alert("Tạo thanh toán thất bại")
    loading.value = false
  }
}
</script>

<style scoped>
.topup-container {
  max-width: 420px;
  margin: 60px auto;
  padding: 25px;
  border-radius: 10px;
  background: #1e1e1e;
  color: white;
}

.input {
  width: 100%;
  padding: 12px;
  margin-bottom: 15px;
  border-radius: 6px;
  border: none;
}

.pay-btn {
  width: 100%;
  padding: 12px;
  background: #ffc439;
  border: none;
  font-weight: bold;
  cursor: pointer;
  margin-bottom: 20px;
  border-radius: 6px;
}

.pay-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.paypal-box {
  margin-top: 10px;
}
</style>
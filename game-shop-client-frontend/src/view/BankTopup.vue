<template>
  <div class="topup-container">
    <h2>Nạp tiền qua chuyển khoản</h2>

    <!-- QR HIỂN THỊ LUÔN -->
    <div v-if="qrData" class="qr-box">
      <img :src="qrData.qrUrl" alt="QR Code" class="qr-image" />

      <div class="bank-info">
        <p><strong>Ngân hàng:</strong> {{ qrData.bankName }}</p>
        <p><strong>Số tài khoản:</strong> {{ qrData.accountNumber }}</p>
        <p><strong>Chủ tài khoản:</strong> {{ qrData.accountName }}</p>
        <p class="deposit-code">
          <strong>Nội dung CK:</strong> {{ qrData.depositContent }}
        </p>
      </div>

      <p class="note">
        ⚠️ Vui lòng chuyển khoản đúng nội dung để hệ thống tự động cộng tiền.
      </p>
    </div>

    <p v-else>Đang tải QR...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import axios from "axios"

const qrData = ref(null)

const loadQr = async () => {
  try {
    const res = await axios.get("/wallet/deposit-info")
    qrData.value = res.data
  } catch (err) {
    console.error(err)
    alert("Không tải được QR")
  }
}

onMounted(() => {
  loadQr()
})
</script>
<style scoped>
.topup-container {
  max-width: 500px;
  margin: 60px auto;
  padding: 25px;
  border-radius: 10px;
  background: #1e1e1e;
  color: white;
  text-align: center;
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
  background: #00b14f;
  border: none;
  font-weight: bold;
  cursor: pointer;
  margin-bottom: 20px;
  border-radius: 6px;
  color: white;
}

.pay-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.qr-box {
  margin-top: 20px;
  background: #2b2b2b;
  padding: 20px;
  border-radius: 10px;
}

.qr-image {
  width: 250px;
  margin-bottom: 15px;
}

.bank-info {
  text-align: left;
  font-size: 14px;
  line-height: 1.6;
}

.deposit-code {
  color: #ffc107;
  font-weight: bold;
}

.note {
  margin-top: 15px;
  font-size: 13px;
  color: #ccc;
}
</style>
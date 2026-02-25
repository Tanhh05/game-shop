<script setup>
import { ref, onMounted } from "vue"
import { getCart } from "@/services/order.service"

const orders = ref([])
const userId = localStorage.getItem("userId")

onMounted(async () => {
  try {
    const res = await getCart(userId)
    orders.value = res.data
  } catch (err) {
    console.error("Không tải được lịch sử giao dịch")
  }
})
</script>

<template>
  <section class="history-section">
    <div class="container">
      <h2 class="title">LỊCH SỬ GIAO DỊCH</h2>

      <div v-if="orders.length === 0" class="empty">
        Bạn chưa có giao dịch nào.
      </div>

      <div
          v-for="order in orders"
          :key="order.id"
          class="order-card"
      >
        <!-- Header -->
        <div class="order-header">
          <div>
            <strong>Mã đơn:</strong> #{{ order.id }}
          </div>

          <div>
            <strong>Trạng thái:</strong>
            <span class="success">{{ order.status }}</span>
          </div>
        </div>

        <!-- Info -->
        <div class="order-info">
          <div>
            <strong>Tổng tiền:</strong>
            {{ order.totalAmount.toLocaleString() }} đ
          </div>

          <div>
            <strong>Ngày mua:</strong>
            {{ new Date(order.createdAt).toLocaleString() }}
          </div>
        </div>

        <!-- Items -->
        <div class="order-items">
          <div
              v-for="(item, index) in order.items"
              :key="index"
              class="order-item"
          >
            <div class="product-name">
              {{ item.productName }}
            </div>

            <div>Giá: {{ item.price.toLocaleString() }} đ</div>
            <div>Số lượng: {{ item.quantity }}</div>

            <!-- KEY -->
            <div v-if="item.key" class="delivery-box key-box">
              🔑 Key của bạn:
              <span class="key-value">
                {{ item.key }}
              </span>
            </div>

            <!-- ACCOUNT -->
            <div v-if="item.username" class="delivery-box account-box">
              👤 Tài khoản:
              <b>{{ item.username }}</b>
              <br />
              🔒 Mật khẩu:
              <b>{{ item.password }}</b>
            </div>

          </div>
        </div>

      </div>
    </div>
  </section>
</template>

<style scoped>
.history-section {
  background: #f9f9f9;
  padding: 60px 0;
}

.container {
  max-width: 1100px;
  width: 92%;
  margin: auto;
}

.title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 30px;
  border-left: 4px solid #ff9800;
  padding-left: 12px;
}

.empty {
  padding: 20px;
  background: #fff;
  border: 1px solid #eee;
}

.order-card {
  background: #fff;
  border: 1px solid #e5e5e5;
  padding: 20px;
  margin-bottom: 25px;
  border-radius: 6px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.order-info {
  margin-bottom: 15px;
  color: #555;
}

.order-items {
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.order-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.product-name {
  font-weight: 600;
  margin-bottom: 5px;
}

.success {
  color: green;
  font-weight: 600;
}

/* DELIVERY BOX */
.delivery-box {
  margin-top: 10px;
  padding: 12px;
  border-radius: 6px;
  font-size: 14px;
}

.key-box {
  background: #e0f7fa;
  border-left: 4px solid #0097a7;
}

.account-box {
  background: #f3e5f5;
  border-left: 4px solid #8e24aa;
}

.key-value {
  background: #0097a7;
  color: #fff;
  padding: 6px 12px;
  border-radius: 6px;
  margin-left: 6px;
  font-weight: 600;
}
</style>
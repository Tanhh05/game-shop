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
    console.error("Không tải được giỏ hàng")
  }
})
</script>

<template>
  <section class="history-section">
    <div class="container">
      <h2 class="title">LỊCH SỬ GIAO DỊCH</h2>

      <div v-if="orders.length === 0">
        Bạn chưa có giao dịch nào.
      </div>

      <div
          v-for="order in orders"
          :key="order.id"
          class="order-card"
      >
        <div class="order-header">
          <div>
            <strong>Mã đơn:</strong> #{{ order.id }}
          </div>

          <div>
            <strong>Trạng thái:</strong>
            <span :class="order.status">
              {{ order.status }}
            </span>
          </div>
        </div>

        <div class="order-info">
          <div>
            <strong>Tổng tiền:</strong>
            {{ order.totalAmount.toLocaleString() }} đ
          </div>

          <div>
            <strong>Ngày tạo:</strong>
            {{ new Date(order.createdAt).toLocaleString() }}
          </div>
        </div>

        <div class="order-items">
          <div
              v-for="(item, index) in order.items"
              :key="index"
              class="order-item"
          >
            <div><b>{{ item.productName }}</b></div>
            <div>Giá: {{ item.price.toLocaleString() }} đ</div>
            <div>Số lượng: {{ item.quantity }}</div>
          </div>
        </div>

      </div>

    </div>
  </section>
</template>

<style scoped>
.history-section {
  background: #fff;
  padding: 60px 0;
}

.container {
  max-width: 1200px;
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

.order-card {
  border: 1px solid #e5e5e5;
  padding: 20px;
  margin-bottom: 25px;
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
  padding-top: 10px;
}

.order-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.PENDING {
  color: orange;
  font-weight: 600;
}

.SUCCESS {
  color: green;
  font-weight: 600;
}

.CANCELLED {
  color: red;
  font-weight: 600;
}
</style>
<script setup>
import { ref, onMounted } from "vue"
import { getPurchaseHistory } from "@/services/order.service"
import { parseApiError } from "@/utils/api-error"

const orders = ref([])
const loading = ref(false)
const error = ref("")
const currentPage = ref(0)
const totalPages = ref(0)
const pageSize = 5

const fetchHistory = async (page = 0) => {
  try {
    loading.value = true
    error.value = ""

    const res = await getPurchaseHistory(page, pageSize)

    orders.value = res.data.content || []
    totalPages.value = res.data.totalPages || 0
    currentPage.value = res.data.number || 0

  } catch (err) {
    error.value = parseApiError(err, "Không tải được lịch sử giao dịch")
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchHistory()
})

const formatCurrency = (value) => {
  if (!value) return "0 đ"
  return value.toLocaleString("vi-VN") + " đ"
}

const formatDate = (value) => {
  if (!value) return "--"
  return new Date(value).toLocaleString("vi-VN")
}

const statusClass = (status) => {
  const normalized = String(status || "").toLowerCase()
  if (normalized.includes("success") || normalized.includes("hoàn") || normalized.includes("thành")) {
    return "status-pill status-pill--success"
  }
  if (normalized.includes("fail") || normalized.includes("hủy") || normalized.includes("cancel")) {
    return "status-pill status-pill--danger"
  }
  if (normalized.includes("pending") || normalized.includes("chờ")) {
    return "status-pill status-pill--pending"
  }
  return "status-pill"
}

</script>
<template>
  <section class="history">
    <div class="history__glow"></div>
    <div class="container">
      <header class="history__header">
        <div>
          <p class="eyebrow">Lịch sử giao dịch</p>
          <h2 class="title">Theo dõi đơn hàng trong một nơi</h2>
          <p class="subtitle">
            Chi tiết từng giao dịch, khóa/ tài khoản được giao, và trạng thái cập nhật theo thời gian thực.
          </p>
        </div>
        <div class="summary">
          <div class="summary-card">
            <div class="summary-label">Tổng đơn</div>
            <div class="summary-value">{{ orders.length }}</div>
          </div>
          <div class="summary-card">
            <div class="summary-label">Trang hiện tại</div>
            <div class="summary-value">{{ totalPages === 0 ? 0 : currentPage + 1 }}</div>
          </div>
        </div>
      </header>

      <div v-if="loading" class="state-card">
        Đang tải dữ liệu...
      </div>

      <div v-else-if="error" class="state-card state-card--error">
        {{ error }}
      </div>

      <div v-else-if="orders.length === 0" class="state-card">
        Bạn chưa có giao dịch nào.
      </div>

      <div v-else class="orders">
        <article v-for="order in orders" :key="order.id" class="order">
          <div class="order__top">
            <div>
              <div class="order__id">Đơn #{{ order.id }}</div>
              <div class="order__time">{{ formatDate(order.createdAt) }}</div>
            </div>
            <div class="order__meta">
              <span :class="statusClass(order.status)">{{ order.status }}</span>
              <div class="order__amount">{{ formatCurrency(order.totalAmount) }}</div>
            </div>
          </div>

          <div class="order__items">
            <div v-for="(item, index) in order.items" :key="index" class="order-item">
              <div class="order-item__main">
                <div class="product-name">{{ item.productName }}</div>
                <div class="order-item__meta">
                  <span>Giá: {{ formatCurrency(item.price) }}</span>
                  <span>Số lượng: {{ item.quantity }}</span>
                </div>
              </div>

              <div class="order-item__deliver">
                <div v-if="item.key" class="delivery-pill delivery-pill--key">
                  Key: <span class="delivery-value">{{ item.key }}</span>
                </div>
                <div v-if="item.username" class="delivery-pill delivery-pill--account">
                  Tài khoản: <span class="delivery-value">{{ item.username }}</span>
                </div>
              </div>
            </div>
          </div>
        </article>
      </div>

      <div v-if="totalPages > 1" class="pagination">
        <button
          class="pagination__btn"
          :disabled="currentPage === 0"
          @click="fetchHistory(currentPage - 1)"
        >
          Trước
        </button>
        <span class="pagination__info">
          Trang {{ currentPage + 1 }} / {{ totalPages }}
        </span>
        <button
          class="pagination__btn"
          :disabled="currentPage === totalPages - 1"
          @click="fetchHistory(currentPage + 1)"
        >
          Sau
        </button>
      </div>
    </div>
  </section>
</template>
<style scoped>
:root {
  --history-bg: #f4f1ec;
  --history-panel: #ffffff;
  --history-dark: #1e1b18;
  --history-muted: #6b6460;
  --history-accent: #ff8a34;
  --history-accent-2: #f8d36b;
  --history-border: #ede6df;
  --history-shadow: 0 24px 60px rgba(34, 24, 10, 0.12);
}

.history {
  position: relative;
  background: radial-gradient(circle at top, #fff6e8 0%, #f6efe6 38%, #f4f1ec 70%);
  padding: 72px 0 90px;
  font-family: "Manrope", "Segoe UI", sans-serif;
  color: var(--history-dark);
  overflow: hidden;
}

.history__glow {
  position: absolute;
  top: -120px;
  right: -80px;
  width: 320px;
  height: 320px;
  background: radial-gradient(circle, rgba(255, 138, 52, 0.35), transparent 70%);
  filter: blur(10px);
  pointer-events: none;
}

.container {
  max-width: 1120px;
  width: 92%;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.history__header {
  display: flex;
  gap: 32px;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 36px;
  flex-wrap: wrap;
}

.eyebrow {
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.24em;
  color: var(--history-muted);
  margin: 0 0 12px;
}

.title {
  font-family: "Space Grotesk", "Manrope", sans-serif;
  font-size: clamp(26px, 3vw, 36px);
  font-weight: 700;
  margin: 0 0 12px;
}

.subtitle {
  max-width: 520px;
  color: var(--history-muted);
  font-size: 15px;
  line-height: 1.6;
  margin: 0;
}

.summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(150px, 1fr));
  gap: 16px;
}

.summary-card {
  background: var(--history-panel);
  border-radius: 16px;
  padding: 18px 20px;
  border: 1px solid var(--history-border);
  box-shadow: 0 10px 24px rgba(35, 26, 14, 0.08);
  min-width: 150px;
}

.summary-label {
  font-size: 12px;
  color: var(--history-muted);
  text-transform: uppercase;
  letter-spacing: 0.18em;
}

.summary-value {
  font-size: 26px;
  font-weight: 700;
  margin-top: 6px;
}

.state-card {
  background: var(--history-panel);
  border: 1px dashed var(--history-border);
  padding: 24px;
  border-radius: 14px;
  text-align: center;
  color: var(--history-muted);
}

.state-card--error {
  border-color: rgba(239, 83, 80, 0.4);
  color: #c0392b;
  background: rgba(239, 83, 80, 0.08);
}

.orders {
  display: grid;
  gap: 20px;
}

.order {
  background: var(--history-panel);
  border-radius: 18px;
  padding: 22px 24px;
  border: 1px solid var(--history-border);
  box-shadow: var(--history-shadow);
}

.order__top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  padding-bottom: 18px;
  border-bottom: 1px solid var(--history-border);
}

.order__id {
  font-weight: 700;
  font-size: 18px;
  margin-bottom: 6px;
}

.order__time {
  color: var(--history-muted);
  font-size: 13px;
}

.order__meta {
  text-align: right;
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-end;
}

.order__amount {
  font-weight: 700;
  font-size: 18px;
  color: var(--history-dark);
}

.status-pill {
  font-size: 12px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 138, 52, 0.12);
  color: #b45309;
  border: 1px solid rgba(255, 138, 52, 0.3);
  text-transform: capitalize;
}

.status-pill--success {
  background: rgba(34, 197, 94, 0.12);
  color: #15803d;
  border-color: rgba(34, 197, 94, 0.3);
}

.status-pill--danger {
  background: rgba(239, 68, 68, 0.12);
  color: #b91c1c;
  border-color: rgba(239, 68, 68, 0.3);
}

.status-pill--pending {
  background: rgba(234, 179, 8, 0.12);
  color: #a16207;
  border-color: rgba(234, 179, 8, 0.35);
}

.order__items {
  padding-top: 18px;
  display: grid;
  gap: 14px;
}

.order-item {
  border-radius: 14px;
  padding: 14px 16px;
  background: #fbfaf7;
  border: 1px solid #f1e8dc;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item__main {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.product-name {
  font-weight: 600;
  font-size: 16px;
}

.order-item__meta {
  color: var(--history-muted);
  font-size: 13px;
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
}

.order-item__deliver {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.delivery-pill {
  font-size: 13px;
  padding: 8px 12px;
  border-radius: 10px;
  border: 1px solid transparent;
  background: rgba(52, 211, 153, 0.1);
  color: #047857;
}

.delivery-pill--key {
  background: rgba(59, 130, 246, 0.12);
  color: #1d4ed8;
  border-color: rgba(59, 130, 246, 0.3);
}

.delivery-pill--account {
  background: rgba(168, 85, 247, 0.12);
  color: #7c3aed;
  border-color: rgba(168, 85, 247, 0.3);
}

.delivery-value {
  font-weight: 600;
  margin-left: 6px;
}

.pagination {
  margin-top: 32px;
  display: flex;
  justify-content: center;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.pagination__btn {
  padding: 8px 16px;
  border-radius: 10px;
  border: 1px solid var(--history-border);
  background: var(--history-panel);
  color: var(--history-dark);
  cursor: pointer;
  font-weight: 600;
}

.pagination__btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination__info {
  font-size: 14px;
  color: var(--history-muted);
}

@media (max-width: 720px) {
  .history__header {
    flex-direction: column;
  }

  .summary {
    width: 100%;
    grid-template-columns: repeat(2, minmax(120px, 1fr));
  }

  .order__top {
    flex-direction: column;
    align-items: flex-start;
  }

  .order__meta {
    align-items: flex-start;
    text-align: left;
  }
}
</style>

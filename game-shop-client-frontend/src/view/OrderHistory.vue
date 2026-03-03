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

</script>
<template>
  <section class="history-section">
    <div class="container">
      <h2 class="title">LỊCH SỬ GIAO DỊCH</h2>

      <!-- Loading -->
      <div v-if="loading" class="loading">
        Đang tải dữ liệu...
      </div>

      <!-- Empty -->
      <div v-else-if="error" class="empty">
        {{ error }}
      </div>

      <!-- Empty -->
      <div v-else-if="orders.length === 0" class="empty">
        Bạn chưa có giao dịch nào.
      </div>

      <!-- Orders -->
      <div
          v-else
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
            <span class="success">
              {{ order.status }}
            </span>
          </div>
        </div>

        <!-- Info -->
        <div class="order-info">
          <div>
            <strong>Tổng tiền:</strong>
            {{ formatCurrency(order.totalAmount) }}
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

            <div>Giá: {{ formatCurrency(item.price) }}</div>
            <div>Số lượng: {{ item.quantity }}</div>

            <!-- KEY -->
            <div v-if="item.key" class="delivery-box key-box">
              🔑 Key của bạn:
              <span class="key-value">
                {{ (item.key) }}
              </span>
            </div>

            <!-- ACCOUNT (Không hiển thị password) -->
            <div v-if="item.username" class="delivery-box account-box">
              👤 Tài khoản:
              <b>{{ item.username }}</b>
            </div>

          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div
          v-if="totalPages > 1"
          class="pagination"
      >
        <button
            :disabled="currentPage === 0"
            @click="fetchHistory(currentPage - 1)"
        >
          ◀ Trước
        </button>

        <span>
          Trang {{ currentPage + 1 }} / {{ totalPages }}
        </span>

        <button
            :disabled="currentPage === totalPages - 1"
            @click="fetchHistory(currentPage + 1)"
        >
          Sau ▶
        </button>
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

.loading,
.empty {
  padding: 20px;
  background: #fff;
  border: 1px solid #eee;
  text-align: center;
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

.pagination {
  margin-top: 25px;
  display: flex;
  justify-content: center;
  gap: 15px;
  align-items: center;
}

.pagination button {
  padding: 6px 14px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  border-radius: 4px;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>

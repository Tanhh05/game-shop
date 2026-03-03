<template>
  <div class="page">
    <div class="page-header">
      <div>
        <div class="page-title">Quản Lý Orders</div>
        <div class="page-subtitle">Theo dõi đơn hàng và trạng thái thanh toán.</div>
      </div>
    </div>

    <div class="card soft">
      <div class="form-grid">
        <input v-model="filters.query" class="input" placeholder="Tìm theo ID, username" />
        <select v-model="filters.status" class="select">
          <option value="">Tất cả trạng thái</option>
          <option value="SUCCESS">SUCCESS</option>
          <option value="PENDING">PENDING</option>
          <option value="FAILED">FAILED</option>
        </select>
      </div>
      <div class="helper">Hiển thị {{ filteredOrders.length }} / {{ orders.length }} đơn ở trang hiện tại.</div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>User</th>
            <th>Tổng tiền</th>
            <th>Status</th>
            <th>Số item</th>
            <th>Thời gian</th>
            <th>Chi tiết</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="filteredOrders.length === 0">
            <td colspan="7" class="helper">Không có order</td>
          </tr>
          <tr v-for="o in filteredOrders" :key="o.id">
            <td>#{{ o.id }}</td>
            <td>{{ o.username }} ({{ o.userId }})</td>
            <td>{{ formatPrice(o.totalAmount) }}</td>
            <td>
              <span class="badge" :class="statusClass(o.status)">{{ o.status }}</span>
            </td>
            <td>{{ o.itemCount }}</td>
            <td>{{ formatDate(o.createdAt) }}</td>
            <td><button class="btn btn-ghost" @click="viewDetail(o)">Xem</button></td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button class="btn btn-outline" :disabled="page===0" @click="fetchOrders(page-1)">←</button>
      <span>Trang {{ page + 1 }} / {{ totalPages }}</span>
      <button class="btn btn-outline" :disabled="page===totalPages-1" @click="fetchOrders(page+1)">→</button>
    </div>

    <div v-if="detail" class="card">
      <div class="page-header">
        <div>
          <div class="page-title">Order #{{ detail.order.id }}</div>
          <div class="page-subtitle">Tổng quan đơn hàng và ví user.</div>
        </div>
        <button class="btn btn-outline" @click="detail = null">Đóng</button>
      </div>

      <div class="form-grid">
        <div>
          <div class="helper">User</div>
          <div>{{ detail.order.username }} ({{ detail.order.userId }})</div>
        </div>
        <div>
          <div class="helper">Status</div>
          <span class="badge" :class="statusClass(detail.order.status)">{{ detail.order.status }}</span>
        </div>
        <div>
          <div class="helper">Tổng tiền</div>
          <div>{{ formatPrice(detail.order.totalAmount) }}</div>
        </div>
        <div>
          <div class="helper">Số item</div>
          <div>{{ detail.order.itemCount }}</div>
        </div>
        <div>
          <div class="helper">Thời gian</div>
          <div>{{ formatDate(detail.order.createdAt) }}</div>
        </div>
        <div>
          <div class="helper">Balance</div>
          <div>{{ formatPrice(detail.wallet?.balance || 0) }}</div>
        </div>
        <div>
          <div class="helper">Orders của user</div>
          <div>{{ detail.userOrders.length }}</div>
        </div>
      </div>

      <div style="margin-top:12px;">
        <div class="card-title">Lịch sử orders của user</div>
        <div class="table-wrap" style="margin-top:10px;">
          <table class="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Status</th>
                <th>Tổng tiền</th>
                <th>Thời gian</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="detail.userOrders.length === 0">
                <td colspan="4" class="helper">Không có dữ liệu</td>
              </tr>
              <tr v-for="o in detail.userOrders.slice(0, 5)" :key="o.id">
                <td>#{{ o.id }}</td>
                <td><span class="badge" :class="statusClass(o.status)">{{ o.status }}</span></td>
                <td>{{ formatPrice(o.totalAmount) }}</td>
                <td>{{ formatDate(o.createdAt) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from "vue"
import { getOrderDetail, getOrders, getUserOrders, getUserWallet } from "@/services/admin.service"
import { parseApiError } from "@/utils/api-error"

const orders = ref([])
const detail = ref(null)
const error = ref("")
const page = ref(0)
const totalPages = ref(0)
const size = 20

const filters = ref({
  query: "",
  status: ""
})

const fetchOrders = async (nextPage = page.value) => {
  try {
    error.value = ""
    const res = await getOrders(nextPage, size)
    orders.value = res.data?.content || []
    totalPages.value = res.data?.totalPages || 0
    page.value = res.data?.number ?? nextPage
  } catch (e) {
    error.value = parseApiError(e, "Không tải được danh sách orders")
  }
}

const filteredOrders = computed(() => {
  const q = filters.value.query.trim().toLowerCase()
  return orders.value.filter((o) => {
    const matchQuery = !q || String(o.id).includes(q) || String(o.username || "").toLowerCase().includes(q)
    const matchStatus = !filters.value.status || o.status === filters.value.status
    return matchQuery && matchStatus
  })
})

const viewDetail = async (summary) => {
  try {
    const [orderRes, walletRes, userOrdersRes] = await Promise.all([
      getOrderDetail(summary.id),
      getUserWallet(summary.userId),
      getUserOrders(summary.userId)
    ])

    detail.value = {
      order: orderRes.data,
      wallet: walletRes.data,
      userOrders: userOrdersRes.data || []
    }
  } catch (e) {
    error.value = parseApiError(e, "Không tải được chi tiết order")
  }
}

const formatPrice = (n) => new Intl.NumberFormat("vi-VN").format(n || 0) + " đ"

const formatDate = (value) => {
  if (!value) return "-"
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value
  return d.toLocaleString("vi-VN")
}

const statusClass = (status) => {
  if (status === "SUCCESS") return "success"
  if (status === "FAILED") return "danger"
  return "warning"
}

onMounted(() => fetchOrders())
</script>

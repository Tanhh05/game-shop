<template>
  <div class="page">
    <div class="page-header">
      <div>
        <div class="page-title">Dashboard Overview</div>
        <div class="page-subtitle">Real-time summary of games, products, and sales.</div>
      </div>
      <div style="display:flex; gap:10px; flex-wrap:wrap;">
        <router-link class="btn btn-outline" to="/admin/games">Quản lý Games</router-link>
        <router-link class="btn btn-outline" to="/admin/products">Quản lý Products</router-link>
      </div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div class="metrics-grid">
      <div class="card">
        <div class="metric-label">Total Games (active)</div>
        <div class="metric-value">{{ loading ? '...' : metrics.games }}</div>
      </div>

      <div class="card">
        <div class="metric-label">Total Products (active)</div>
        <div class="metric-value">{{ loading ? '...' : metrics.products }}</div>
      </div>

      <div class="card">
        <div class="metric-label">Total Orders</div>
        <div class="metric-value">{{ loading ? '...' : metrics.orders }}</div>
      </div>

      <div class="card">
        <div class="metric-label">Total Users</div>
        <div class="metric-value">{{ loading ? '...' : metrics.users }}</div>
      </div>

      <div class="card">
        <div class="metric-label">Total Sales (trang hiện tại)</div>
        <div class="metric-value">{{ loading ? '...' : formatPrice(metrics.sales) }}</div>
      </div>

      <div class="card">
        <div class="metric-label">Available Inventory</div>
        <div class="metric-value">{{ loading ? '...' : metrics.availableInventory }}</div>
      </div>
    </div>

    <div class="metrics-grid" style="grid-template-columns: repeat(2, minmax(0, 1fr));">
      <div class="card">
        <div class="card-title">Orders by Status</div>
        <div class="chart">
          <div v-for="s in statusData" :key="s.label" class="chart-row">
            <div class="chart-label">{{ s.label }}</div>
            <div class="chart-bar">
              <span :style="{ width: s.percent + '%', background: s.color }"></span>
            </div>
            <div class="chart-value">{{ s.count }}</div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-title">Sales (7 latest days)</div>
        <div class="spark">
          <div v-for="d in salesSeries" :key="d.label" class="spark-col">
            <div class="spark-bar" :style="{ height: d.height + '%' }"></div>
            <div class="spark-label">{{ d.label }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="metrics-grid" style="grid-template-columns: repeat(3, minmax(0, 1fr));">
      <div class="card soft">
        <div class="card-title">Quick actions</div>
        <p class="helper">Thao tác nhanh để duy trì vận hành.</p>
        <div style="display:flex; gap:10px; flex-wrap:wrap;">
          <router-link class="btn btn-primary" to="/admin/games">+ Game</router-link>
          <router-link class="btn btn-outline" to="/admin/products/add">+ Product</router-link>
          <router-link class="btn btn-outline" to="/admin/inventory">Import Inventory</router-link>
        </div>
      </div>

      <div class="card">
        <div class="card-title">Today focus</div>
        <p class="helper">Kiểm tra status orders và bổ sung tồn kho trước giờ cao điểm.</p>
      </div>

      <div class="card">
        <div class="card-title">Health check</div>
        <p class="helper">Đảm bảo games và products đều đang active để hiển thị cho user.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue"
import { getInventoryStats, getOrders, getUsers } from "@/services/admin.service"
import { getGames } from "@/services/game.service"
import productService from "@/services/product.service"
import { parseApiError } from "@/utils/api-error"

const loading = ref(false)
const error = ref("")

const metrics = ref({
  games: 0,
  products: 0,
  users: 0,
  orders: 0,
  sales: 0,
  availableInventory: 0
})

const ordersSnapshot = ref([])

const formatPrice = (n) => new Intl.NumberFormat("vi-VN").format(n || 0) + " đ"

const statusData = computed(() => {
  const total = ordersSnapshot.value.length || 1
  const counts = ordersSnapshot.value.reduce((acc, o) => {
    const key = o.status || "PENDING"
    acc[key] = (acc[key] || 0) + 1
    return acc
  }, {})

  const mapRow = (label, color) => ({
    label,
    color,
    count: counts[label] || 0,
    percent: Math.round(((counts[label] || 0) / total) * 100)
  })

  return [
    mapRow("SUCCESS", "#16a34a"),
    mapRow("PENDING", "#f59e0b"),
    mapRow("FAILED", "#ef4444")
  ]
})

const salesSeries = computed(() => {
  const map = new Map()
  ordersSnapshot.value.forEach((o) => {
    if (!o.createdAt) return
    const d = new Date(o.createdAt)
    if (Number.isNaN(d.getTime())) return
    const key = d.toLocaleDateString("vi-VN", { month: "2-digit", day: "2-digit" })
    map.set(key, (map.get(key) || 0) + (o.totalAmount || 0))
  })

  const entries = Array.from(map.entries()).slice(-7)
  const max = Math.max(...entries.map(([, v]) => v), 1)
  return entries.map(([label, value]) => ({
    label,
    height: Math.round((value / max) * 100)
  }))
})

const fetchMetrics = async () => {
  try {
    loading.value = true
    error.value = ""

    const [gamesRes, productsRes, usersRes, ordersRes, inventoryRes] = await Promise.all([
      getGames(0, 100),
      productService.getProducts({ page: 0, size: 200, sortBy: "id", direction: "desc" }),
      getUsers(0, 1),
      getOrders(0, 200),
      getInventoryStats()
    ])

    const games = gamesRes.data?.content || []
    const products = productsRes.data?.content || []
    const orders = ordersRes.data?.content || []
    const inventory = inventoryRes.data || []

    ordersSnapshot.value = orders

    metrics.value = {
      games: gamesRes.data?.totalElements ?? games.length,
      products: productsRes.data?.totalElements ?? products.length,
      users: usersRes.data?.totalElements ?? 0,
      orders: ordersRes.data?.totalElements ?? orders.length,
      sales: orders.reduce((sum, o) => sum + (o.totalAmount || 0), 0),
      availableInventory: inventory.reduce((sum, s) => sum + (s.availableKeys || 0) + (s.availableAccounts || 0), 0)
    }
  } catch (e) {
    error.value = parseApiError(e, "Không tải được dashboard metrics")
  } finally {
    loading.value = false
  }
}

onMounted(fetchMetrics)
</script>

<style scoped>
.chart {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 12px;
}

.chart-row {
  display: grid;
  grid-template-columns: 90px 1fr 40px;
  align-items: center;
  gap: 10px;
}

.chart-label {
  font-weight: 600;
  font-size: 12px;
  color: #6b6f76;
}

.chart-bar {
  height: 10px;
  background: #f1efe7;
  border-radius: 999px;
  overflow: hidden;
}

.chart-bar span {
  display: block;
  height: 100%;
  border-radius: 999px;
}

.chart-value {
  text-align: right;
  font-size: 12px;
  color: #6b6f76;
}

.spark {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(40px, 1fr));
  gap: 10px;
  align-items: end;
  height: 160px;
  margin-top: 12px;
}

.spark-col {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  gap: 8px;
  align-items: center;
}

.spark-bar {
  width: 100%;
  background: linear-gradient(180deg, #0ea5a4, #1f6feb);
  border-radius: 8px 8px 4px 4px;
  min-height: 10px;
}

.spark-label {
  font-size: 11px;
  color: #6b6f76;
}

@media (max-width: 900px) {
  .metrics-grid {
    grid-template-columns: 1fr !important;
  }
}
</style>

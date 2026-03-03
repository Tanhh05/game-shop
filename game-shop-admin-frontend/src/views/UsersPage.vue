<template>
  <div class="page">
    <div class="page-header">
      <div>
        <div class="page-title">Quản Lý Users</div>
        <div class="page-subtitle">Theo dõi và khóa/mở tài khoản người dùng.</div>
      </div>
    </div>

    <div class="card soft">
      <div class="form-grid">
        <input v-model="filters.query" class="input" placeholder="Tìm theo username, email" />
        <select v-model="filters.role" class="select">
          <option value="">Tất cả role</option>
          <option value="USER">USER</option>
          <option value="RESELLER">RESELLER</option>
          <option value="ADMIN">ADMIN</option>
        </select>
      </div>
      <div class="form-grid" style="margin-top:10px;">
        <select v-model="filters.status" class="select">
          <option value="">Tất cả trạng thái</option>
          <option value="active">ACTIVE</option>
          <option value="blocked">BLOCKED</option>
        </select>
      </div>
      <div class="helper">Hiển thị {{ filteredUsers.length }} / {{ users.length }} user ở trang hiện tại.</div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Role</th>
            <th>Status</th>
            <th>Hành động</th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="filteredUsers.length === 0">
            <td colspan="6" class="helper">Không có user</td>
          </tr>

          <tr v-for="u in filteredUsers" :key="u.id">
            <td>#{{ u.id }}</td>
            <td>{{ u.username }}</td>
            <td>{{ u.email }}</td>
            <td>{{ u.role }}</td>
            <td>
              <span class="badge" :class="u.status ? 'success' : 'danger'">
                {{ u.status ? 'ACTIVE' : 'BLOCKED' }}
              </span>
            </td>
            <td style="display:flex; gap:8px; flex-wrap:wrap;">
              <button class="btn btn-ghost" @click="viewDetail(u)">Chi tiết</button>
              <button class="btn btn-outline" @click="toggleStatus(u)" :disabled="loadingActionId === u.id">
                {{ u.status ? 'Khóa' : 'Mở' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button class="btn btn-outline" :disabled="page===0" @click="fetchUsers(page-1)">←</button>
      <span>Trang {{ page + 1 }} / {{ totalPages }}</span>
      <button class="btn btn-outline" :disabled="page===totalPages-1" @click="fetchUsers(page+1)">→</button>
    </div>

    <div v-if="detail" class="card">
      <div class="page-header">
        <div>
          <div class="page-title">User #{{ detail.profile.id }}</div>
          <div class="page-subtitle">Thông tin tài khoản và lịch sử giao dịch.</div>
        </div>
        <button class="btn btn-outline" @click="detail = null">Đóng</button>
      </div>

      <div class="form-grid">
        <div>
          <div class="helper">Username</div>
          <div>{{ detail.profile.username }}</div>
        </div>
        <div>
          <div class="helper">Email</div>
          <div>{{ detail.profile.email }}</div>
        </div>
        <div>
          <div class="helper">Role</div>
          <div>{{ detail.profile.role }}</div>
        </div>
        <div>
          <div class="helper">Status</div>
          <span class="badge" :class="detail.profile.status ? 'success' : 'danger'">
            {{ detail.profile.status ? 'ACTIVE' : 'BLOCKED' }}
          </span>
        </div>
        <div>
          <div class="helper">Created At</div>
          <div>{{ formatDate(detail.profile.createdAt) }}</div>
        </div>
        <div>
          <div class="helper">Balance</div>
          <div>{{ formatPrice(detail.wallet?.balance || 0) }}</div>
        </div>
      </div>

      <div style="margin-top:12px;">
        <div class="card-title">Orders gần nhất</div>
        <div class="table-wrap" style="margin-top:10px;">
          <table class="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Status</th>
                <th>Total</th>
                <th>Thời gian</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="detail.orders.length === 0">
                <td colspan="4" class="helper">Không có đơn hàng</td>
              </tr>
              <tr v-for="o in detail.orders.slice(0, 5)" :key="o.id">
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
import { changeUserStatus, getUserDetail, getUserOrders, getUserWallet, getUsers } from "@/services/admin.service"
import { parseApiError } from "@/utils/api-error"
import { useUi } from "@/composables/useUi"

const ui = useUi()
const users = ref([])
const error = ref("")
const page = ref(0)
const totalPages = ref(0)
const size = 20
const loadingActionId = ref(null)
const detail = ref(null)

const filters = ref({
  query: "",
  role: "",
  status: ""
})

const fetchUsers = async (nextPage = page.value) => {
  try {
    error.value = ""
    const res = await getUsers(nextPage, size)
    users.value = res.data?.content || []
    totalPages.value = res.data?.totalPages || 0
    page.value = res.data?.number ?? nextPage
  } catch (e) {
    error.value = parseApiError(e, "Không tải được danh sách users")
  }
}

const filteredUsers = computed(() => {
  const q = filters.value.query.trim().toLowerCase()
  return users.value.filter((u) => {
    const matchQuery = !q || String(u.username || "").toLowerCase().includes(q) || String(u.email || "").toLowerCase().includes(q)
    const matchRole = !filters.value.role || u.role === filters.value.role
    const matchStatus = !filters.value.status || (filters.value.status === "active" ? u.status : !u.status)
    return matchQuery && matchRole && matchStatus
  })
})

const toggleStatus = async (user) => {
  const ok = await ui.confirm({
    title: "Đổi trạng thái user",
    message: `Bạn có chắc muốn ${user.status ? "khóa" : "mở"} tài khoản \"${user.username}\"?`
  })
  if (!ok) return

  try {
    loadingActionId.value = user.id
    await changeUserStatus(user.id, !user.status)
    ui.toast("Cập nhật trạng thái user thành công", "success")
    await fetchUsers(page.value)
  } catch (e) {
    error.value = parseApiError(e, "Cập nhật trạng thái user thất bại")
    ui.toast("Cập nhật trạng thái user thất bại", "error")
  } finally {
    loadingActionId.value = null
  }
}

const viewDetail = async (user) => {
  try {
    const [profileRes, walletRes, ordersRes] = await Promise.all([
      getUserDetail(user.id),
      getUserWallet(user.id),
      getUserOrders(user.id)
    ])

    detail.value = {
      profile: profileRes.data,
      wallet: walletRes.data,
      orders: ordersRes.data || []
    }
  } catch (e) {
    error.value = parseApiError(e, "Không tải được chi tiết user")
  }
}

const formatDate = (value) => {
  if (!value) return "-"
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value
  return d.toLocaleString("vi-VN")
}

const formatPrice = (n) => new Intl.NumberFormat("vi-VN").format(n || 0) + " đ"

const statusClass = (status) => {
  if (status === "SUCCESS") return "success"
  if (status === "FAILED") return "danger"
  return "warning"
}

onMounted(() => fetchUsers())
</script>

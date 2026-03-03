<template>
  <div class="page">
    <div class="page-header">
      <div>
        <div class="page-title">Quản Lý Sản Phẩm</div>
        <div class="page-subtitle">Quản lý danh sách sản phẩm và trạng thái bán.</div>
      </div>
      <button class="btn btn-primary" @click="goAdd">+ Thêm sản phẩm mới</button>
    </div>

    <div class="card soft">
      <div class="form-grid">
        <input v-model="filters.query" class="input" placeholder="Tìm theo tên sản phẩm, game" />
        <select v-model="filters.status" class="select">
          <option value="">Tất cả trạng thái</option>
          <option value="active">ĐANG BÁN</option>
          <option value="inactive">NGỪNG BÁN</option>
        </select>
      </div>
      <div class="helper">Hiển thị {{ filteredProducts.length }} / {{ products.length }} sản phẩm ở trang hiện tại.</div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th>Sản phẩm</th>
            <th>Tên game</th>
            <th>Giá gói đầu tiên (VND)</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="filteredProducts.length === 0">
            <td colspan="5" class="helper">Không có sản phẩm</td>
          </tr>

          <tr v-for="p in filteredProducts" :key="p.id">
            <td class="product">
              <img :src="p.thumbnail" alt="thumbnail" />
              <div>
                <div style="font-weight:600;">{{ p.title }}</div>
                <div class="helper">#{{ p.id }}</div>
              </div>
            </td>

            <td>{{ p.gameName }}</td>

            <td style="font-weight:600; color:#0ea5a4;">{{ formatPrice(firstPackagePrice(p)) }}</td>

            <td>
              <span class="badge" :class="p.status ? 'success' : 'danger'">
                {{ p.status ? 'ĐANG BÁN' : 'NGỪNG BÁN' }}
              </span>
            </td>

            <td style="display:flex; gap:8px; flex-wrap:wrap;">
              <button class="btn btn-ghost" @click="notifyUnsupportedEdit">Sửa</button>
              <button class="btn btn-outline" @click="toggleStatus(p)">{{ p.status ? 'Disable' : 'Enable' }}</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button class="btn btn-outline" :disabled="currentPage === 0" @click="changePage(currentPage - 1)">←</button>
      <span>Trang {{ currentPage + 1 }} / {{ totalPages }}</span>
      <button class="btn btn-outline" :disabled="currentPage === totalPages - 1" @click="changePage(currentPage + 1)">→</button>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import productService from "@/services/product.service.js"
import { parseApiError } from "@/utils/api-error"
import { useUi } from "@/composables/useUi"

const ui = useUi()
const router = useRouter()
const products = ref([])
const currentPage = ref(0)
const totalPages = ref(0)
const size = 10
const error = ref("")

const filters = ref({
  query: "",
  status: ""
})

const goAdd = () => router.push("/admin/products/add")

const fetchProducts = async () => {
  try {
    error.value = ""

    const res = await productService.getProducts({
      page: currentPage.value,
      size,
      sortBy: "id",
      direction: "desc"
    })

    products.value = res.data?.content || []
    totalPages.value = res.data?.totalPages || 0
  } catch (e) {
    error.value = parseApiError(e, "Lỗi khi tải sản phẩm")
  }
}

const changePage = (page) => {
  currentPage.value = page
  fetchProducts()
}

const filteredProducts = computed(() => {
  const q = filters.value.query.trim().toLowerCase()
  return products.value.filter((p) => {
    const matchQuery = !q || String(p.title || "").toLowerCase().includes(q) || String(p.gameName || "").toLowerCase().includes(q)
    const matchStatus = !filters.value.status || (filters.value.status === "active" ? p.status : !p.status)
    return matchQuery && matchStatus
  })
})

const notifyUnsupportedEdit = () => {
  ui.toast("BE chưa có endpoint update product. Hiện chỉ hỗ trợ đổi status.", "info")
}

const toggleStatus = async (product) => {
  const ok = await ui.confirm({
    title: "Đổi trạng thái sản phẩm",
    message: `Bạn có chắc muốn ${product.status ? "disable" : "enable"} sản phẩm \"${product.title}\"?`
  })
  if (!ok) return

  try {
    error.value = ""
    await productService.changeProductStatus(product.id, !product.status)
    ui.toast("Cập nhật trạng thái sản phẩm thành công", "success")
    await fetchProducts()
  } catch (e) {
    error.value = parseApiError(e, "Cập nhật trạng thái sản phẩm thất bại")
    ui.toast("Cập nhật trạng thái sản phẩm thất bại", "error")
  }
}

onMounted(fetchProducts)

const firstPackagePrice = (product) => product?.packages?.[0]?.price || 0

const formatPrice = (price) => {
  if (!price) return "0 đ"
  return new Intl.NumberFormat("vi-VN").format(price) + " đ"
}
</script>

<style scoped>
.product {
  display: flex;
  align-items: center;
  gap: 12px;
}

img {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  object-fit: cover;
  border: 1px solid #e4e0d7;
}
</style>

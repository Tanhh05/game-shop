<script setup>
import { ref, onMounted } from "vue"
import { getProducts } from "@/services/product.service"
import ProductCard from "@/components/ProductCard.vue"

const products = ref([])
const currentPage = ref(0)
const totalPages = ref(0)
const pageSize = 8
const loading = ref(false)

const fetchProducts = async () => {
  try {
    loading.value = true

    const res = await getProducts({
      page: currentPage.value,
      size: pageSize,
      sortBy: "createdAt",
      direction: "desc"
    })

    products.value = res.data.content
    totalPages.value = res.data.totalPages

  } catch (err) {
    console.error("Lỗi load sản phẩm:", err)
  } finally {
    loading.value = false
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    fetchProducts()
  }
}

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    fetchProducts()
  }
}

const goToPage = (page) => {
  currentPage.value = page
  fetchProducts()
}

onMounted(fetchProducts)
</script>

<template>
  <section class="catalog">
    <div class="container">
      <header class="catalog__header">
        <div>
          <p class="eyebrow">Danh mục</p>
          <h2 class="title">Khám phá kho game mới nhất</h2>
          <p class="subtitle">Sản phẩm được cập nhật liên tục, giao dịch nhanh và an toàn.</p>
        </div>
        <div class="catalog__meta">
          <div class="meta-card">
            <div class="meta-label">Trang</div>
            <div class="meta-value">{{ totalPages === 0 ? 0 : currentPage + 1 }}</div>
          </div>
          <div class="meta-card">
            <div class="meta-label">Sản phẩm</div>
            <div class="meta-value">{{ products.length }}</div>
          </div>
        </div>
      </header>

      <div v-if="loading" class="state-card">
        Đang tải sản phẩm...
      </div>

      <div v-else class="grid">
        <ProductCard v-for="item in products" :key="item.id" :product="item" />
      </div>

      <div class="pagination" v-if="totalPages > 1">
        <button class="pagination__btn" @click="prevPage" :disabled="currentPage === 0">
          Trang trước
        </button>

        <button
          v-for="page in totalPages"
          :key="page"
          :class="['pagination__btn', { active: currentPage === page - 1 }]"
          @click="goToPage(page - 1)"
        >
          {{ page }}
        </button>

        <button class="pagination__btn" @click="nextPage" :disabled="currentPage === totalPages - 1">
          Trang sau
        </button>
      </div>
    </div>
  </section>
</template>

<style scoped>

.catalog {
  background: #f8fafc;
  padding: 64px 0 80px;
  width: 100%;
  font-family: "Manrope", "Segoe UI", sans-serif;
}

.container {
  max-width: 1200px;
  width: 92%;
  margin: 0 auto;
}

.catalog__header {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: flex-start;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.eyebrow {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.3em;
  color: #94a3b8;
  margin: 0 0 12px;
}

.title {
  font-family: "Space Grotesk", "Manrope", sans-serif;
  font-size: clamp(26px, 3vw, 34px);
  margin: 0 0 12px;
  color: #0f172a;
}

.subtitle {
  margin: 0;
  color: #64748b;
  font-size: 15px;
  line-height: 1.6;
  max-width: 520px;
}

.catalog__meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(130px, 1fr));
  gap: 12px;
}

.meta-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 14px 16px;
}

.meta-label {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.18em;
  color: #94a3b8;
}

.meta-value {
  font-size: 22px;
  font-weight: 700;
  margin-top: 6px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 24px;
}

.state-card {
  text-align: center;
  padding: 40px 0;
  font-size: 15px;
  color: #64748b;
  border: 1px dashed #e2e8f0;
  border-radius: 12px;
  background: #ffffff;
}

.pagination {
  margin-top: 36px;
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.pagination__btn {
  padding: 8px 14px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  cursor: pointer;
  transition: 0.2s;
  border-radius: 999px;
  font-weight: 600;
  color: #334155;
}

.pagination__btn:hover {
  background: #f97316;
  color: #ffffff;
  border-color: #f97316;
}

.pagination__btn.active {
  background: #f97316;
  color: #ffffff;
  border-color: #f97316;
  font-weight: 700;
}

.pagination__btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .catalog__meta {
    width: 100%;
    grid-template-columns: repeat(2, minmax(120px, 1fr));
  }
}
</style>

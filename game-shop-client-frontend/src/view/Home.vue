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
  <section class="section">
    <div class="container">
      <h2 class="title">DANH SÁCH GAME</h2>

      <div v-if="loading" class="loading">
        Đang tải sản phẩm...
      </div>

      <div v-else class="grid">
        <ProductCard
            v-for="item in products"
            :key="item.id"
            :product="item"
        />
      </div>

      <!-- Pagination -->
      <div class="pagination" v-if="totalPages > 1">
        <button
            @click="prevPage"
            :disabled="currentPage === 0"
        >
          Trang trước
        </button>

        <button
            v-for="page in totalPages"
            :key="page"
            :class="{ active: currentPage === page - 1 }"
            @click="goToPage(page - 1)"
        >
          {{ page }}
        </button>

        <button
            @click="nextPage"
            :disabled="currentPage === totalPages - 1"
        >
          Trang sau
        </button>
      </div>
    </div>
  </section>
</template>

<style scoped>
.section {
  background: #ffffff;
  padding: 60px 0;
  width: 100%;
}

.container {
  max-width: 1200px;
  width: 92%;
  margin: auto;
}

.title {
  font-size: 24px;
  font-weight: 700;
  color: #111;
  margin-bottom: 35px;
  padding-left: 14px;
  border-left: 4px solid #ff9800;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 25px;
}

.loading {
  text-align: center;
  padding: 40px 0;
  font-size: 16px;
}

/* Pagination */
.pagination {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.pagination button {
  padding: 8px 14px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  transition: 0.2s;
  border-radius: 6px;
}

.pagination button:hover {
  background: #ff9800;
  color: white;
}

.pagination button.active {
  background: #ff9800;
  color: white;
  font-weight: bold;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Responsive */
@media (max-width: 992px) {
  .grid {
    gap: 20px;
  }

  .title {
    font-size: 22px;
  }
}

@media (max-width: 600px) {
  .section {
    padding: 40px 0;
  }

  .title {
    font-size: 20px;
  }
}
</style>
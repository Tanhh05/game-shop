<template>
  <div class="product-page">

    <!-- HEADER -->
    <div class="header">
      <h2>Quản Lý Sản Phẩm</h2>
      <button class="btn-add" @click="goAdd">+ Thêm sản phẩm mới</button>
    </div>

    <!-- TABLE -->
    <div class="table-box">
      <table>
        <thead>
        <tr>
          <th>Sản phẩm</th>
          <th>Tên game</th>
          <th>Giá (VND)</th>
          <th>Trạng thái</th>
          <th>Hành động</th>
        </tr>
        </thead>

        <tbody>
        <tr v-if="products.length === 0">
          <td colspan="5" class="empty">Không có sản phẩm</td>
        </tr>

        <tr v-for="p in products" :key="p.id">
          <td class="product">
            <img :src="p.thumbnail" alt="thumbnail" />
            {{ p.title }}
          </td>

          <td>{{ p.gameName }}</td>

          <td class="price">
            {{ formatPrice(p.price) }}
          </td>

          <td>
            <span :class="p.status ? 'active' : 'inactive'">
              {{ p.status ? 'ĐANG BÁN' : 'NGỪNG BÁN' }}
            </span>
          </td>

          <td class="actions">
            ✏️ 🗑️
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- PAGINATION -->
    <div class="pagination" v-if="totalPages > 1">
      <button
          :disabled="currentPage === 0"
          @click="changePage(currentPage - 1)">
        ←
      </button>

      <span>
        Trang {{ currentPage + 1 }} / {{ totalPages }}
      </span>

      <button
          :disabled="currentPage === totalPages - 1"
          @click="changePage(currentPage + 1)">
        →
      </button>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import productService from "@/services/product.service.js";

const router = useRouter();
const goAdd = () => {
  // điều hướng tới trang thêm sản phẩm đã đăng ký trong router
  router.push('/admin/products/add');
};

const products = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const size = 10;

const fetchProducts = async () => {
  try {
    const res = await productService.getProducts({
      page: currentPage.value,
      size: size,
      sortBy: "id",
      direction: "desc"
    });

    // 🔥 backend trả Page
    products.value = res.data.content;
    totalPages.value = res.data.totalPages;

  } catch (error) {
    console.error("Lỗi khi tải sản phẩm:", error);
  }
};

const changePage = (page) => {
  currentPage.value = page;
  fetchProducts();
};

onMounted(fetchProducts);

const formatPrice = (price) => {
  if (!price) return "0 đ";
  return new Intl.NumberFormat("vi-VN").format(price) + " đ";
};
</script>

<style scoped>
.product-page {
  padding: 30px;
  color: #fff;
}

/* HEADER */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.header h2 {
  font-size: 22px;
  font-weight: 600;
}

.btn-add {
  background: linear-gradient(90deg, #2563eb, #3b82f6);
  border: none;
  padding: 10px 20px;
  border-radius: 12px;
  font-weight: 500;
  color: white;
  cursor: pointer;
  transition: 0.3s;
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 15px rgba(59, 130, 246, 0.4);
}

/* TABLE BOX */
.table-box {
  background: #0f172a;
  padding: 25px;
  border-radius: 20px;
  border: 1px solid #1e293b;
  box-shadow: 0 0 25px rgba(0, 0, 0, 0.4);
}

/* TABLE */
table {
  width: 100%;
  border-collapse: collapse;
}

thead th {
  text-align: left;
  font-size: 13px;
  font-weight: 500;
  color: #94a3b8;
  padding-bottom: 15px;
  border-bottom: 1px solid #1e293b;
}

tbody tr {
  transition: 0.2s;
}

tbody tr:hover {
  background: #1e293b;
  border-radius: 12px;
}

td {
  padding: 18px 10px;
  font-size: 14px;
}

/* PRODUCT COLUMN */
.product {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 500;
}

img {
  width: 45px;
  height: 45px;
  border-radius: 10px;
  object-fit: cover;
  border: 1px solid #334155;
}

/* PRICE */
.price {
  color: #3b82f6;
  font-weight: 600;
}

/* STATUS */
.active {
  background: rgba(16, 185, 129, 0.2);
  color: #10b981;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.inactive {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

/* ACTIONS */
.actions {
  font-size: 16px;
  display: flex;
  gap: 10px;
}

.actions span {
  cursor: pointer;
  transition: 0.2s;
}

.actions span:hover {
  transform: scale(1.2);
}

/* EMPTY */
.empty {
  text-align: center;
  padding: 30px;
  color: #64748b;
}

/* PAGINATION */
.pagination {
  margin-top: 25px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
}

.pagination button {
  background: #1e293b;
  border: none;
  color: white;
  padding: 8px 14px;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s;
}

.pagination button:hover {
  background: #2563eb;
}

.pagination button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>

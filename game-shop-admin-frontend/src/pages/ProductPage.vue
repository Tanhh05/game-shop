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
import productService from "@/services/product.service";

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
  padding: 20px;
  color: white;
}

.header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.btn-add {
  background: #ff8800;
  border: none;
  padding: 8px 18px;
  border-radius: 20px;
  font-weight: bold;
  cursor: pointer;
}

.table-box {
  background: #1a120b;
  padding: 20px;
  border-radius: 15px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px;
  text-align: left;
}

thead {
  border-bottom: 1px solid #333;
}

.product {
  display: flex;
  align-items: center;
  gap: 10px;
}

img {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  object-fit: cover;
}

.price {
  color: #ff8800;
  font-weight: bold;
}

.active {
  background: green;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 12px;
}

.inactive {
  background: gray;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 12px;
}

.empty {
  text-align: center;
  padding: 20px;
  color: #888;
}

.actions {
  cursor: pointer;
  font-size: 18px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
}

.pagination button {
  padding: 6px 12px;
  cursor: pointer;
}
</style>

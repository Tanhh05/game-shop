<template>
  <div class="container" v-if="product">
    <div class="left">
      <img :src="product.thumbnail" :alt="product.title" />
    </div>

    <div class="right">
      <h1>{{ product.title }}</h1>

      <p class="price">
        {{ product.price.toLocaleString() }} ₫
      </p>

      <p class="short">
        {{ product.shortDescription }}
      </p>

      <div class="desc">
        <h3>Mô tả chi tiết</h3>
        <p>{{ product.description }}</p>
      </div>

      <button class="buy-btn">
        Mua ngay
      </button>
    </div>
  </div>

  <div v-else class="loading">
    Đang tải sản phẩm...
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProductBySlug} from "@/services/product/service.js";

const route = useRoute()
const product = ref(null)

onMounted(async () => {
  try {
    product.value = await getProductBySlug(route.params.slug)
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  padding: 30px;
}

.left img {
  width: 100%;
  border-radius: 8px;
}

.right h1 {
  margin-bottom: 10px;
}

.price {
  color: #e67e22;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 10px;
}

.short {
  margin-bottom: 20px;
}

.desc h3 {
  margin-bottom: 6px;
}

.buy-btn {
  margin-top: 20px;
  background: #f39c12;
  color: #fff;
  border: none;
  padding: 12px 24px;
  font-size: 16px;
  cursor: pointer;
}
</style>

<script setup>
import { formatPrice } from "@/utils/format"

defineProps({
  product: Object
})
</script>

<template>
  <div class="card">
    <div class="image-wrapper">
      <img :src="product.thumbnail" />
    </div>

    <div class="content">
      <h3>{{ product.title }}</h3>
      <p>{{ product.shortDescription }}</p>

      <div class="bottom">
        <span class="price">
          {{ formatPrice(product.price) }}
        </span>

        <router-link
            :to="`/product/${product.slug}`"
            class="btn"
        >
          XEM
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card {
  background: #ffffff;   /* nền trắng */
  border: 1px solid #e5e5e5;
  border-radius: 0;      /* cạnh vuông */
  overflow: hidden;
  transition: 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
}

.image-wrapper {
  width: 100%;
  aspect-ratio: 16/10;
  overflow: hidden;
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Nội dung */
.content {
  padding: 18px;
  background: #ffffff;   /* đảm bảo trắng */
  color: #111;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.content h3 {
  margin-bottom: 8px;
  font-size: 18px;
  font-weight: 600;
}

.content p {
  font-size: 14px;
  color: #555;
  margin-bottom: 15px;
  flex-grow: 1;
}

/* Bottom */
.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Giá */
.price {
  color: #ff9800;
  font-weight: 700;
  font-size: 18px;
}

/* Button */
.btn {
  background: #ff9800;
  padding: 7px 14px;
  color: white;
  text-decoration: none;
  font-weight: 600;
  border-radius: 4px;
  transition: 0.3s;
}

.btn:hover {
  background: #f57c00;
}
</style>
<script setup>
defineProps({
  product: Object
})
</script>

<template>
  <article class="card">
    <div class="image-wrapper">
      <img :src="product.thumbnail" :alt="product.title" />
      <div class="image-tag">Hot</div>
    </div>

    <div class="content">
      <h3>{{ product.title }}</h3>
      <p>{{ product.shortDescription }}</p>

      <div class="bottom">
        <router-link :to="`/product/${product.slug}`" class="btn">
          Xem chi tiết
        </router-link>
      </div>
    </div>
  </article>
</template>

<style scoped>
.card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
  font-family: "Manrope", "Segoe UI", sans-serif;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12);
}

.image-wrapper {
  width: 100%;
  aspect-ratio: 16/10;
  overflow: hidden;
  position: relative;
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(249, 115, 22, 0.9);
  color: #ffffff;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 999px;
}

.content {
  padding: 18px 18px 20px;
  color: #111;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.content h3 {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 700;
  font-family: "Space Grotesk", "Manrope", sans-serif;
}

.content p {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 18px;
  flex-grow: 1;
}

.bottom {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.btn {
  background: #f97316;
  padding: 8px 16px;
  color: #ffffff;
  text-decoration: none;
  font-weight: 600;
  border-radius: 999px;
  transition: background 0.2s ease;
}

.btn:hover {
  background: #ea580c;
}
</style>

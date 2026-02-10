<template>
  <div class="card">
    <img :src="product.thumbnail" alt="product image" />

    <div class="content">
      <h3>{{ product.title }}</h3>
      <p>{{ product.shortDescription }}</p>

      <button @click="goDetail">
        Xem ngay
      </button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const goDetail = () => {
  router.push(`/product/${props.product.slug}`)
}
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 6px;
  overflow: hidden;
}

img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.content {
  padding: 14px;
}

button {
  margin-top: 10px;
  width: 100%;
  background: #f39c12;
  border: none;
  padding: 10px;
  color: #fff;
  font-weight: bold;
  cursor: pointer;
}
</style>

<template>
  <section class="games">
    <h2>DANH SÁCH GAME</h2>

    <div class="grid" v-if="products.length">
      <GameCard
          v-for="item in products"
          :key="item.id"
          :product="item"
      />
    </div>

    <p v-else>Đang tải sản phẩm...</p>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getAllProducts } from "@/services/product/service.js";
import GameCard from './GameCard.vue'

const products = ref([])

onMounted(async () => {
  products.value = await getAllProducts()
})
</script>

<style scoped>
.games {
  padding: 40px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
</style>

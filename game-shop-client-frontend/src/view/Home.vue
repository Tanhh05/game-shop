<script setup>
import { ref, onMounted } from "vue"
import { getProducts } from "@/services/product.service"
import ProductCard from "@/components/ProductCard.vue"

const products = ref([])

onMounted(async () => {
  const res = await getProducts()
  products.value = res.data.content
})
</script>

<template>

    <section class="section">
      <div class="container">
        <h2 class="title">DANH SÁCH GAME</h2>

        <div class="grid">
          <ProductCard
              v-for="item in products"
              :key="item.id"
              :product="item"
          />
        </div>
      </div>
    </section>

</template>

<style scoped>
.section {
  background: #ffffff;   /* nền trắng */
  padding: 60px 0;
  width: 100%;
}

/* Container */
.container {
  max-width: 1200px;
  width: 92%;
  margin: auto;
}

/* Title */
.title {
  font-size: 24px;
  font-weight: 700;
  color: #111;
  margin-bottom: 35px;
  padding-left: 14px;
  border-left: 4px solid #ff9800;   /* vạch cam */
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* Grid responsive */
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 25px;
}

/* Tablet */
@media (max-width: 992px) {
  .grid {
    gap: 20px;
  }

  .title {
    font-size: 22px;
  }
}

/* Mobile */
@media (max-width: 600px) {
  .section {
    padding: 40px 0;
  }

  .title {
    font-size: 20px;
  }
}
</style>
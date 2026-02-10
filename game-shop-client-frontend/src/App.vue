<template>
  <Header />

  <main class="main-content">
    <router-view />
  </main>

  <Footer />
</template>

<script setup>
import Header from '@/components/layout/Header.vue'
import Footer from '@/components/layout/Footer.vue'
</script>

<style>
.main-content {
  min-height: calc(100vh - 160px); /* tuỳ chiều cao header + footer */
}
</style>

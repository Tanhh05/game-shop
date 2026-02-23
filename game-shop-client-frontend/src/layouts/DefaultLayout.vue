<script setup>
import Header from "@/components/Header.vue"
import Banner from "@/components/Banner.vue"
import Footer from "@/components/Footer.vue"
</script>

<template>
  <Header />
  <Banner />
  <router-view />

  <Footer />
</template>
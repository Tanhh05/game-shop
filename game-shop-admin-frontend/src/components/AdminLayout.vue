<template>
  <div class="layout">
    <Sidebar />

    <div class="main">
      <Topbar />
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import Sidebar from "./Sidebar.vue";
import Topbar from "./Topbar.vue";
</script>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
  background: radial-gradient(circle at top left, #0f172a, #020617);
  color: #fff;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.content {
  padding: 30px;
  overflow-y: auto;
}
</style>
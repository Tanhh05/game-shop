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
  background: #0f0f0f;
  color: white;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.content {
  padding: 25px;
  overflow-y: auto;
}
</style>

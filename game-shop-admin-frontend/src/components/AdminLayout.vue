<template>
  <div class="layout">
    <Sidebar />

    <div class="main">
      <Topbar />
      <div class="content">
        <router-view />
      </div>
    </div>

    <ConfirmDialog />
    <ToastContainer />
  </div>
</template>

<script setup>
import Sidebar from "./Sidebar.vue"
import Topbar from "./Topbar.vue"
import ConfirmDialog from "@/components/ui/ConfirmDialog.vue"
import ToastContainer from "@/components/ui/ToastContainer.vue"
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #fbfaf7, #f1efe7 60%, #f7f4ee);
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.content {
  padding: 26px 30px 40px;
  flex: 1;
}

@media (max-width: 900px) {
  .content {
    padding: 20px;
  }
}
</style>

<template>
  <div class="wrap">
    <div v-for="t in ui.state.toasts" :key="t.id" class="toast" :class="t.type">
      <span>{{ t.message }}</span>
      <button @click="ui.removeToast(t.id)">x</button>
    </div>
  </div>
</template>

<script setup>
import { useUi } from "@/composables/useUi"
const ui = useUi()
</script>

<style scoped>
.wrap {
  position: fixed;
  right: 18px;
  top: 18px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 60;
}

.toast {
  min-width: 260px;
  max-width: 380px;
  color: #17181a;
  border-radius: 12px;
  padding: 12px 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border: 1px solid #e4e0d7;
  background: #ffffff;
  box-shadow: 0 12px 24px rgba(22, 24, 26, 0.12);
}

.toast.info { border-left: 4px solid #1f6feb; }
.toast.success { border-left: 4px solid #16a34a; }
.toast.error { border-left: 4px solid #ef4444; }

button {
  border: 0;
  background: transparent;
  color: #6b6f76;
  font-size: 14px;
  cursor: pointer;
}
</style>

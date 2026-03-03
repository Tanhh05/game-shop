<template>
  <div v-if="ui.state.confirm.open" class="overlay">
    <div class="modal">
      <h3>{{ ui.state.confirm.title }}</h3>
      <p>{{ ui.state.confirm.message }}</p>
      <div class="actions">
        <button class="btn btn-ghost" @click="ui.confirmCancel()">Hủy</button>
        <button class="btn btn-primary" @click="ui.confirmAccept()">Xác nhận</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useUi } from "@/composables/useUi"
const ui = useUi()
</script>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(23, 24, 26, 0.4);
  display: grid;
  place-items: center;
  z-index: 50;
}

.modal {
  width: min(460px, 92vw);
  background: #ffffff;
  color: #17181a;
  border: 1px solid #e4e0d7;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 20px 50px rgba(22, 24, 26, 0.2);
}

h3 {
  margin: 0 0 8px;
  font-size: 18px;
}

p {
  margin: 0 0 18px;
  color: #6b6f76;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>

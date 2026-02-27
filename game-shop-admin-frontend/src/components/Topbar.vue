<template>
  <div class="topbar">

    <input
        type="text"
        placeholder="🔍 Search data..."
    />

    <div class="right">
      🔔
      ⚙
    </div>

  </div>
</template>

<style scoped>
.topbar {
  height: 60px;
  background: #0f172a;
  border-bottom: 1px solid #1e293b;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 25px;
}

input {
  width: 300px;
  padding: 8px 15px;
  border-radius: 20px;
  border: none;
  background: #1e293b;
  color: white;
}

.right {
  display: flex;
  gap: 15px;
  font-size: 18px;
}
</style>
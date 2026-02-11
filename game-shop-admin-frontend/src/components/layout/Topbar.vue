<template>
  <div class="topbar">

    <input
        type="text"
        placeholder="🔎 Tìm kiếm sản phẩm, ID tài khoản..."
    />

    <div class="right">
      🔔
      <div class="lang">VN ▾</div>
    </div>

  </div>
</template>

<style scoped>
.topbar {
  height: 60px;
  background: #1a120b;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #2a1a0f;
}

input {
  width: 350px;
  padding: 8px 15px;
  border-radius: 20px;
  border: none;
  background: #2a1a0f;
  color: white;
}

.right {
  display: flex;
  gap: 15px;
  align-items: center;
}

.lang {
  background: #2a1a0f;
  padding: 5px 10px;
  border-radius: 8px;
}
</style>

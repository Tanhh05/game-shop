<template>
  <div class="sidebar">

    <div class="logo">
      🎮 TTS
    </div>

    <div class="section">
      <div class="title">MENU CHÍNH</div>

      <router-link to="/admin/dashboard" class="item">
        📊 Bảng điều khiển
      </router-link>

      <router-link to="/admin/products" class="item">
        📦 Quản lý sản phẩm
      </router-link>

      <router-link to="/admin/orders" class="item">
        🛒 Đơn hàng
      </router-link>

      <router-link to="/admin/users" class="item">
        👤 Người dùng
      </router-link>
    </div>

    <div class="section bottom">
      <div class="title">HỖ TRỢ & CẤU HÌNH</div>
      <div class="item">⚙ Cài đặt hệ thống</div>
      <div class="item">❓ Hỗ trợ khách hàng</div>
    </div>

  </div>
</template>

<style scoped>
.sidebar {
  width: 250px;
  background: linear-gradient(180deg, #1b120a, #0f0a05);
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #ff8800;
  margin-bottom: 30px;
}

.section {
  margin-bottom: 30px;
}

.title {
  font-size: 12px;
  color: #888;
  margin-bottom: 10px;
}

.item {
  display: block;
  padding: 10px 15px;
  border-radius: 10px;
  color: white;
  text-decoration: none;
  margin-bottom: 8px;
  transition: 0.2s;
}

.item:hover {
  background: #2a1a0f;
}

.router-link-exact-active {
  background: #ff8800;
  color: black;
}

.bottom {
  margin-top: auto;
}
</style>

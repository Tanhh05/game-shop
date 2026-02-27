<template>
  <div class="sidebar">

    <div class="logo">
      🎮 GameShop
    </div>

    <router-link to="/admin/dashboard" class="item">
      📊 Dashboard
    </router-link>

    <router-link to="/admin/products" class="item">
      📦 Products
    </router-link>

    <router-link to="/admin/orders" class="item">
      🛒 Orders
    </router-link>

    <router-link to="/admin/users" class="item">
      👤 Users
    </router-link>

  </div>
</template>

<style scoped>
.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #0f172a, #020617);
  padding: 25px 15px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #1e293b;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 40px;
  color: #3b82f6;
}

.item {
  padding: 12px 15px;
  border-radius: 10px;
  margin-bottom: 10px;
  color: #cbd5e1;
  text-decoration: none;
  transition: 0.3s;
}

.item:hover {
  background: #1e293b;
  color: #fff;
}

.router-link-exact-active {
  background: linear-gradient(90deg, #2563eb, #3b82f6);
  color: white;
}
</style>
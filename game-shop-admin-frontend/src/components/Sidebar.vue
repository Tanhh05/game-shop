<template>
  <aside class="sidebar">
    <div class="brand">
      <div class="brand-mark">GS</div>
      <div>
        <div class="brand-name">GameShop Admin</div>
        <div class="brand-sub">Operations Console</div>
      </div>
    </div>

    <nav class="nav">
      <router-link to="/admin/dashboard" class="nav-item">Dashboard</router-link>
      <router-link to="/admin/games" class="nav-item">Games</router-link>
      <router-link to="/admin/products" class="nav-item">Products</router-link>
      <router-link to="/admin/orders" class="nav-item">Orders</router-link>
      <router-link to="/admin/users" class="nav-item">Users</router-link>
      <router-link to="/admin/inventory" class="nav-item">Inventory</router-link>
    </nav>

    <div class="sidebar-foot">
      <div class="foot-card">
        <div class="foot-title">Today</div>
        <div class="foot-text">Keep stock healthy and orders on track.</div>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 250px;
  padding: 24px 18px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  background: #ffffff;
  border-right: 1px solid #e4e0d7;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-mark {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: linear-gradient(135deg, #0ea5a4, #1f6feb);
  color: #fff;
  font-weight: 700;
  display: grid;
  place-items: center;
  letter-spacing: 0.04em;
}

.brand-name {
  font-weight: 700;
  font-size: 16px;
}

.brand-sub {
  font-size: 12px;
  color: #6b6f76;
}

.nav {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.nav-item {
  padding: 10px 12px;
  border-radius: 12px;
  color: #1f2937;
  text-decoration: none;
  font-weight: 600;
  border: 1px solid transparent;
  transition: background 0.2s ease, border 0.2s ease;
}

.nav-item:hover {
  background: #f6f6f3;
  border-color: #e4e0d7;
}

.router-link-exact-active {
  background: rgba(14, 165, 164, 0.12);
  border-color: rgba(14, 165, 164, 0.3);
  color: #0f766e;
}

.sidebar-foot {
  margin-top: auto;
}

.foot-card {
  background: #f6f6f3;
  border: 1px solid #e4e0d7;
  border-radius: 14px;
  padding: 14px;
}

.foot-title {
  font-weight: 600;
  margin-bottom: 6px;
}

.foot-text {
  font-size: 12px;
  color: #6b6f76;
}

@media (max-width: 900px) {
  .sidebar {
    width: 200px;
  }
}

@media (max-width: 720px) {
  .sidebar {
    position: sticky;
    top: 0;
    height: 100vh;
    width: 180px;
  }
}
</style>

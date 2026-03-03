<template>
  <div class="forbidden">
    <div class="card">
      <div class="badge warning">403</div>
      <h1>Không có quyền truy cập</h1>
      <p>Bạn không có quyền truy cập khu vực admin.</p>
      <router-link to="/login" class="btn btn-primary">Về đăng nhập</router-link>
    </div>
  </div>
</template>

<style scoped>
.forbidden {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: linear-gradient(120deg, #f8f5ef, #f1efe7 50%, #ffffff);
}

.card {
  width: min(420px, 92vw);
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

h1 {
  font-size: 24px;
}

p {
  color: #6b6f76;
}
</style>

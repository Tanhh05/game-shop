<template>
  <section class="forbidden-wrap">
    <div class="card">
      <h1>403</h1>
      <p>Bạn không có quyền truy cập trang này.</p>
      <router-link to="/" class="btn">Về trang chủ</router-link>
    </div>
  </section>
</template>

<style scoped>
.forbidden-wrap {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fb;
}

.card {
  background: #fff;
  border: 1px solid #eceff3;
  border-radius: 12px;
  padding: 28px;
  text-align: center;
  width: min(420px, 92vw);
}

h1 {
  font-size: 48px;
  margin: 0 0 10px;
}

p {
  margin: 0 0 16px;
  color: #4b5563;
}

.btn {
  display: inline-block;
  background: #111827;
  color: #fff;
  text-decoration: none;
  padding: 10px 14px;
  border-radius: 8px;
}
</style>

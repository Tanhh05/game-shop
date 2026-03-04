<template>
  <section class="forbidden">
    <div class="forbidden__card">
      <p class="eyebrow">403</p>
      <h1>Không có quyền truy cập</h1>
      <p class="message">Trang bạn yêu cầu yêu cầu quyền truy cập cao hơn.</p>
      <div class="actions">
        <router-link to="/" class="btn btn--primary">Về trang chủ</router-link>
        <router-link to="/login" class="btn btn--ghost">Đăng nhập</router-link>
      </div>
    </div>
  </section>
</template>

<style scoped>

.forbidden {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at top, #fff4e6 0%, #f8f5f0 50%, #edf2f7 100%);
  padding: 60px 0;
  font-family: "Manrope", "Segoe UI", sans-serif;
}

.forbidden__card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  padding: 32px;
  text-align: center;
  width: min(460px, 92vw);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.12);
}

.eyebrow {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.28em;
  color: #94a3b8;
  margin: 0 0 12px;
}

h1 {
  font-family: "Space Grotesk", "Manrope", sans-serif;
  font-size: clamp(24px, 3vw, 30px);
  margin: 0 0 12px;
}

.message {
  margin: 0 0 20px;
  color: #64748b;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.btn {
  text-decoration: none;
  padding: 10px 18px;
  border-radius: 999px;
  font-weight: 600;
  font-size: 14px;
}

.btn--primary {
  background: #f97316;
  color: #ffffff;
}

.btn--ghost {
  border: 1px solid #e2e8f0;
  color: #0f172a;
}
</style>

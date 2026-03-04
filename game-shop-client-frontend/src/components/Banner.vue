<template>
  <section class="hero">
    <div class="hero__overlay"></div>
    <div class="hero__content">
      <p class="eyebrow">TTS TOOL VIP</p>
      <h1>Mua key game nhanh, giao dịch an toàn</h1>
      <p class="subtitle">
        Danh mục sản phẩm cập nhật mỗi ngày. Thanh toán tự động, nhận key ngay sau khi hoàn tất.
      </p>
      <div class="hero__actions">
        <router-link to="/register" class="btn btn--primary">Bắt đầu ngay</router-link>
        <router-link to="/" class="btn btn--ghost">Khám phá game</router-link>
      </div>
    </div>
  </section>
</template>

<style scoped>

.hero {
  position: relative;
  min-height: 320px;
  background: url("https://pub-83cf7eb09c5947e0a56e93794db182d0.r2.dev/64810662-bc6f-460b-9e1a-c8a2f568707c-banner.jpg")
    center/cover no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #f8fafc;
  font-family: "Manrope", "Segoe UI", sans-serif;
  overflow: hidden;
}

.hero__overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, rgba(15, 23, 42, 0.85), rgba(30, 41, 59, 0.6));
}

.hero__content {
  position: relative;
  max-width: 720px;
  text-align: center;
  z-index: 1;
}

.eyebrow {
  letter-spacing: 0.3em;
  text-transform: uppercase;
  font-size: 12px;
  color: rgba(248, 250, 252, 0.8);
  margin: 0 0 12px;
}

h1 {
  font-family: "Space Grotesk", "Manrope", sans-serif;
  font-size: clamp(28px, 4vw, 40px);
  margin: 0 0 16px;
}

.subtitle {
  margin: 0 0 24px;
  font-size: 15px;
  color: rgba(248, 250, 252, 0.8);
  line-height: 1.6;
}

.hero__actions {
  display: flex;
  justify-content: center;
  gap: 14px;
  flex-wrap: wrap;
}

.btn {
  text-decoration: none;
  padding: 10px 18px;
  border-radius: 999px;
  font-weight: 600;
  font-size: 14px;
  transition: transform 0.2s ease;
}

.btn--primary {
  background: #f97316;
  color: #ffffff;
}

.btn--ghost {
  border: 1px solid rgba(248, 250, 252, 0.5);
  color: #ffffff;
}

.btn:hover {
  transform: translateY(-1px);
}
</style>

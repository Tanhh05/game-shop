<template>
  <footer class="footer">
    <div class="container">
      <div class="footer__grid">
        <div>
          <h2>TTS TOOL VIP</h2>
          <p>
            Nền tảng giao dịch key game và tài khoản uy tín, cập nhật sản phẩm liên tục, thanh toán nhanh.
          </p>
        </div>

        <div>
          <h3>Điều khoản</h3>
          <ul>
            <li>Giao dịch tự động, nhận key ngay</li>
            <li>Không chia sẻ thông tin thanh toán</li>
            <li>Hỗ trợ khách hàng trong giờ hành chính</li>
          </ul>
        </div>

        <div>
          <h3>Liên hệ</h3>
          <ul>
            <li>Hỗ trợ: inbox fanpage</li>
            <li>Thời gian: 9:00 - 18:00</li>
            <li>Ưu tiên xử lý đơn tự động</li>
          </ul>
        </div>
      </div>

      <div class="footer__meta">
        <span>TTS TOOL VIP | key game | nạp tiền tự động | giao dịch an toàn</span>
        <span>©2026 TTS TOOL VIP. All rights reserved.</span>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background: #0f172a;
  color: #cbd5f5;
  padding: 60px 0 32px;
  font-family: "Manrope", "Segoe UI", sans-serif;
}

.container {
  max-width: 1200px;
  width: 92%;
  margin: 0 auto;
}

.footer__grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 28px;
}

h2 {
  font-family: "Space Grotesk", "Manrope", sans-serif;
  font-size: 22px;
  color: #ffffff;
  margin: 0 0 12px;
}

h3 {
  margin: 0 0 12px;
  font-size: 16px;
  color: #f97316;
}

p {
  font-size: 14px;
  line-height: 1.6;
  color: #cbd5f5;
}

ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 8px;
  font-size: 14px;
}

.footer__meta {
  margin-top: 32px;
  padding-top: 20px;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 13px;
  color: #94a3b8;
}
</style>

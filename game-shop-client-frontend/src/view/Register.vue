<script setup>
import { ref } from "vue"
import { useRouter } from "vue-router"
import { register } from "@/services/auth.service"
import { parseApiError } from "@/utils/api-error"

const router = useRouter()

const form = ref({
  username: "",
  email: "",
  password: ""
})

const loading = ref(false)
const error = ref("")
const success = ref("")

const handleRegister = async () => {
  error.value = ""
  success.value = ""

  if (!form.value.username || !form.value.email || !form.value.password) {
    error.value = "Vui lòng nhập đầy đủ thông tin"
    return
  }

  try {
    loading.value = true
    await register(form.value)
    success.value = "Đăng ký thành công. Vui lòng đăng nhập."
    setTimeout(() => router.push("/login"), 800)
  } catch (err) {
    error.value = parseApiError(err, "Đăng ký thất bại")
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="register">
    <div class="register__glow"></div>
    <div class="register__container">
      <div class="register__panel">
        <div class="register__intro">
          <p class="eyebrow">Join us</p>
          <h2 class="title">Tạo tài khoản mới</h2>
          <p class="subtitle">
            Đăng ký để nhận ưu đãi, quản lý đơn hàng và nạp tiền nhanh hơn.
          </p>
          <div class="feature-list">
            <div class="feature-item">Lưu lịch sử giao dịch</div>
            <div class="feature-item">Nhận key nhanh sau thanh toán</div>
            <div class="feature-item">Nạp tiền tự động</div>
          </div>
        </div>

        <div class="register__card">
          <div class="brand">TTS TOOL VIP</div>

          <label class="field">
            <span>Tên đăng nhập</span>
            <input v-model="form.username" placeholder="Nhập username" autocomplete="username" />
          </label>

          <label class="field">
            <span>Email</span>
            <input v-model="form.email" placeholder="you@example.com" autocomplete="email" />
          </label>

          <label class="field">
            <span>Mật khẩu</span>
            <input
              v-model="form.password"
              type="password"
              placeholder="Tối thiểu 6 ký tự"
              autocomplete="new-password"
            />
          </label>

          <button class="primary-btn" @click="handleRegister" :disabled="loading">
            {{ loading ? "Đang xử lý..." : "Đăng ký" }}
          </button>

          <p v-if="error" class="error">{{ error }}</p>
          <p v-if="success" class="ok">{{ success }}</p>

          <div class="register__hint">
            Đã có tài khoản?
            <router-link to="/login">Đăng nhập</router-link>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.register {
  --register-dark: #0f172a;
  --register-muted: #64748b;
  --register-border: #e2e8f0;
  min-height: 100vh;
  background: radial-gradient(circle at top right, #fff4e6 0%, #f8f5f0 42%, #edf2f7 100%);
  display: flex;
  align-items: center;
  padding: 80px 0;
  font-family: "Manrope", "Segoe UI", sans-serif;
  color: var(--register-dark);
  position: relative;
  overflow: hidden;
}

.register__glow {
  position: absolute;
  bottom: -200px;
  right: -120px;
  width: 360px;
  height: 360px;
  background: radial-gradient(circle, rgba(249, 115, 22, 0.35), transparent 70%);
  filter: blur(8px);
  pointer-events: none;
}

.register__container {
  width: 92%;
  max-width: 1100px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.register__panel {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(320px, 0.9fr);
  gap: 40px;
  align-items: center;
}

.register__intro {
  padding-right: 12px;
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 0.28em;
  text-transform: uppercase;
  color: var(--register-muted);
  margin: 0 0 12px;
}

.title {
  font-family: "Space Grotesk", "Manrope", sans-serif;
  font-size: clamp(26px, 3vw, 38px);
  margin: 0 0 12px;
}

.subtitle {
  color: var(--register-muted);
  font-size: 15px;
  line-height: 1.6;
  max-width: 520px;
  margin: 0 0 18px;
}

.feature-list {
  display: grid;
  gap: 10px;
  color: #0f172a;
  font-weight: 600;
  font-size: 14px;
}

.feature-item {
  background: rgba(249, 115, 22, 0.1);
  border: 1px solid rgba(249, 115, 22, 0.25);
  padding: 10px 12px;
  border-radius: 12px;
}

.register__card {
  background: #ffffff;
  border-radius: 20px;
  padding: 32px;
  border: 1px solid var(--register-border);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.12);
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.brand {
  font-family: "Space Grotesk", "Manrope", sans-serif;
  font-weight: 700;
  font-size: 20px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 13px;
  color: var(--register-muted);
}

.field input {
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid var(--register-border);
  font-size: 14px;
  background: #f8fafc;
  color: var(--register-dark);
  outline: none;
}

.field input:focus {
  border-color: rgba(249, 115, 22, 0.6);
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.15);
}

.primary-btn {
  padding: 12px 16px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #fb923c, #f97316);
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.primary-btn:not(:disabled):hover {
  transform: translateY(-1px);
}

.error {
  color: #dc2626;
  font-size: 13px;
  white-space: pre-line;
}

.ok {
  color: #16a34a;
  font-size: 13px;
}

.register__hint {
  font-size: 13px;
  color: var(--register-muted);
}

.register__hint a {
  color: #f97316;
  text-decoration: none;
  font-weight: 600;
  margin-left: 6px;
}

@media (max-width: 900px) {
  .register__panel {
    grid-template-columns: 1fr;
  }
}
</style>

<script setup>
import { ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { login } from "@/services/auth.service"
import { parseApiError } from "@/utils/api-error"
import { useAuthStore } from "@/stores/auth"

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const form = ref({
  username: "",
  password: ""
})

const loading = ref(false)
const error = ref("")

const handleLogin = async () => {
  error.value = ""

  if (!form.value.username || !form.value.password) {
    error.value = "Vui lòng nhập đầy đủ thông tin"
    return
  }

  try {
    loading.value = true
    const res = await login(form.value)

    auth.setSession(res.data)

    await router.push(route.query.redirect || "/")
  } catch (err) {
    error.value = parseApiError(err, "Sai tài khoản hoặc mật khẩu")
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="login">
    <div class="login__glow"></div>
    <div class="login__container">
      <div class="login__panel">
        <div class="login__intro">
          <p class="eyebrow">Welcome back</p>
          <h2 class="title">Đăng nhập để tiếp tục</h2>
          <p class="subtitle">
            Truy cập kho game, đơn hàng và lịch sử giao dịch của bạn. Đăng nhập nhanh để trải nghiệm liền mạch.
          </p>
        </div>

        <div class="login__card">
          <div class="brand">TTS TOOL VIP</div>
          <label class="field">
            <span>Tên đăng nhập</span>
            <input v-model="form.username" placeholder="Nhập username" autocomplete="username" />
          </label>

          <label class="field">
            <span>Mật khẩu</span>
            <input
              v-model="form.password"
              type="password"
              placeholder="Nhập mật khẩu"
              autocomplete="current-password"
            />
          </label>

          <button class="primary-btn" @click="handleLogin" :disabled="loading">
            {{ loading ? "Đang đăng nhập..." : "Đăng nhập" }}
          </button>

          <p v-if="error" class="error">{{ error }}</p>

          <div class="login__hint">
            Chưa có tài khoản?
            <router-link to="/register">Đăng ký ngay</router-link>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.login {
  --login-bg: #0f172a;
  --login-panel: #ffffff;
  --login-dark: #0f172a;
  --login-muted: #64748b;
  --login-accent: #fb923c;
  --login-border: #e2e8f0;
  min-height: 100vh;
  background: radial-gradient(circle at top, #fff4e6 0%, #f8f5f0 42%, #edf2f7 100%);
  display: flex;
  align-items: center;
  padding: 80px 0;
  font-family: "Manrope", "Segoe UI", sans-serif;
  color: var(--login-dark);
  position: relative;
  overflow: hidden;
}

.login__glow {
  position: absolute;
  top: -200px;
  left: -120px;
  width: 360px;
  height: 360px;
  background: radial-gradient(circle, rgba(251, 146, 60, 0.35), transparent 70%);
  filter: blur(8px);
  pointer-events: none;
}

.login__container {
  width: 92%;
  max-width: 1100px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.login__panel {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
  gap: 40px;
  align-items: center;
}

.login__intro {
  padding-right: 12px;
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 0.28em;
  text-transform: uppercase;
  color: var(--login-muted);
  margin: 0 0 12px;
}

.title {
  font-family: "Space Grotesk", "Manrope", sans-serif;
  font-size: clamp(26px, 3vw, 38px);
  margin: 0 0 12px;
}

.subtitle {
  color: var(--login-muted);
  font-size: 15px;
  line-height: 1.6;
  max-width: 520px;
  margin: 0;
}

.login__card {
  background: var(--login-panel);
  border-radius: 20px;
  padding: 32px;
  border: 1px solid var(--login-border);
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
  color: var(--login-muted);
}

.field input {
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid var(--login-border);
  font-size: 14px;
  background: #f8fafc;
  color: var(--login-dark);
  outline: none;
}

.field input:focus {
  border-color: rgba(251, 146, 60, 0.6);
  box-shadow: 0 0 0 3px rgba(251, 146, 60, 0.15);
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
}

.login__hint {
  font-size: 13px;
  color: var(--login-muted);
}

.login__hint a {
  color: #f97316;
  text-decoration: none;
  font-weight: 600;
  margin-left: 6px;
}

@media (max-width: 900px) {
  .login__panel {
    grid-template-columns: 1fr;
  }
}
</style>

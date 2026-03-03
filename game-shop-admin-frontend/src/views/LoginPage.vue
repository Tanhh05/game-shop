<template>
  <div class="login">
    <div class="intro">
      <div class="intro-card">
        <div class="intro-badge">Admin Console</div>
        <h1>Manage every game, order, and inventory pulse.</h1>
        <p>
          Sign in to keep GameShop running smoothly. Track orders, unlock inventory,
          and keep products synced with real-time demand.
        </p>
        <div class="intro-stats">
          <div>
            <div class="stat-value">24/7</div>
            <div class="stat-label">Operations</div>
          </div>
          <div>
            <div class="stat-value">100%</div>
            <div class="stat-label">Control</div>
          </div>
        </div>
      </div>
    </div>

    <div class="panel">
      <div class="card">
        <h2>Đăng nhập quản trị</h2>
        <p class="helper">Chỉ tài khoản ADMIN mới truy cập được.</p>

        <input v-model="form.username" class="input" placeholder="Username" />
        <input v-model="form.password" class="input" type="password" placeholder="Password" />

        <button class="btn btn-primary" :disabled="loading" @click="handleLogin">
          {{ loading ? "Đang đăng nhập..." : "Đăng nhập" }}
        </button>

        <p v-if="error" class="error">{{ error }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { login } from "@/services/auth.service"
import { parseApiError } from "@/utils/api-error"
import { setAuth } from "@/utils/auth"

const router = useRouter()
const route = useRoute()

const form = ref({ username: "", password: "" })
const loading = ref(false)
const error = ref("")

const handleLogin = async () => {
  error.value = ""
  if (!form.value.username || !form.value.password) {
    error.value = "Vui lòng nhập username và password"
    return
  }

  try {
    loading.value = true
    const res = await login(form.value)
    setAuth(res.data)

    if (res.data?.role !== "ADMIN") {
      error.value = "Tài khoản không có quyền ADMIN"
      return
    }

    await router.push(route.query.redirect || "/admin/dashboard")
  } catch (e) {
    error.value = parseApiError(e, "Đăng nhập thất bại")
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  background: linear-gradient(120deg, #f8f5ef, #f1efe7 50%, #ffffff);
}

.intro {
  display: grid;
  place-items: center;
  padding: 40px;
}

.intro-card {
  max-width: 420px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.intro-badge {
  display: inline-flex;
  width: fit-content;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(14, 165, 164, 0.12);
  color: #0f766e;
  font-weight: 600;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

h1 {
  font-size: 36px;
  line-height: 1.1;
}

p {
  color: #6b6f76;
}

.intro-stats {
  display: flex;
  gap: 24px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #0ea5a4;
}

.stat-label {
  font-size: 12px;
  color: #6b6f76;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.panel {
  display: grid;
  place-items: center;
  padding: 40px 24px;
}

.card {
  width: min(380px, 92vw);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

h2 {
  font-size: 22px;
}

@media (max-width: 900px) {
  .login {
    grid-template-columns: 1fr;
  }

  .intro {
    display: none;
  }
}
</style>

<script setup>
import { ref } from "vue"
import { useRouter } from "vue-router"
import { login } from "@/services/auth.service"

const router = useRouter()

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

    // Lưu thông tin vào localStorage
    localStorage.setItem("token", res.data.token)
    localStorage.setItem("userId", res.data.userId)
    localStorage.setItem("username", res.data.username)
    localStorage.setItem("role", res.data.role)
    localStorage.setItem("balance", res.data.balance)

    await router.push("/")
  } catch (err) {
    error.value = "Sai tài khoản hoặc mật khẩu"
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-wrapper">
    <div class="login-box">
      <h2>ĐĂNG NHẬP</h2>

      <input
          v-model="form.username"
          placeholder="Username"
      />

      <input
          v-model="form.password"
          type="password"
          placeholder="Password"
      />

      <button @click="handleLogin" :disabled="loading">
        {{ loading ? "Đang đăng nhập..." : "Đăng nhập" }}
      </button>

      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </div>
</template>

<style scoped>
.login-wrapper {
  height: 100vh;
  background: #111;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  background: #1c1c1c;
  padding: 40px;
  width: 350px;
  border-radius: 10px;
  text-align: center;
  border: 1px solid #222;
}

.login-box h2 {
  color: gold;
  margin-bottom: 25px;
}

.login-box input {
  width: 100%;
  padding: 10px;
  margin-bottom: 15px;
  border-radius: 5px;
  border: none;
  background: #2c2c2c;
  color: white;
}

.login-box button {
  width: 100%;
  padding: 10px;
  background: orange;
  border: none;
  color: white;
  font-weight: bold;
  border-radius: 5px;
  cursor: pointer;
  transition: 0.3s;
}

.login-box button:hover {
  background: #ff9800;
}

.error {
  color: red;
  margin-top: 10px;
}
</style>
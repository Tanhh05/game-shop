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
  <div class="login-wrapper">
    <div class="login-box">
      <h2>ĐĂNG KÝ</h2>

      <input v-model="form.username" placeholder="Username" />
      <input v-model="form.email" placeholder="Email" />
      <input v-model="form.password" type="password" placeholder="Password" />

      <button @click="handleRegister" :disabled="loading">
        {{ loading ? "Đang xử lý..." : "Đăng ký" }}
      </button>

      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="success" class="ok">{{ success }}</p>
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
}

.error {
  color: #ff6b6b;
  margin-top: 10px;
  white-space: pre-line;
}

.ok {
  color: #6bff95;
  margin-top: 10px;
}
</style>

<template>
  <div class="auth">
    <h2>Đăng nhập</h2>

    <input v-model="form.username" placeholder="Username hoặc Email" />
    <input v-model="form.password" type="password" placeholder="Mật khẩu" />

    <button @click="handleLogin">Đăng nhập</button>

    <p class="error" v-if="error">{{ error }}</p>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/services/authService'

const router = useRouter()
const error = ref('')

const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  error.value = ''
  try {
    const res = await login(form)

    // lưu token + user
    localStorage.setItem('token', res.token)
    localStorage.setItem('user', JSON.stringify(res))

    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.message || 'Đăng nhập thất bại'
  }
}
</script>

<style scoped>
.auth {
  max-width: 360px;
  margin: 80px auto;
  padding: 20px;
  background: #fff;
}
input {
  width: 100%;
  margin-bottom: 12px;
  padding: 10px;
}
button {
  width: 100%;
  padding: 10px;
  background: #f39c12;
  border: none;
  color: #fff;
}
.error {
  color: red;
  margin-top: 10px;
}
</style>

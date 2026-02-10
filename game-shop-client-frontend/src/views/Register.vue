<template>
  <div class="auth">
    <h2>Đăng ký</h2>

    <input v-model="form.username" placeholder="Username" />
    <input v-model="form.email" placeholder="Email" />
    <input v-model="form.password" type="password" placeholder="Mật khẩu" />

    <button @click="handleRegister">Đăng ký</button>

    <p class="error" v-if="error">{{ error }}</p>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/services/authService'

const router = useRouter()
const error = ref('')

const form = reactive({
  username: '',
  email: '',
  password: ''
})

const handleRegister = async () => {
  error.value = ''
  try {
    const res = await register(form)

    // auto login sau khi đăng ký
    localStorage.setItem('token', res.token)
    localStorage.setItem('user', JSON.stringify(res))

    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.message || 'Đăng ký thất bại'
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
  background: #2ecc71;
  border: none;
  color: #fff;
}
.error {
  color: red;
  margin-top: 10px;
}
</style>

<template>
  <header class="header">
    <!-- LOGO -->
    <div class="logo" @click="goHome">
      ttstoolvip
    </div>

    <!-- MENU -->
    <nav>
      <a href="#">RESELLER</a>
      <a href="#">NẠP TIỀN WEB</a>

      <!-- CHƯA ĐĂNG NHẬP -->
      <template v-if="!user">
        <router-link to="/login">ĐĂNG NHẬP</router-link>
        <router-link to="/register">ĐĂNG KÝ</router-link>
      </template>

      <!-- ĐÃ ĐĂNG NHẬP -->
      <template v-else>
        <span class="balance">
          💰 {{ formatMoney(user.balance) }}
        </span>

        <span class="welcome">
          Xin chào, <b>{{ user.username }}</b>
        </span>

        <a href="#" @click.prevent="logout">ĐĂNG XUẤT</a>
      </template>
    </nav>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = ref(null)

const goHome = () => {
  router.push('/')
}

onMounted(() => {
  const u = localStorage.getItem('user')
  if (u) {
    user.value = JSON.parse(u)
  }
})

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  user.value = null
  router.push('/login')
}

const formatMoney = (money) => {
  if (money == null) return '0 ₫'
  return Number(money).toLocaleString('vi-VN') + ' ₫'
}
</script>

<style scoped>
.header {
  background: #000;
  color: #fff;
  padding: 14px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
}

nav {
  display: flex;
  align-items: center;
}

nav a {
  margin-left: 20px;
  color: #fff;
  text-decoration: none;
  font-weight: 500;
}

nav a:hover {
  color: #f39c12;
}

.welcome {
  margin-left: 20px;
  color: #f39c12;
}

.balance {
  margin-left: 20px;
  color: #2ecc71;
  font-weight: bold;
}
</style>

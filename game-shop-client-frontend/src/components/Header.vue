<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()

const isOpen = ref(false)
const isLogin = ref(false)
const username = ref("")
const balance = ref(0)

const toggleMenu = () => {
  isOpen.value = !isOpen.value
}

// Load user info
const loadUser = () => {
  const token = localStorage.getItem("token")

  if (token) {
    isLogin.value = true
    username.value = localStorage.getItem("username") || ""
    balance.value = Number(localStorage.getItem("balance")) || 0
  } else {
    isLogin.value = false
  }
}

onMounted(() => {
  loadUser()

  // Lắng nghe khi balance thay đổi
  window.addEventListener("balance-updated", loadUser)
})

onBeforeUnmount(() => {
  window.removeEventListener("balance-updated", loadUser)
})

const logout = () => {
  localStorage.clear()
  loadUser()
  router.push("/login")
}
</script>

<template>
  <header class="header">
    <div class="container">

      <!-- Logo -->
      <div class="logo" @click="$router.push('/')">
        ttstoolvip
      </div>

      <!-- Desktop Menu -->
      <nav class="menu desktop">
        <a class="reseller">RESELLER</a>
        <a class="deposit">NẠP TIỀN WEB</a>

        <template v-if="isLogin">
          <router-link to="/orders">LỊCH SỬ</router-link>

          <span class="welcome">
             {{ username }}
          </span>

          <span class="wallet">
            💰 {{ balance.toLocaleString() }}đ
          </span>

          <a href="#" @click.prevent="logout">ĐĂNG XUẤT</a>
        </template>

        <template v-else>
          <router-link to="/login">ĐĂNG NHẬP</router-link>
          <router-link to="/register">ĐĂNG KÝ</router-link>
        </template>
      </nav>

      <!-- Hamburger -->
      <div class="hamburger" @click="toggleMenu">
        ☰
      </div>
    </div>

    <!-- Mobile Menu -->
    <div class="mobile-menu" v-if="isOpen">

      <template v-if="isLogin">
        <div class="mobile-user">
          <div>👤 {{ username }}</div>
          <div class="wallet">
            💰 {{ balance.toLocaleString() }}đ
          </div>
        </div>

        <router-link to="/orders">LỊCH SỬ GIAO DỊCH</router-link>
        <a href="#" @click.prevent="logout">ĐĂNG XUẤT</a>
      </template>

      <template v-else>
        <router-link to="/login">ĐĂNG NHẬP</router-link>
        <router-link to="/register">ĐĂNG KÝ</router-link>
      </template>

      <a class="reseller">RESELLER</a>
      <a class="deposit">NẠP TIỀN WEB</a>
    </div>
  </header>
</template>

<style scoped>
.header {
  background: #000;
  border-bottom: 1px solid #111;
}

.container {
  max-width: 1200px;
  width: 92%;
  margin: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

/* Logo */
.logo {
  font-size: 22px;
  font-weight: 700;
  color: #ffd700;
  cursor: pointer;
}

/* Desktop menu */
.menu {
  display: flex;
  gap: 24px;
  align-items: center;
}

.menu a,
.menu span {
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  color: #ccc;
  transition: 0.3s;
}

.menu a:hover {
  color: #ffd700;
}

.reseller {
  color: #00e5ff;
}

.deposit {
  color: #ffeb3b;
}

.welcome {
  color: #ffd700;
}

.wallet {
  color: #00ff99;
  font-weight: 700;
}

/* Hamburger */
.hamburger {
  display: none;
  font-size: 22px;
  color: white;
  cursor: pointer;
  border: 1px solid #444;
  padding: 5px 10px;
  border-radius: 6px;
}

/* Mobile */
.mobile-menu {
  display: none;
  flex-direction: column;
  background: #000;
  padding: 20px;
  gap: 18px;
}

.mobile-menu a {
  color: white;
  text-decoration: none;
  font-weight: 600;
}

.mobile-user {
  border-bottom: 1px solid #222;
  padding-bottom: 12px;
}

@media (max-width: 768px) {
  .desktop {
    display: none;
  }

  .hamburger {
    display: block;
  }

  .mobile-menu {
    display: flex;
  }
}
</style>
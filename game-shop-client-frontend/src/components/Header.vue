<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { storeToRefs } from "pinia"
import { useAuthStore } from "@/stores/auth"

const router = useRouter()
const auth = useAuthStore()
const { username, balance, isAuthenticated, role } = storeToRefs(auth)

const isOpen = ref(false)

const toggleMenu = () => {
  isOpen.value = !isOpen.value
}

const loadUser = async () => {
  auth.initFromStorage()
  if (!isAuthenticated.value) return

  try {
    await auth.refreshBalance()
  } catch (err) {
    console.error("Không load được số dư:", err)
  }
}

onMounted(loadUser)

const logout = async () => {
  await auth.logout()
  await router.push("/login")
}
</script>

<template>
  <header class="header">
    <div class="container">
      <div class="logo" @click="$router.push('/')">
        ttstoolvip
      </div>

      <nav class="menu desktop">
        <span class="reseller">RESELLER</span>

        <template v-if="isAuthenticated">
          <router-link to="/topup" class="deposit">NẠP TIỀN WEB</router-link>
          <router-link to="/orders">LỊCH SỬ</router-link>

          <span class="welcome">
            {{ role === "ADMIN" ? "ADMIN" : "WELCOME" }}: {{ username || "" }}
          </span>

          <span class="wallet">
            SỐ DƯ: {{ Number(balance || 0).toLocaleString() }}đ
          </span>

          <a href="#" @click.prevent="logout">ĐĂNG XUẤT</a>
        </template>

        <template v-else>
          <router-link to="/login">ĐĂNG NHẬP</router-link>
          <router-link to="/register">ĐĂNG KÝ</router-link>
        </template>
      </nav>

      <div class="hamburger" @click="toggleMenu">☰</div>
    </div>

    <div class="mobile-menu" v-if="isOpen">
      <template v-if="isAuthenticated">
        <div class="mobile-user">
          <div>{{ role === "ADMIN" ? "ADMIN" : "USER" }}: {{ username || "" }}</div>
          <div class="wallet">💰 {{ Number(balance || 0).toLocaleString() }}đ</div>
        </div>

        <router-link to="/topup">NẠP TIỀN WEB</router-link>
        <router-link to="/orders">LỊCH SỬ GIAO DỊCH</router-link>
        <a href="#" @click.prevent="logout">ĐĂNG XUẤT</a>
      </template>

      <template v-else>
        <router-link to="/login">ĐĂNG NHẬP</router-link>
        <router-link to="/register">ĐĂNG KÝ</router-link>
      </template>

      <span class="reseller">RESELLER</span>
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

.logo {
  font-size: 22px;
  font-weight: 700;
  color: #ffd700;
  cursor: pointer;
}

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

.hamburger {
  display: none;
  font-size: 22px;
  color: white;
  cursor: pointer;
  border: 1px solid #444;
  padding: 5px 10px;
  border-radius: 6px;
}

.mobile-menu {
  display: none;
  flex-direction: column;
  background: #000;
  padding: 20px;
  gap: 18px;
}

.mobile-menu a,
.mobile-menu span {
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

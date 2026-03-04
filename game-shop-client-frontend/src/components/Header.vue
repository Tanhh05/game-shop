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
        TTS TOOL VIP
      </div>

      <nav class="menu desktop">
        <template v-if="isAuthenticated">
          <router-link to="/topup" class="menu__link menu__link--accent">Nạp tiền</router-link>
          <router-link to="/orders" class="menu__link">Lịch sử</router-link>

          <span class="menu__pill">
            {{ role === "ADMIN" ? "Admin" : "User" }}: {{ username || "" }}
          </span>

          <span class="menu__pill menu__pill--balance">
            Số dư: {{ Number(balance || 0).toLocaleString() }}đ
          </span>

          <a href="#" class="menu__link" @click.prevent="logout">Đăng xuất</a>
        </template>

        <template v-else>
          <router-link to="/login" class="menu__link">Đăng nhập</router-link>
          <router-link to="/register" class="menu__link menu__link--accent">Đăng ký</router-link>
        </template>
      </nav>

      <button class="hamburger" @click="toggleMenu" type="button">
        Menu
      </button>
    </div>

    <div class="mobile-menu" v-if="isOpen">
      <template v-if="isAuthenticated">
        <div class="mobile-user">
          <div>{{ role === "ADMIN" ? "Admin" : "User" }}: {{ username || "" }}</div>
          <div class="menu__pill menu__pill--balance">
            Số dư: {{ Number(balance || 0).toLocaleString() }}đ
          </div>
        </div>

        <router-link to="/topup" class="mobile-link">Nạp tiền</router-link>
        <router-link to="/orders" class="mobile-link">Lịch sử giao dịch</router-link>
        <a href="#" class="mobile-link" @click.prevent="logout">Đăng xuất</a>
      </template>

      <template v-else>
        <router-link to="/login" class="mobile-link">Đăng nhập</router-link>
        <router-link to="/register" class="mobile-link">Đăng ký</router-link>
      </template>
    </div>
  </header>
</template>

<style scoped>

.header {
  position: sticky;
  top: 0;
  z-index: 10;
  background: rgba(255, 255, 255, 0.9);
  border-bottom: 1px solid #e2e8f0;
  backdrop-filter: blur(8px);
  font-family: "Manrope", "Segoe UI", sans-serif;
}

.container {
  max-width: 1200px;
  width: 92%;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

.logo {
  font-family: "Space Grotesk", "Manrope", sans-serif;
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
  cursor: pointer;
}

.menu {
  display: flex;
  gap: 20px;
  align-items: center;
}

.menu__link {
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  color: #334155;
  transition: color 0.2s ease;
}

.menu__link:hover {
  color: #f97316;
}

.menu__link--accent {
  color: #f97316;
}

.menu__pill {
  background: #f1f5f9;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  color: #0f172a;
}

.menu__pill--balance {
  background: rgba(249, 115, 22, 0.12);
  color: #c2410c;
}

.hamburger {
  display: none;
  font-size: 12px;
  font-weight: 700;
  color: #0f172a;
  background: transparent;
  border: 1px solid #e2e8f0;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  text-transform: uppercase;
  letter-spacing: 0.2em;
}

.mobile-menu {
  display: none;
  flex-direction: column;
  background: #ffffff;
  border-top: 1px solid #e2e8f0;
  padding: 16px 24px;
  gap: 12px;
}

.mobile-link {
  color: #0f172a;
  text-decoration: none;
  font-weight: 600;
}

.mobile-user {
  display: grid;
  gap: 8px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
}

@media (max-width: 820px) {
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

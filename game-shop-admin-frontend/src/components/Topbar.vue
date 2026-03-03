<template>
  <div class="topbar">
    <div class="search">
      <span class="search-label">Search</span>
      <input type="text" placeholder="Search by order, user, product..." />
    </div>

    <div class="right">
      <div class="chip">
        <span class="dot"></span>
        <span class="user">{{ username || "ADMIN" }}</span>
      </div>
      <button class="btn btn-outline" @click="handleLogout">Đăng xuất</button>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue"
import { useRouter } from "vue-router"
import { logout } from "@/services/auth.service"
import { clearAuth, getUsername } from "@/utils/auth"

const router = useRouter()
const username = computed(() => getUsername())

const handleLogout = async () => {
  try {
    await logout()
  } catch (_) {
    // ignore network error
  }

  clearAuth()
  await router.push("/login")
}
</script>

<style scoped>
.topbar {
  height: 70px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  border-bottom: 1px solid #e4e0d7;
  position: sticky;
  top: 0;
  z-index: 10;
}

.search {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.search-label {
  font-size: 11px;
  color: #6b6f76;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

input {
  width: 320px;
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid #e4e0d7;
  background: #f6f6f3;
  color: #1f2937;
}

.right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 999px;
  background: #f6f6f3;
  border: 1px solid #e4e0d7;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #16a34a;
}

.user {
  font-weight: 600;
  color: #1f2937;
}

@media (max-width: 900px) {
  input {
    width: 200px;
  }
}

@media (max-width: 700px) {
  .topbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    height: auto;
    padding: 16px;
  }

  input {
    width: 100%;
  }

  .right {
    width: 100%;
    justify-content: space-between;
  }
}
</style>

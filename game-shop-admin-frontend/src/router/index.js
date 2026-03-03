import { createRouter, createWebHistory } from "vue-router"
import AdminLayout from "@/components/AdminLayout.vue"
import ProductPage from "@/views/ProductPage.vue"
import AddProductPage from "@/views/AddProductPage.vue"
import DashboardPage from "@/views/DashboardPage.vue"
import LoginPage from "@/views/LoginPage.vue"
import ForbiddenPage from "@/views/ForbiddenPage.vue"
import UsersPage from "@/views/UsersPage.vue"
import OrdersPage from "@/views/OrdersPage.vue"
import InventoryPage from "@/views/InventoryPage.vue"
import GamesPage from "@/views/GamesPage.vue"
import { isAdmin, isAuthenticated } from "@/utils/auth"

const routes = [
  { path: "/", redirect: "/admin/dashboard" },

  { path: "/login", name: "login", component: LoginPage, meta: { guestOnly: true } },
  { path: "/forbidden", name: "forbidden", component: ForbiddenPage },

  {
    path: "/admin",
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: "", redirect: "dashboard" },
      { path: "dashboard", component: DashboardPage },
      { path: "games", component: GamesPage },
      { path: "products", component: ProductPage },
      { path: "products/add", component: AddProductPage },
      { path: "orders", component: OrdersPage },
      { path: "users", component: UsersPage },
      { path: "inventory", component: InventoryPage },
      { path: ":pathMatch(.*)*", redirect: "dashboard" }
    ]
  },

  { path: "/:pathMatch(.*)*", redirect: "/admin/dashboard" }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.guestOnly && isAuthenticated()) {
    next("/admin/dashboard")
    return
  }

  if (to.meta.requiresAuth && !isAuthenticated()) {
    next({ path: "/login", query: { redirect: to.fullPath } })
    return
  }

  if (to.meta.requiresAdmin && !isAdmin()) {
    next("/forbidden")
    return
  }

  next()
})

export default router

import { createRouter, createWebHistory } from "vue-router"
import pinia from "@/stores"
import { useAuthStore } from "@/stores/auth"

import DefaultLayout from "@/layouts/DefaultLayout.vue"

import Home from "@/view/Home.vue"
import ProductDetail from "@/view/ProductDetail.vue"
import Login from "@/view/Login.vue"
import Register from "@/view/Register.vue"
import OrderHistory from "@/view/OrderHistory.vue"
import BankTopup from "@/view/BankTopup.vue"
import PaymentSuccess from "@/view/PaymentSuccess.vue"
import Forbidden from "@/view/Forbidden.vue"

const CLIENT_ROLES = ["USER", "RESELLER", "ADMIN"]

const routes = [
  {
    path: "/",
    component: DefaultLayout,
    children: [
      {
        path: "",
        name: "home",
        component: Home
      },
      {
        path: "product/:slug",
        name: "product-detail",
        component: ProductDetail
      },
      {
        path: "orders",
        name: "orders",
        component: OrderHistory,
        meta: { requiresAuth: true, roles: CLIENT_ROLES }
      },
      {
        path: "topup",
        name: "topup",
        component: BankTopup,
        meta: { requiresAuth: true, roles: CLIENT_ROLES }
      },
      {
        path: "/payment-success",
        component: PaymentSuccess,
        meta: { requiresAuth: true, roles: CLIENT_ROLES }
      }
    ]
  },
  {
    path: "/login",
    name: "login",
    component: Login,
    meta: { guestOnly: true }
  },
  {
    path: "/register",
    name: "register",
    component: Register,
    meta: { guestOnly: true }
  },
  {
    path: "/forbidden",
    name: "forbidden",
    component: Forbidden
  },
  {
    path: "/:pathMatch(.*)*",
    redirect: "/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore(pinia)
  auth.initFromStorage()

  if (to.meta.guestOnly && auth.isAuthenticated) {
    next("/")
    return
  }

  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    next({
      path: "/login",
      query: { redirect: to.fullPath }
    })
    return
  }

  const allowedRoles = to.meta.roles
  if (Array.isArray(allowedRoles) && allowedRoles.length > 0) {
    if (!allowedRoles.includes(auth.role)) {
      next("/forbidden")
      return
    }
  }

  next()
})

export default router

import { createRouter, createWebHistory } from "vue-router"
import DefaultLayout from "@/layouts/DefaultLayout.vue"

import Home from "@/view/Home.vue"
import ProductDetail from "@/view/ProductDetail.vue"
import Login from "@/view/Login.vue"
import OrderHistory from "@/view/OrderHistory.vue"

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
                meta: { requiresAuth: true } // 🔒 cần đăng nhập
            }
        ]
    },
    {
        path: "/login",
        name: "login",
        component: Login
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

/* 🔒 Route Guard */
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem("token")

    if (to.meta.requiresAuth && !token) {
        next("/login")
    } else {
        next()
    }
})

export default router
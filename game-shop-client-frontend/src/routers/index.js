// src/routers/index.js

import { createRouter, createWebHistory } from "vue-router"

// Layout
import DefaultLayout from "@/layouts/DefaultLayout.vue"

// Views
import Home from "@/view/Home.vue"
import ProductDetail from "@/view/ProductDetail.vue"
import Login from "@/view/Login.vue"
import OrderHistory from "@/view/OrderHistory.vue"
import TopupPaypal from "@/view/TopupPaypal.vue";
import PaymentSuccess from "@/view/PaymentSuccess.vue";

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
                meta: { requiresAuth: true }
            },
            {
                path: "topup",
                name: "topup",
                component: TopupPaypal,
                meta: { requiresAuth: true } // 🔒 bắt buộc đăng nhập mới nạp
            },
            {
                path: "/payment-success",
                component: PaymentSuccess
            },

        ]
    },
    {
        path: "/login",
        name: "login",
        component: Login
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

/* 🔒 Route Guard */
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem("token")

    if (to.meta.requiresAuth && !token) {
        next({
            path: "/login",
            query: { redirect: to.fullPath }
        })
    } else {
        next()
    }
})

export default router
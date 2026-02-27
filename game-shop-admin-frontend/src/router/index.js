import { createRouter, createWebHistory } from "vue-router";
import AdminLayout from "@/components/AdminLayout.vue";
import ProductPage from "@/views/ProductPage.vue";
import AddProductPage from "@/views/AddProductPage.vue";
import DashboardPage from "@/views/DashboardPage.vue";

const routes = [
    // Redirect tiện lợi
    {
        path: "/products",
        redirect: "/admin/products"
    },
    {
        path: "/products/add",
        redirect: "/admin/products/add"
    },

    // Nếu ai đó vào /dashboard → chuyển về /admin/dashboard
    {
        path: "/dashboard",
        redirect: "/admin/dashboard"
    },

    {
        path: "/admin",
        component: AdminLayout,
        children: [
            {
                path: "",
                redirect: "dashboard"
            },

            {
                path: "dashboard",
                component: DashboardPage
            },

            {
                path: "products",
                component: ProductPage
            },
            {
                path: "products/add",
                component: AddProductPage
            },

            {
                path: "orders",
                redirect: "products"
            },
            {
                path: "users",
                redirect: "products"
            },

            {
                path: ":pathMatch(.*)*",
                redirect: "dashboard"
            }
        ]
    }
];

export default createRouter({
    history: createWebHistory(),
    routes
});
import { createRouter, createWebHistory } from "vue-router";
import AdminLayout from "@/components/layout/AdminLayout.vue";
import ProductPage from "@/pages/ProductPage.vue";

const routes = [
    {
        path: "/admin",
        component: AdminLayout,
        children: [
            {
                path: "products",
                component: ProductPage
            }
        ]
    }
];

export default createRouter({
    history: createWebHistory(),
    routes
});

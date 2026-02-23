import { createRouter, createWebHistory } from "vue-router";
import AdminLayout from "@/components/layout/AdminLayout.vue";
import ProductPage from "@/pages/ProductPage.vue";
import AddProductPage from "@/pages/AddProductPage.vue"; // thêm import

const routes = [
    // Redirects tiện lợi: nhiều nơi có thể dùng '/products' hoặc '/products/add'
    {
        path: "/products",
        redirect: "/admin/products"
    },
    {
        path: "/products/add",
        redirect: "/admin/products/add"
    },

    {
        path: "/admin",
        component: AdminLayout,
        children: [
            // mặc định chuyển tới products
            {
                path: "",
                redirect: "products"
            },

            // giữ trang quản lý sản phẩm
            {
                path: "products",
                component: ProductPage
            },
            {
                path: "products/add", // truy cập: /admin/products/add
                component: AddProductPage
            },

            // các route thường gặp trong admin — redirect về products nếu chưa có page riêng
            {
                path: "dashboard",
                redirect: "products"
            },
            {
                path: "orders",
                redirect: "products"
            },
            {
                path: "users",
                redirect: "products"
            },

            // nếu có path con lạ khác, chuyển về products để tránh warning
            {
                path: ":pathMatch(.*)*",
                redirect: "products"
            }
        ]
    }
];

export default createRouter({
    history: createWebHistory(),
    routes
});

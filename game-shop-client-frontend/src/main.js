import { createApp } from "vue"
import App from "./App.vue"
import router from "./routers"   // ⚠️ kiểm tra path này

createApp(App)
    .use(router)   // 🔥 CÁI NÀY BẮT BUỘC PHẢI CÓ
    .mount("#app")
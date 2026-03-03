<template>
  <div class="page">
    <div class="page-header">
      <div>
        <div class="page-title">Thêm Sản Phẩm Mới</div>
        <div class="page-subtitle">Tạo sản phẩm kèm gói giá mặc định.</div>
      </div>
      <button @click="cancel" class="btn btn-outline">Hủy</button>
    </div>

    <form class="card" @submit.prevent="submit">
      <div class="form-grid">
        <div>
          <label class="helper">Game ID *</label>
          <input v-model.number="form.gameId" class="input" type="number" min="1" />
        </div>

        <div>
          <label class="helper">Tiêu đề *</label>
          <input v-model="form.title" class="input" type="text" />
        </div>

        <div>
          <label class="helper">Slug *</label>
          <input v-model="form.slug" class="input" type="text" />
        </div>

        <div>
          <label class="helper">Giá gói mặc định (VND) *</label>
          <input v-model.number="form.price" class="input" type="number" min="1" />
        </div>

        <div>
          <label class="helper">Tên gói mặc định</label>
          <input v-model="form.packageName" class="input" type="text" />
        </div>

        <div>
          <label class="helper">Loại (type)</label>
          <select v-model="form.type" class="select">
            <option value="KEY">KEY</option>
            <option value="ACCOUNT">ACCOUNT</option>
          </select>
        </div>

        <div>
          <label class="helper">Platform</label>
          <select v-model="form.platform" class="select">
            <option value="ALL">ALL</option>
            <option value="ANDROID">ANDROID</option>
            <option value="IOS">IOS</option>
          </select>
        </div>

        <div>
          <label class="helper">Trạng thái</label>
          <select v-model="form.status" class="select">
            <option :value="true">ĐANG BÁN</option>
            <option :value="false">NGỪNG BÁN</option>
          </select>
        </div>
      </div>

      <div>
        <label class="helper">Short description</label>
        <textarea v-model="form.shortDescription" rows="2" class="textarea"></textarea>
      </div>

      <div>
        <label class="helper">Description</label>
        <textarea v-model="form.description" rows="4" class="textarea"></textarea>
      </div>

      <div>
        <label class="helper">Ảnh đại diện</label>
        <input type="file" @change="onFileChange" accept="image/*" :disabled="loading" />
        <img v-if="preview" :src="preview" class="preview" />
      </div>

      <div style="display:flex; gap:10px; flex-wrap:wrap;">
        <button type="submit" class="btn btn-primary" :disabled="loading">Tạo sản phẩm</button>
        <button type="button" @click="cancel" class="btn btn-outline">Hủy</button>
      </div>

      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="success" class="helper">Tạo sản phẩm thành công. Đang chuyển về danh sách...</p>
    </form>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from "vue"
import { useRouter } from "vue-router"
import productService from "@/services/product.service.js"
import { parseApiError } from "@/utils/api-error"

const router = useRouter()

const form = ref({
  gameId: null,
  title: "",
  slug: "",
  price: null,
  packageName: "Mặc định",
  shortDescription: "",
  description: "",
  type: "KEY",
  platform: "ALL",
  status: true
})

const file = ref(null)
const preview = ref(null)
const loading = ref(false)
const error = ref("")
const success = ref(false)

onUnmounted(() => {
  if (preview.value) {
    URL.revokeObjectURL(preview.value)
    preview.value = null
  }
})

const onFileChange = (e) => {
  const f = e.target.files?.[0]
  if (!f) return

  if (preview.value) {
    URL.revokeObjectURL(preview.value)
    preview.value = null
  }

  file.value = f
  preview.value = URL.createObjectURL(f)
}

const validate = () => {
  if (!form.value.gameId || form.value.gameId <= 0) return "Game ID là bắt buộc"
  if (!form.value.title?.trim()) return "Tiêu đề là bắt buộc"
  if (!form.value.slug?.trim()) return "Slug là bắt buộc"
  if (!form.value.price || form.value.price <= 0) return "Giá phải > 0"
  return null
}

const submit = async () => {
  if (loading.value) return

  error.value = ""
  const v = validate()
  if (v) {
    error.value = v
    return
  }

  loading.value = true
  try {
    const payload = {
      gameId: form.value.gameId,
      title: form.value.title.trim(),
      slug: form.value.slug.trim(),
      shortDescription: form.value.shortDescription,
      description: form.value.description,
      type: form.value.type,
      platform: form.value.platform,
      status: form.value.status,
      packages: [
        {
          name: form.value.packageName || "Mặc định",
          price: form.value.price,
          durationValue: 30,
          durationUnit: "DAY"
        }
      ]
    }

    const fd = new FormData()
    fd.append("data", JSON.stringify(payload))
    if (file.value) fd.append("file", file.value)

    await productService.createProduct(fd)

    success.value = true
    if (preview.value) {
      URL.revokeObjectURL(preview.value)
      preview.value = null
    }

    setTimeout(() => {
      router.push({ path: "/admin/products", query: { refresh: Date.now() } })
    }, 700)
  } catch (e) {
    error.value = parseApiError(e, "Tạo sản phẩm thất bại")
  } finally {
    loading.value = false
  }
}

const cancel = () => {
  if (preview.value) {
    URL.revokeObjectURL(preview.value)
    preview.value = null
  }
  router.push("/admin/products")
}
</script>

<style scoped>
.preview {
  margin-top: 10px;
  width: 160px;
  height: 160px;
  object-fit: cover;
  border-radius: 16px;
  border: 1px solid #e4e0d7;
}
</style>

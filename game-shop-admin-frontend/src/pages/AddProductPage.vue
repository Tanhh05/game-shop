<template>
  <div class="add-product-page">
    <header class="header">
      <h2>Thêm Sản Phẩm Mới</h2>
      <button @click="cancel" class="btn-cancel">Hủy</button>
    </header>

    <form class="form" @submit.prevent="submit">
      <div class="row">
        <label>Game ID *</label>
        <input v-model.number="form.gameId" type="number" min="1" />
      </div>

      <div class="row">
        <label>Tiêu đề *</label>
        <input v-model="form.title" type="text" />
      </div>

      <div class="row">
        <label>Slug *</label>
        <input v-model="form.slug" type="text" />
      </div>

      <div class="row">
        <label>Giá (VND) *</label>
        <input v-model.number="form.price" type="number" min="0" />
      </div>

      <div class="row">
        <label>Short description</label>
        <textarea v-model="form.shortDescription" rows="2"></textarea>
      </div>

      <div class="row">
        <label>Description</label>
        <textarea v-model="form.description" rows="4"></textarea>
      </div>

      <div class="row">
        <label>Loại (type)</label>
        <input v-model="form.type" type="text" />
      </div>

      <div class="row">
        <label>Platform</label>
        <input v-model="form.platform" type="text" />
      </div>

      <div class="row">
        <label>Trạng thái</label>
        <select v-model="form.status">
          <option :value="true">ĐANG BÁN</option>
          <option :value="false">NGỪNG BÁN</option>
        </select>
      </div>

      <div class="row">
        <label>Ảnh đại diện</label>
        <input type="file" @change="onFileChange" accept="image/*" :disabled="loading" />
        <img v-if="preview" :src="preview" class="preview" />
      </div>

      <div class="actions">
        <button type="submit" :disabled="loading">Tạo sản phẩm</button>
        <button type="button" @click="cancel" class="btn-ghost">Hủy</button>
      </div>

      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="success" class="success">Tạo sản phẩm thành công. Đang chuyển về danh sách...</p>
    </form>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import productService from '@/services/product.service' // điều chỉnh nếu service có tên khác

const router = useRouter()

const form = ref({
  gameId: null,
  title: '',
  slug: '',
  price: null,
  shortDescription: '',
  description: '',
  type: '',
  platform: '',
  status: true
})

const file = ref(null)
const preview = ref(null)
const loading = ref(false)
const error = ref(null)
const success = ref(false)

// giải phóng preview URL khi unmount
onUnmounted(() => {
  if (preview.value) {
    URL.revokeObjectURL(preview.value)
    preview.value = null
  }
})

const onFileChange = (e) => {
  const f = e.target.files && e.target.files[0]
  if (!f) return
  // giải phóng preview cũ nếu có
  if (preview.value) {
    URL.revokeObjectURL(preview.value)
    preview.value = null
  }
  file.value = f
  preview.value = URL.createObjectURL(f)
}

const validate = () => {
  if (!form.value.gameId) return "Game ID là bắt buộc"
  if (!form.value.title || !form.value.title.trim()) return "Tiêu đề là bắt buộc"
  if (!form.value.slug || !form.value.slug.trim()) return "Slug là bắt buộc"
  if (form.value.price == null || form.value.price < 0) return "Giá phải là số >= 0"
  return null
}

const submit = async () => {
  // tránh double submit
  if (loading.value) return

  error.value = null
  const v = validate()
  if (v) {
    error.value = v
    return
  }

  loading.value = true
  try {
    const payload = {
      gameId: form.value.gameId,
      title: form.value.title,
      slug: form.value.slug,
      price: form.value.price,
      shortDescription: form.value.shortDescription,
      description: form.value.description,
      type: form.value.type,
      platform: form.value.platform,
      status: form.value.status
    }

    const fd = new FormData()
    fd.append('data', JSON.stringify(payload))
    if (file.value) fd.append('file', file.value)

    // gọi API: POST /api/products (multipart/form-data)
    await productService.createProduct(fd)

    success.value = true
    // giải phóng preview trước khi chuyển trang
    if (preview.value) {
      URL.revokeObjectURL(preview.value)
      preview.value = null
    }
    setTimeout(() => {
      router.push({ path: '/admin/products', query: { refresh: Date.now() } })
    }, 700)
  } catch (err) {
    console.error(err)
    // hiển thị lỗi từ backend nếu có
    error.value = err?.response?.data?.error || err?.response?.data?.message || err?.message || 'Tạo sản phẩm thất bại'
  } finally {
    loading.value = false
  }
}

const cancel = () => {
  // giải phóng preview nếu có
  if (preview.value) {
    URL.revokeObjectURL(preview.value)
    preview.value = null
  }
  router.push('/admin/products')
}
</script>

<style scoped>
.add-product-page {
  padding: 20px;
  color: #111;
  max-width: 800px;
  margin: 0 auto;
}

.header {
  display:flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.form {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
}

.row {
  display:flex;
  flex-direction: column;
  margin-bottom: 12px;
}

.row label {
  font-weight: 600;
  margin-bottom: 6px;
}

.row input[type="text"],
.row input[type="number"],
.row textarea,
.row select {
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.preview {
  margin-top: 8px;
  max-width: 140px;
  max-height: 90px;
  object-fit: cover;
  border-radius: 6px;
}

.actions {
  display:flex;
  gap: 10px;
  margin-top: 8px;
}

.actions button {
  padding: 8px 14px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
}

.btn-ghost {
  background: #f3f4f6;
}

.btn-cancel {
  background: transparent;
  border: 1px solid #ddd;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}

.error {
  color: #c53030;
  margin-top: 8px;
}

.success {
  color: #2f855a;
  margin-top: 8px;
}
</style>

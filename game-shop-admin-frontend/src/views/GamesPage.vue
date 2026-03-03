<template>
  <div class="page">
    <div class="page-header">
      <div>
        <div class="page-title">Quản Lý Games</div>
        <div class="page-subtitle">Tạo, cập nhật và quản lý trạng thái game.</div>
      </div>
      <button class="btn btn-primary" @click="openCreate = !openCreate">
        {{ openCreate ? 'Đóng form' : '+ Thêm game' }}
      </button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="openCreate" class="card">
      <div class="page-title">Tạo game mới</div>
      <div class="form-grid">
        <input v-model="form.name" class="input" placeholder="Name" />
        <input v-model="form.slug" class="input" placeholder="Slug" />
      </div>
      <textarea v-model="form.description" rows="3" class="textarea" placeholder="Description"></textarea>
      <input type="file" accept="image/*" @change="onFileChange" />
      <button class="btn btn-primary" @click="submitCreate">Tạo game</button>
    </div>

    <div v-if="editing" class="card">
      <div class="page-title">Chỉnh sửa game #{{ editForm.id }}</div>
      <div class="form-grid">
        <input v-model="editForm.name" class="input" placeholder="Name" />
        <input v-model="editForm.slug" class="input" placeholder="Slug" />
      </div>
      <textarea v-model="editForm.description" rows="3" class="textarea" placeholder="Description"></textarea>
      <input type="file" accept="image/*" @change="onEditFileChange" />
      <div style="display:flex; gap:10px;">
        <button class="btn btn-primary" @click="submitEdit">Lưu</button>
        <button class="btn btn-outline" @click="cancelEdit">Hủy</button>
      </div>
    </div>

    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Slug</th>
            <th>Status</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="games.length===0">
            <td colspan="5" class="helper">Không có game</td>
          </tr>
          <tr v-for="g in games" :key="g.id">
            <td>#{{ g.id }}</td>
            <td>{{ g.name }}</td>
            <td>{{ g.slug }}</td>
            <td>
              <span class="badge" :class="g.status ? 'success' : 'danger'">
                {{ g.status ? 'ACTIVE' : 'INACTIVE' }}
              </span>
            </td>
            <td style="display:flex; gap:8px; flex-wrap:wrap;">
              <button class="btn btn-ghost" @click="startEdit(g)">Sửa</button>
              <button class="btn btn-outline" @click="toggleStatus(g)">{{ g.status ? 'Disable' : 'Enable' }}</button>
              <button class="btn btn-danger" @click="removeGame(g)">Xóa</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button class="btn btn-outline" :disabled="page===0" @click="fetchGames(page-1)">←</button>
      <span>Trang {{ page + 1 }} / {{ totalPages }}</span>
      <button class="btn btn-outline" :disabled="page===totalPages-1" @click="fetchGames(page+1)">→</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { changeGameStatus, createGame, deleteGame, getGames, updateGame } from "@/services/game.service"
import { parseApiError } from "@/utils/api-error"
import { useUi } from "@/composables/useUi"

const ui = useUi()
const games = ref([])
const page = ref(0)
const totalPages = ref(0)
const error = ref("")
const openCreate = ref(false)

const form = ref({ name: "", slug: "", description: "", status: true })
const file = ref(null)

const editing = ref(false)
const editForm = ref({ id: null, name: "", slug: "", description: "", status: true })
const editFile = ref(null)

const fetchGames = async (nextPage = page.value) => {
  try {
    const res = await getGames(nextPage, 20)
    games.value = res.data?.content || []
    totalPages.value = res.data?.totalPages || 0
    page.value = res.data?.number ?? nextPage
  } catch (e) {
    error.value = parseApiError(e, "Không tải được games")
  }
}

const onFileChange = (e) => { file.value = e.target.files?.[0] || null }
const onEditFileChange = (e) => { editFile.value = e.target.files?.[0] || null }

const submitCreate = async () => {
  try {
    error.value = ""
    if (!form.value.name.trim() || !form.value.slug.trim()) {
      error.value = "Name và slug là bắt buộc"
      return
    }

    await createGame({ ...form.value }, file.value)
    ui.toast("Tạo game thành công", "success")
    form.value = { name: "", slug: "", description: "", status: true }
    file.value = null
    openCreate.value = false
    await fetchGames(page.value)
  } catch (e) {
    error.value = parseApiError(e, "Tạo game thất bại")
    ui.toast("Tạo game thất bại", "error")
  }
}

const startEdit = (g) => {
  editing.value = true
  editForm.value = { id: g.id, name: g.name, slug: g.slug, description: g.description || "", status: g.status }
  editFile.value = null
}

const cancelEdit = () => {
  editing.value = false
  editFile.value = null
}

const submitEdit = async () => {
  try {
    error.value = ""
    await updateGame(editForm.value.id, {
      name: editForm.value.name,
      slug: editForm.value.slug,
      description: editForm.value.description,
      status: editForm.value.status
    }, editFile.value)
    ui.toast("Cập nhật game thành công", "success")
    editing.value = false
    await fetchGames(page.value)
  } catch (e) {
    error.value = parseApiError(e, "Cập nhật game thất bại")
    ui.toast("Cập nhật game thất bại", "error")
  }
}

const toggleStatus = async (g) => {
  const ok = await ui.confirm({
    title: "Đổi trạng thái game",
    message: `Bạn có chắc muốn ${g.status ? "disable" : "enable"} game \"${g.name}\"?`
  })
  if (!ok) return

  try {
    error.value = ""
    await changeGameStatus(g.id, !g.status)
    ui.toast("Đổi trạng thái game thành công", "success")
    await fetchGames(page.value)
  } catch (e) {
    error.value = parseApiError(e, "Đổi trạng thái game thất bại")
    ui.toast("Đổi trạng thái game thất bại", "error")
  }
}

const removeGame = async (g) => {
  const ok = await ui.confirm({
    title: "Xóa game",
    message: `Xác nhận xóa game \"${g.name}\"?`
  })
  if (!ok) return

  try {
    error.value = ""
    await deleteGame(g.id)
    ui.toast("Xóa game thành công", "success")
    await fetchGames(page.value)
  } catch (e) {
    error.value = parseApiError(e, "Xóa game thất bại")
    ui.toast("Xóa game thất bại", "error")
  }
}

onMounted(fetchGames)
</script>

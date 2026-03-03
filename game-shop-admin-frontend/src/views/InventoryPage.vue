<template>
  <div class="page">
    <div class="page-header">
      <div>
        <div class="page-title">Quản Lý Inventory</div>
        <div class="page-subtitle">Nhập keys/accounts và theo dõi kho sản phẩm.</div>
      </div>
      <div style="display:flex; gap:10px; flex-wrap:wrap;">
        <button class="btn btn-outline" @click="exportStats">Export CSV</button>
        <button class="btn btn-outline" @click="exportGameSummary">Export Game Summary</button>
      </div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div class="form-grid">
      <div class="card">
        <div class="card-title">Import Keys</div>
        <input v-model.number="keyForm.productId" class="input" type="number" min="1" placeholder="Product ID" />
        <textarea v-model="keyForm.keysRaw" class="textarea" rows="6" placeholder="Mỗi dòng 1 key"></textarea>
        <div class="helper">
          Valid: {{ keyValidation.validCount }} | Duplicates: {{ keyValidation.duplicateCount }}
        </div>
        <div v-if="keyValidation.duplicateCount" class="helper" style="color:#b45309;">
          Duplicates: {{ keyValidation.duplicates.slice(0, 5).join(", ") }}
        </div>
        <button class="btn btn-primary" @click="submitKeys">Import Keys</button>
      </div>

      <div class="card">
        <div class="card-title">Import Accounts</div>
        <input v-model.number="accForm.productId" class="input" type="number" min="1" placeholder="Product ID" />
        <textarea v-model="accForm.accountsRaw" class="textarea" rows="6" placeholder="Mỗi dòng: username:password"></textarea>
        <div class="helper">
          Valid: {{ accountValidation.validCount }} | Invalid: {{ accountValidation.invalidCount }}
        </div>
        <div v-if="accountValidation.invalidCount" class="helper" style="color:#b45309;">
          Invalid lines: {{ accountValidation.invalidLines.slice(0, 3).join(" | ") }}
        </div>
        <button class="btn btn-primary" @click="submitAccounts">Import Accounts</button>
      </div>
    </div>

    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th>Product</th>
            <th>Game</th>
            <th>Available Keys</th>
            <th>Sold Keys</th>
            <th>Available Accounts</th>
            <th>Sold Accounts</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="stats.length===0">
            <td colspan="7" class="helper">Không có dữ liệu</td>
          </tr>
          <tr v-for="s in statsWithGame" :key="s.productId">
            <td>{{ s.productName }} (#{{ s.productId }})</td>
            <td>{{ s.gameName || "-" }}</td>
            <td>{{ s.availableKeys }}</td>
            <td>{{ s.soldKeys }}</td>
            <td>{{ s.availableAccounts }}</td>
            <td>{{ s.soldAccounts }}</td>
            <td>{{ s.totalInventory }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="gameSummary.length" class="card" style="margin-top:16px;">
      <div class="card-title">Inventory by Game</div>
      <div class="table-wrap" style="margin-top:10px;">
        <table class="table">
          <thead>
            <tr>
              <th>Game</th>
              <th>Products</th>
              <th>Available Keys</th>
              <th>Sold Keys</th>
              <th>Available Accounts</th>
              <th>Sold Accounts</th>
              <th>Total</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="g in gameSummary" :key="g.gameName">
              <td>{{ g.gameName }}</td>
              <td>{{ g.productCount }}</td>
              <td>{{ g.availableKeys }}</td>
              <td>{{ g.soldKeys }}</td>
              <td>{{ g.availableAccounts }}</td>
              <td>{{ g.soldAccounts }}</td>
              <td>{{ g.totalInventory }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from "vue"
import { getInventoryStats, importAccounts, importKeys } from "@/services/admin.service"
import productService from "@/services/product.service"
import { parseApiError } from "@/utils/api-error"
import { useUi } from "@/composables/useUi"

const ui = useUi()
const error = ref("")
const stats = ref([])
const products = ref([])

const keyForm = ref({ productId: null, keysRaw: "" })
const accForm = ref({ productId: null, accountsRaw: "" })

const fetchStats = async () => {
  try {
    const res = await getInventoryStats()
    stats.value = res.data || []
  } catch (e) {
    error.value = parseApiError(e, "Không tải được inventory stats")
  }
}

const fetchProducts = async () => {
  try {
    const res = await productService.getProducts({ page: 0, size: 200, sortBy: "id", direction: "desc" })
    products.value = res.data?.content || []
  } catch (_) {
    products.value = []
  }
}

const keyValidation = computed(() => {
  const lines = keyForm.value.keysRaw
    .split("\n")
    .map((x) => x.trim())
    .filter(Boolean)

  const seen = new Set()
  const duplicates = []
  lines.forEach((k) => {
    if (seen.has(k)) duplicates.push(k)
    seen.add(k)
  })

  return {
    keys: lines,
    duplicateCount: duplicates.length,
    duplicates,
    validCount: lines.length - duplicates.length
  }
})

const accountValidation = computed(() => {
  const lines = accForm.value.accountsRaw
    .split("\n")
    .map((x) => x.trim())
    .filter(Boolean)

  const invalidLines = []
  const accounts = lines
    .map((line) => {
      const idx = line.indexOf(":")
      if (idx === -1) {
        invalidLines.push(line)
        return null
      }
      const username = line.slice(0, idx).trim()
      const password = line.slice(idx + 1).trim()
      if (!username || !password) {
        invalidLines.push(line)
        return null
      }
      return { username, password }
    })
    .filter(Boolean)

  return {
    accounts,
    invalidLines,
    invalidCount: invalidLines.length,
    validCount: accounts.length
  }
})

const submitKeys = async () => {
  try {
    error.value = ""

    if (!keyForm.value.productId || keyValidation.value.keys.length === 0) {
      error.value = "Vui lòng nhập productId và danh sách keys"
      return
    }

    if (keyValidation.value.duplicateCount > 0) {
      error.value = "Danh sách keys có duplicate. Vui lòng kiểm tra lại."
      return
    }

    const ok = await ui.confirm({
      title: "Import keys",
      message: `Xác nhận import ${keyValidation.value.validCount} key cho product #${keyForm.value.productId}?`
    })
    if (!ok) return

    await importKeys({ productId: keyForm.value.productId, keys: keyValidation.value.keys })
    ui.toast("Import keys thành công", "success")
    keyForm.value.keysRaw = ""
    await fetchStats()
  } catch (e) {
    error.value = parseApiError(e, "Import keys thất bại")
    ui.toast("Import keys thất bại", "error")
  }
}

const submitAccounts = async () => {
  try {
    error.value = ""

    if (!accForm.value.productId || accountValidation.value.accounts.length === 0) {
      error.value = "Vui lòng nhập productId và danh sách accounts"
      return
    }

    if (accountValidation.value.invalidCount > 0) {
      error.value = "Danh sách accounts có dòng không hợp lệ."
      return
    }

    const ok = await ui.confirm({
      title: "Import accounts",
      message: `Xác nhận import ${accountValidation.value.validCount} account cho product #${accForm.value.productId}?`
    })
    if (!ok) return

    await importAccounts({ productId: accForm.value.productId, accounts: accountValidation.value.accounts })
    ui.toast("Import accounts thành công", "success")
    accForm.value.accountsRaw = ""
    await fetchStats()
  } catch (e) {
    error.value = parseApiError(e, "Import accounts thất bại")
    ui.toast("Import accounts thất bại", "error")
  }
}

const statsWithGame = computed(() => {
  const map = new Map()
  products.value.forEach((p) => map.set(p.id, p.gameName))
  return stats.value.map((s) => ({
    ...s,
    gameName: map.get(s.productId) || ""
  }))
})

const gameSummary = computed(() => {
  const grouped = new Map()
  statsWithGame.value.forEach((s) => {
    const key = s.gameName || "Unknown"
    if (!grouped.has(key)) {
      grouped.set(key, {
        gameName: key,
        productCount: 0,
        availableKeys: 0,
        soldKeys: 0,
        availableAccounts: 0,
        soldAccounts: 0,
        totalInventory: 0
      })
    }
    const g = grouped.get(key)
    g.productCount += 1
    g.availableKeys += s.availableKeys || 0
    g.soldKeys += s.soldKeys || 0
    g.availableAccounts += s.availableAccounts || 0
    g.soldAccounts += s.soldAccounts || 0
    g.totalInventory += s.totalInventory || 0
  })
  return Array.from(grouped.values()).sort((a, b) => b.totalInventory - a.totalInventory)
})

const downloadCsv = (rows, filename) => {
  const escape = (value) => {
    const str = String(value ?? "")
    if (str.includes(",") || str.includes("\n") || str.includes('"')) {
      return `"${str.replace(/"/g, '""')}"`
    }
    return str
  }

  const content = rows.map((row) => row.map(escape).join(",")).join("\n")
  const blob = new Blob([content], { type: "text/csv;charset=utf-8;" })
  const url = URL.createObjectURL(blob)
  const link = document.createElement("a")
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

const exportStats = () => {
  const rows = [
    ["productId", "productName", "gameName", "availableKeys", "soldKeys", "availableAccounts", "soldAccounts", "totalInventory"],
    ...statsWithGame.value.map((s) => [
      s.productId,
      s.productName,
      s.gameName,
      s.availableKeys,
      s.soldKeys,
      s.availableAccounts,
      s.soldAccounts,
      s.totalInventory
    ])
  ]
  downloadCsv(rows, "inventory-stats.csv")
}

const exportGameSummary = () => {
  const rows = [
    ["gameName", "productCount", "availableKeys", "soldKeys", "availableAccounts", "soldAccounts", "totalInventory"],
    ...gameSummary.value.map((g) => [
      g.gameName,
      g.productCount,
      g.availableKeys,
      g.soldKeys,
      g.availableAccounts,
      g.soldAccounts,
      g.totalInventory
    ])
  ]
  downloadCsv(rows, "inventory-by-game.csv")
}

onMounted(() => {
  fetchStats()
  fetchProducts()
})
</script>

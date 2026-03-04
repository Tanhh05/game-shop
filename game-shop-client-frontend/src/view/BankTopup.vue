<template>
  <section class="topup">
    <div class="topup__glow"></div>
    <div class="topup__container">
      <header class="topup__header">
        <div>
          <p class="eyebrow">Nạp tiền web</p>
          <h2 class="title">Chuyển khoản nhanh, cộng tiền tự động</h2>
          <p class="subtitle">
            Quét QR hoặc nhập thông tin ngân hàng bên dưới. Hệ thống sẽ tự động cộng tiền khi đúng nội dung chuyển khoản.
          </p>
        </div>
        <div class="header-card">
          <div class="header-card__label">Trạng thái</div>
          <div class="header-card__value">
            <span v-if="loading">Đang tải</span>
            <span v-else-if="qrData">Sẵn sàng</span>
            <span v-else>Lỗi</span>
          </div>
        </div>
      </header>

      <div v-if="loading" class="state-card">
        Đang tải QR...
      </div>

      <div v-else-if="qrData" class="topup__grid">
        <div class="qr-card">
          <div class="qr-card__frame">
            <img :src="qrData.qrUrl" alt="QR Code" class="qr-image" />
          </div>
          <p class="qr-card__hint">Quét QR để chuyển khoản nhanh</p>
        </div>

        <div class="bank-card">
          <h3>Thông tin ngân hàng</h3>
          <div class="bank-list">
            <div class="bank-row">
              <span>Ngân hàng</span>
              <strong>{{ qrData.bankName }}</strong>
            </div>
            <div class="bank-row">
              <span>Số tài khoản</span>
              <div class="row-right">
                <strong>{{ qrData.accountNumber }}</strong>
                <button class="copy-btn" @click="copyToClipboard(qrData.accountNumber)">Copy</button>
              </div>
            </div>
            <div class="bank-row">
              <span>Chủ tài khoản</span>
              <strong>{{ qrData.accountName }}</strong>
            </div>
            <div class="bank-row bank-row--highlight">
              <span>Nội dung CK</span>
              <div class="row-right">
                <strong>{{ qrData.depositContent }}</strong>
                <button class="copy-btn" @click="copyToClipboard(qrData.depositContent)">Copy</button>
              </div>
            </div>
          </div>
          <div class="note">
            Vui lòng chuyển khoản đúng nội dung để hệ thống tự động cộng tiền.
          </div>
        </div>
      </div>

      <div v-else class="state-card state-card--error">
        {{ error || "Không tải được QR" }}
      </div>

      <section class="history">
        <div class="history__header">
          <h3>Lịch sử nạp tiền</h3>
          <button class="refresh-btn" @click="loadLogs" :disabled="logsLoading">
            {{ logsLoading ? "Đang tải..." : "Làm mới" }}
          </button>
        </div>

        <div v-if="logsLoading" class="state-card">
          Đang tải lịch sử...
        </div>

        <div v-else-if="logsError" class="state-card state-card--error">
          {{ logsError }}
        </div>

        <div v-else-if="topupLogs.length === 0" class="state-card">
          Chưa có giao dịch nạp tiền.
        </div>

        <div v-else class="history__list">
          <div v-for="log in topupLogs" :key="log.id" class="history__item">
            <div>
              <div class="history__amount">+ {{ formatCurrency(log.amount) }}</div>
              <div class="history__meta">
                {{ formatDate(log.createdAt) }}
              </div>
            </div>
            <div class="history__info">
              <div class="history__tag">TOPUP</div>
              <div class="history__meta">
                Số dư sau: {{ formatCurrency(log.balanceAfter) }}
              </div>
              <div class="history__meta" v-if="log.bankTransactionId">
                Mã GD: {{ log.bankTransactionId }}
              </div>
              <div class="history__meta" v-else-if="log.refId">
                Ref: {{ log.refId }}
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, ref, onMounted } from "vue"
import { getDepositInfo, getWalletLogs } from "@/services/wallet.service.js";
import { parseApiError } from "@/utils/api-error"

const qrData = ref(null)
const loading = ref(true)
const error = ref("")
const logs = ref([])
const logsLoading = ref(false)
const logsError = ref("")

const loadQr = async () => {
  try {
    const res = await getDepositInfo()
    qrData.value = res.data
  } catch (err) {
    error.value = parseApiError(err, "Lỗi load QR")
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadQr()
  loadLogs()
})

const copyToClipboard = async (text) => {
  if (!text) return
  try {
    await navigator.clipboard.writeText(String(text))
  } catch (err) {
    console.error("Copy failed", err)
  }
}

const loadLogs = async () => {
  try {
    logsLoading.value = true
    logsError.value = ""
    const res = await getWalletLogs()
    logs.value = Array.isArray(res.data) ? res.data : []
  } catch (err) {
    logsError.value = parseApiError(err, "Không tải được lịch sử nạp tiền")
  } finally {
    logsLoading.value = false
  }
}

const topupLogs = computed(() => {
  return logs.value.filter((log) => String(log.type || "").toUpperCase() === "TOPUP")
})

const formatCurrency = (value) => {
  if (value == null) return "0 đ"
  return Number(value).toLocaleString("vi-VN") + " đ"
}

const formatDate = (value) => {
  if (!value) return "--"
  if (Array.isArray(value)) {
    const [y, m, d, h = 0, min = 0, s = 0] = value
    return new Date(y, m - 1, d, h, min, s).toLocaleString("vi-VN")
  }
  return new Date(value).toLocaleString("vi-VN")
}
</script>
<style scoped>

.topup {
  --topup-bg: #f3f4f6;
  --topup-panel: #ffffff;
  --topup-dark: #0f172a;
  --topup-muted: #64748b;
  --topup-accent: #ff7a18;
  --topup-accent-2: #f97316;
  --topup-border: #e2e8f0;
  --topup-shadow: 0 20px 50px rgba(15, 23, 42, 0.12);
  position: relative;
  background: radial-gradient(circle at top left, #fff3e6 0%, #f8f5f0 45%, #f3f4f6 100%);
  padding: 72px 0 90px;
  font-family: "Manrope", "Segoe UI", sans-serif;
  color: var(--topup-dark);
  overflow: hidden;
}

.topup__glow {
  position: absolute;
  top: -140px;
  right: -120px;
  width: 360px;
  height: 360px;
  background: radial-gradient(circle, rgba(249, 115, 22, 0.35), transparent 70%);
  filter: blur(6px);
  pointer-events: none;
}

.topup__container {
  max-width: 1100px;
  width: 92%;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.topup__header {
  display: flex;
  justify-content: space-between;
  gap: 28px;
  align-items: flex-start;
  margin-bottom: 36px;
  flex-wrap: wrap;
}

.eyebrow {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.28em;
  color: var(--topup-muted);
  margin: 0 0 12px;
}

.title {
  font-family: "Space Grotesk", "Manrope", sans-serif;
  font-size: clamp(26px, 3vw, 36px);
  margin: 0 0 12px;
}

.subtitle {
  max-width: 520px;
  color: var(--topup-muted);
  font-size: 15px;
  line-height: 1.6;
  margin: 0;
}

.header-card {
  background: var(--topup-panel);
  border-radius: 16px;
  padding: 18px 20px;
  border: 1px solid var(--topup-border);
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
  min-width: 160px;
}

.header-card__label {
  font-size: 12px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--topup-muted);
}

.header-card__value {
  margin-top: 6px;
  font-size: 22px;
  font-weight: 700;
}

.state-card {
  background: var(--topup-panel);
  border: 1px dashed var(--topup-border);
  padding: 24px;
  border-radius: 14px;
  text-align: center;
  color: var(--topup-muted);
}

.state-card--error {
  border-color: rgba(239, 83, 80, 0.4);
  color: #c0392b;
  background: rgba(239, 83, 80, 0.08);
}

.topup__grid {
  display: grid;
  grid-template-columns: minmax(260px, 320px) minmax(0, 1fr);
  gap: 24px;
  align-items: start;
}

.qr-card {
  background: var(--topup-panel);
  border-radius: 20px;
  padding: 24px;
  border: 1px solid var(--topup-border);
  box-shadow: var(--topup-shadow);
  text-align: center;
}

.qr-card__frame {
  background: #fff7ed;
  border-radius: 16px;
  padding: 16px;
  border: 1px solid #fed7aa;
}

.qr-image {
  width: 100%;
  max-width: 240px;
  height: auto;
  display: block;
  margin: 0 auto;
}

.qr-card__hint {
  margin: 16px 0 0;
  color: var(--topup-muted);
  font-size: 13px;
}

.bank-card {
  background: var(--topup-panel);
  border-radius: 20px;
  padding: 24px 28px;
  border: 1px solid var(--topup-border);
  box-shadow: var(--topup-shadow);
}

.bank-card h3 {
  margin: 0 0 18px;
  font-size: 20px;
}

.bank-list {
  display: grid;
  gap: 14px;
}

.bank-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  padding: 12px 14px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.bank-row--highlight {
  background: rgba(249, 115, 22, 0.08);
  border-color: rgba(249, 115, 22, 0.25);
}

.row-right {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.copy-btn {
  border: 1px solid rgba(249, 115, 22, 0.4);
  background: rgba(249, 115, 22, 0.12);
  color: #c2410c;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}

.note {
  margin-top: 18px;
  padding: 12px 14px;
  background: #0f172a;
  color: #f8fafc;
  border-radius: 12px;
  font-size: 13px;
}

.history {
  margin-top: 40px;
  display: grid;
  gap: 16px;
}

.history__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.history__header h3 {
  margin: 0;
  font-size: 20px;
}

.refresh-btn {
  border: 1px solid rgba(249, 115, 22, 0.35);
  background: rgba(249, 115, 22, 0.12);
  color: #c2410c;
  padding: 8px 14px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.history__list {
  display: grid;
  gap: 12px;
}

.history__item {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.history__amount {
  font-size: 18px;
  font-weight: 700;
  color: #16a34a;
}

.history__info {
  text-align: right;
  display: grid;
  gap: 4px;
}

.history__tag {
  font-size: 11px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: #f97316;
  font-weight: 700;
}

.history__meta {
  font-size: 12px;
  color: var(--topup-muted);
}

@media (max-width: 900px) {
  .topup__grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .topup__header {
    flex-direction: column;
  }

  .bank-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .row-right {
    width: 100%;
    justify-content: space-between;
  }

  .history__item {
    flex-direction: column;
    align-items: flex-start;
  }

  .history__info {
    text-align: left;
  }
}
</style>

import { reactive, readonly } from "vue"

const state = reactive({
  confirm: {
    open: false,
    title: "Xác nhận",
    message: "",
    resolve: null
  },
  toasts: []
})

let toastId = 1

const confirm = ({ title = "Xác nhận", message = "Bạn có chắc không?" } = {}) =>
  new Promise((resolve) => {
    state.confirm.open = true
    state.confirm.title = title
    state.confirm.message = message
    state.confirm.resolve = resolve
  })

const confirmAccept = () => {
  state.confirm.open = false
  if (typeof state.confirm.resolve === "function") state.confirm.resolve(true)
  state.confirm.resolve = null
}

const confirmCancel = () => {
  state.confirm.open = false
  if (typeof state.confirm.resolve === "function") state.confirm.resolve(false)
  state.confirm.resolve = null
}

const toast = (message, type = "info", timeout = 2800) => {
  const id = toastId++
  state.toasts.push({ id, message, type })
  if (timeout > 0) {
    setTimeout(() => removeToast(id), timeout)
  }
}

const removeToast = (id) => {
  const idx = state.toasts.findIndex((t) => t.id === id)
  if (idx >= 0) state.toasts.splice(idx, 1)
}

export const useUi = () => ({
  state: readonly(state),
  confirm,
  confirmAccept,
  confirmCancel,
  toast,
  removeToast
})

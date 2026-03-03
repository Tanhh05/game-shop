export const TOKEN_KEY = "token"

export const getToken = () => localStorage.getItem(TOKEN_KEY)

export const setAuth = (payload = {}) => {
  if (payload.token) localStorage.setItem("token", payload.token)
  if (payload.userId != null) localStorage.setItem("userId", String(payload.userId))
  if (payload.username) localStorage.setItem("username", payload.username)
  if (payload.role) localStorage.setItem("role", payload.role)
  if (payload.balance != null) localStorage.setItem("balance", String(payload.balance))
}

export const clearAuth = () => {
  localStorage.removeItem("token")
  localStorage.removeItem("userId")
  localStorage.removeItem("username")
  localStorage.removeItem("role")
  localStorage.removeItem("balance")
}

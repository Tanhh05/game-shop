export const parseApiError = (error, fallback = "Có lỗi xảy ra") => {
  const data = error?.response?.data

  if (!data) return fallback

  if (typeof data === "string") return data

  if (Array.isArray(data.details) && data.details.length > 0) {
    return data.details.join("\n")
  }

  return data.message || data.error || data.detail || fallback
}

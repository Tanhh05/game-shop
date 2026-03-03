import api from "@/api/axios"

export const getGames = (page = 0, size = 20) =>
  api.get("/games", { params: { page, size, sortBy: "id", direction: "desc" } })

export const createGame = (payload, file) => {
  const fd = new FormData()
  fd.append("data", JSON.stringify(payload))
  if (file) fd.append("file", file)
  return api.post("/games", fd)
}

export const updateGame = (id, payload, file) => {
  const fd = new FormData()
  fd.append("data", JSON.stringify(payload))
  if (file) fd.append("file", file)
  return api.patch(`/games/${id}`, fd)
}

export const changeGameStatus = (id, status) =>
  api.patch(`/games/${id}/status`, null, { params: { status } })

export const deleteGame = (id) => api.delete(`/games/${id}`)

// src/api/http.js
import axios from 'axios'
import { auth } from '../api/firebase'

const api = axios.create({
  baseURL: 'https://6d69c2462486.ngrok-free.app/citas/rest',
  headers: { 'Content-Type': 'application/json' },
})

// Este header especial le indica a ngrok que no muestre la pantalla de advertencia
api.defaults.headers.common['ngrok-skip-browser-warning'] = 'true'

api.interceptors.request.use(async (config) => {
  const user = auth.currentUser
  if (user) {
    const idToken = await user.getIdToken()
    config.headers.Authorization = `Bearer ${idToken}`
  }
  return config
})

export default api

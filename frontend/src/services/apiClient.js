// =============================================================================
// API Service — Axios Client with JWT Interceptors
// All backend communication goes through this module.
// =============================================================================

import axios from 'axios'

// ─── Create Axios instance ────────────────────────────────────────────────────
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 15_000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// ─── Request Interceptor — Attach JWT ────────────────────────────────────────
apiClient.interceptors.request.use(
  (config) => {
    const token = getAccessToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// ─── Response Interceptor — Handle 401 / Token Refresh ───────────────────────
let isRefreshing = false
let failedQueue = []

const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => (error ? prom.reject(error) : prom.resolve(token)))
  failedQueue = []
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // Queue requests while refresh is in-flight
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`
            return apiClient(originalRequest)
          })
          .catch((err) => Promise.reject(err))
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        const refreshToken = getRefreshToken()
        const { data } = await axios.post(
          `${import.meta.env.VITE_API_BASE_URL}/api/v1/auth/refresh`,
          { refreshToken },
        )
        setTokens(data.accessToken, data.refreshToken)
        processQueue(null, data.accessToken)
        originalRequest.headers.Authorization = `Bearer ${data.accessToken}`
        return apiClient(originalRequest)
      } catch (refreshError) {
        processQueue(refreshError, null)
        clearTokens()
        // Redirect to login
        window.location.href = '/login'
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }

    return Promise.reject(error)
  },
)

// ─── Token Storage Helpers ───────────────────────────────────────────────────
// In production Electron app: delegate to preload via window.slashAI.invoke()
// For web: use sessionStorage (never localStorage for JWTs!)

function getAccessToken() {
  return sessionStorage.getItem('slashai_access_token')
}

function getRefreshToken() {
  return sessionStorage.getItem('slashai_refresh_token')
}

export function setTokens(accessToken, refreshToken) {
  sessionStorage.setItem('slashai_access_token', accessToken)
  if (refreshToken) {
    sessionStorage.setItem('slashai_refresh_token', refreshToken)
  }
}

export function clearTokens() {
  sessionStorage.removeItem('slashai_access_token')
  sessionStorage.removeItem('slashai_refresh_token')
}

export default apiClient

// =============================================================================
// Auth Service — Authentication API calls
// =============================================================================

import apiClient, { setTokens, clearTokens } from './apiClient'

/**
 * Login with email and password.
 * Returns tokens and user profile.
 */
export async function loginWithPassword(email, password) {
  const { data } = await apiClient.post('/api/v1/auth/login', { email, password })
  setTokens(data.accessToken, data.refreshToken)
  return data
}

/**
 * Register a new local account.
 */
export async function registerUser(email, password, displayName) {
  const { data } = await apiClient.post('/api/v1/auth/register', {
    email,
    password,
    displayName,
  })
  return data
}

/**
 * Logout — clear tokens and notify backend to invalidate refresh token.
 */
export async function logout() {
  try {
    await apiClient.post('/api/v1/auth/logout')
  } finally {
    clearTokens()
    // Also notify Electron main process if running in desktop mode
    if (window.slashAI) {
      window.slashAI.send('auth:logout')
    }
  }
}

/**
 * Get the currently authenticated user's profile.
 */
export async function getCurrentUser() {
  const { data } = await apiClient.get('/api/v1/auth/me')
  return data
}

/**
 * Initiate OAuth login by redirecting to backend's OAuth2 endpoint.
 * @param {'google'|'github'} provider
 */
export function initiateOAuthLogin(provider) {
  const backendUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  window.location.href = `${backendUrl}/oauth2/authorization/${provider}`
}

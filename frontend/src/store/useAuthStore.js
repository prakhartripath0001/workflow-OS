// =============================================================================
// Zustand Auth Store
// =============================================================================
// Global authentication state. Used by components to read auth status
// without prop-drilling or React Context boilerplate.
// =============================================================================

import { create } from 'zustand'
import { devtools, persist } from 'zustand/middleware'
import { getCurrentUser, logout as apiLogout } from '../services/authService'

const useAuthStore = create(
  devtools(
    persist(
      (set, get) => ({
        // ── State ──────────────────────────────────────────────────────────
        user: null,           // { id, email, displayName, avatarUrl, role }
        isAuthenticated: false,
        isLoading: false,
        error: null,

        // ── Actions ────────────────────────────────────────────────────────

        /**
         * Fetch current user from backend and update store.
         * Called after OAuth redirect or on app bootstrap.
         */
        fetchCurrentUser: async () => {
          set({ isLoading: true, error: null })
          try {
            const user = await getCurrentUser()
            set({ user, isAuthenticated: true, isLoading: false })
          } catch (err) {
            set({ user: null, isAuthenticated: false, isLoading: false, error: err.message })
          }
        },

        /**
         * Set user manually (e.g. after email/password login response).
         */
        setUser: (user) => set({ user, isAuthenticated: !!user }),

        /**
         * Logout — clear local state and call API logout.
         */
        logout: async () => {
          try {
            await apiLogout()
          } finally {
            set({ user: null, isAuthenticated: false, error: null })
          }
        },

        clearError: () => set({ error: null }),
      }),
      {
        name: 'slashai-auth',
        // Only persist minimal, non-sensitive data
        partialize: (state) => ({ user: state.user, isAuthenticated: state.isAuthenticated }),
      },
    ),
    { name: 'AuthStore' },
  ),
)

export default useAuthStore

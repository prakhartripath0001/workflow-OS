// =============================================================================
// Electron IPC Handlers — Auth Channels
// Registered in main.js: authHandlers.register(ipcMain)
// =============================================================================

const { ipcMain, session } = require('electron')

/**
 * Register all auth-related IPC handlers.
 * @param {Electron.IpcMain} ipcMainInstance
 */
function register(ipcMainInstance) {
  /**
   * Get the current stored session token (from Electron's secure storage).
   * Returns null if not authenticated.
   */
  ipcMainInstance.handle('auth:get-token', async () => {
    try {
      // In production: read from OS keychain via keytar or safeStorage
      const token = global.sessionToken || null
      return { token }
    } catch (err) {
      console.error('[IPC:auth] Failed to get token:', err.message)
      return { token: null, error: err.message }
    }
  })

  /**
   * Logout — clear session state
   */
  ipcMainInstance.on('auth:logout', async (event) => {
    try {
      global.sessionToken = null
      // Clear Electron session cookies
      await session.defaultSession.clearStorageData({ storages: ['cookies', 'localstorage'] })
      event.sender.send('auth:session-updated', { authenticated: false })
    } catch (err) {
      console.error('[IPC:auth] Logout failed:', err.message)
    }
  })
}

module.exports = { register }

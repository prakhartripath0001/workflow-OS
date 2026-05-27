// =============================================================================
// Electron Preload — Secure IPC Bridge
// =============================================================================
// This file runs in a privileged context (Node.js enabled) but exposes ONLY
// a whitelist of APIs to the renderer via contextBridge.
//
// Security principle: Renderer process is completely sandboxed.
// All system/backend calls go through explicitly named bridge channels here.
// This prevents prototype pollution, remote code execution, and XSS escalation.
// =============================================================================

const { contextBridge, ipcRenderer } = require('electron')

// ─── Whitelist of valid IPC channel names ────────────────────────────────────
const VALID_SEND_CHANNELS = [
  'auth:logout',
  'auth:get-session',
  'system:open-external',
  'system:get-app-version',
  'system:toggle-devtools',
  'window:minimize',
  'window:maximize',
  'window:close',
]

const VALID_RECEIVE_CHANNELS = [
  'auth:session-updated',
  'system:update-available',
  'system:update-downloaded',
  'app:deep-link',
]

const VALID_INVOKE_CHANNELS = [
  'auth:get-token',
  'auth:refresh-token',
  'system:get-platform',
  'system:get-app-version',
  'keychain:get-secret',
  'keychain:set-secret',
  'workspace:grant-folder',
  'workspace:list-folders',
  'workspace:scan',
  'workspace:read-file',
  'extensions:list-installed',
]

// ─── Expose to renderer via window.slashAI ────────────────────────────────────
contextBridge.exposeInMainWorld('slashAI', {
  /**
   * Send a one-way message to the main process.
   * Use for fire-and-forget operations.
   */
  send: (channel, data) => {
    if (VALID_SEND_CHANNELS.includes(channel)) {
      ipcRenderer.send(channel, data)
    } else {
      console.error(`[IPC] Blocked unauthorized send channel: ${channel}`)
    }
  },

  /**
   * Send a message and wait for a reply (request-response pattern).
   * Use for operations that return a value.
   */
  invoke: (channel, data) => {
    if (VALID_INVOKE_CHANNELS.includes(channel)) {
      return ipcRenderer.invoke(channel, data)
    }
    return Promise.reject(new Error(`[IPC] Blocked unauthorized invoke channel: ${channel}`))
  },

  /**
   * Listen for messages FROM the main process.
   * Returns a cleanup function to remove the listener.
   */
  on: (channel, callback) => {
    if (VALID_RECEIVE_CHANNELS.includes(channel)) {
      // Wrap to strip the `event` arg — renderer should never see it
      const subscription = (_event, ...args) => callback(...args)
      ipcRenderer.on(channel, subscription)
      // Return cleanup function
      return () => ipcRenderer.removeListener(channel, subscription)
    }
    console.error(`[IPC] Blocked unauthorized receive channel: ${channel}`)
    return () => {}
  },

  /**
   * Remove all listeners on a channel.
   */
  removeAllListeners: (channel) => {
    if (VALID_RECEIVE_CHANNELS.includes(channel)) {
      ipcRenderer.removeAllListeners(channel)
    }
  },

  /**
   * Convenience: app metadata
   */
  platform: process.platform,
})

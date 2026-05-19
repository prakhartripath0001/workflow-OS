// =============================================================================
// Command Service — Slash Command API calls
// =============================================================================

import apiClient from './apiClient'

/**
 * Execute a slash command.
 * @param {string} rawInput - Full raw input from user, e.g. "/gmail compose to: foo@bar.com"
 * @returns {Promise<{status: string, result: object}>}
 */
export async function executeCommand(rawInput) {
  const { data } = await apiClient.post('/api/v1/commands/execute', { rawInput })
  return data
}

/**
 * Get all available commands.
 * @returns {Promise<Array<{name: string, description: string, category: string}>>}
 */
export async function getAvailableCommands() {
  const { data } = await apiClient.get('/api/v1/commands')
  return data
}

/**
 * Get command execution history for the current user.
 * @param {number} page
 * @param {number} size
 */
export async function getCommandHistory(page = 0, size = 20) {
  const { data } = await apiClient.get('/api/v1/commands/history', {
    params: { page, size },
  })
  return data
}

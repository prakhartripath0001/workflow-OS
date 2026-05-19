// =============================================================================
// useCommand — Custom Hook for Slash Command Execution
// =============================================================================

import { useState, useCallback } from 'react'
import { executeCommand, getAvailableCommands } from '../services/commandService'

/**
 * Hook for executing slash commands and tracking their state.
 *
 * @example
 * const { execute, isLoading, result, error } = useCommand()
 * await execute('/gmail compose to:foo@bar.com')
 */
export function useCommand() {
  const [isLoading, setIsLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)

  const execute = useCallback(async (rawInput) => {
    if (!rawInput?.trim()) return
    setIsLoading(true)
    setError(null)
    setResult(null)

    try {
      const response = await executeCommand(rawInput.trim())
      setResult(response)
      return response
    } catch (err) {
      const message = err.response?.data?.message || err.message || 'Command failed'
      setError(message)
      throw err
    } finally {
      setIsLoading(false)
    }
  }, [])

  const reset = useCallback(() => {
    setResult(null)
    setError(null)
  }, [])

  return { execute, isLoading, result, error, reset }
}

/**
 * Hook to fetch and cache available commands for autocomplete.
 */
export function useAvailableCommands() {
  const [commands, setCommands] = useState([])
  const [isLoading, setIsLoading] = useState(false)

  const load = useCallback(async () => {
    setIsLoading(true)
    try {
      const data = await getAvailableCommands()
      setCommands(data)
    } catch {
      // Silently fail — commands load is non-critical
    } finally {
      setIsLoading(false)
    }
  }, [])

  return { commands, isLoading, load }
}

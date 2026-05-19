// =============================================================================
// Vitest Global Test Setup
// =============================================================================
import '@testing-library/jest-dom'
import { vi } from 'vitest'

// Mock window.slashAI (Electron IPC bridge) so tests run in jsdom
window.slashAI = {
  send: vi.fn(),
  invoke: vi.fn().mockResolvedValue({}),
  on: vi.fn().mockReturnValue(() => {}),
  removeAllListeners: vi.fn(),
  platform: 'darwin',
}

// Silence console.error for expected errors in tests
const originalError = console.error
beforeAll(() => {
  console.error = (...args) => {
    if (typeof args[0] === 'string' && args[0].includes('Warning:')) return
    originalError(...args)
  }
})

afterAll(() => {
  console.error = originalError
})

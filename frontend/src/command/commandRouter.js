import { executeCommand } from '../services/commandService'
import { parseSlashCommand } from './commandParser'
import { commandRegistry } from './commandRegistry'

export class CommandRouter {
  constructor(registry = commandRegistry) {
    this.registry = registry
    this.middleware = []
  }

  use(fn) {
    this.middleware.push(fn)
  }

  async execute(rawInput, context = {}) {
    const parsed = parseSlashCommand(rawInput)
    if (parsed.type === 'natural_language') {
      return executeCommand(rawInput)
    }

    const localCommand = this.registry.find(parsed.commandId)
    const execution = {
      rawInput,
      parsed,
      context,
      command: localCommand,
    }

    for (const fn of this.middleware) {
      await fn(execution)
    }

    if (localCommand?.execute) {
      return localCommand.execute({ ...context, input: parsed, rawInput })
    }

    return executeCommand(rawInput)
  }
}

export const commandRouter = new CommandRouter()

import { commandMatches } from './commandParser'

export class CommandRegistry {
  constructor() {
    this.commands = new Map()
  }

  register(command, owner = 'core') {
    if (!command.command?.startsWith('/')) {
      throw new Error('Command must start with /')
    }
    this.commands.set(command.command, { ...command, owner })
  }

  unregisterOwner(owner) {
    for (const [key, command] of this.commands.entries()) {
      if (command.owner === owner) this.commands.delete(key)
    }
  }

  find(commandId) {
    return this.commands.get(commandId)
  }

  list() {
    return [...this.commands.values()].sort((a, b) => a.command.localeCompare(b.command))
  }

  suggest(query, limit = 8) {
    return this.list().filter((command) => commandMatches(command, query)).slice(0, limit)
  }
}

export const commandRegistry = new CommandRegistry()

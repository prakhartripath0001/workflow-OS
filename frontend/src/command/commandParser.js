export function parseSlashCommand(input) {
  const raw = input.trim()
  if (!raw.startsWith('/')) {
    return { raw, type: 'natural_language', namespace: null, action: null, args: [], flags: {} }
  }

  const tokens = raw.slice(1).match(/"[^"]+"|'[^']+'|\S+/g) ?? []
  const [namespace, action, ...args] = tokens.map((token) => token.replace(/^["']|["']$/g, ''))
  const flags = {}
  const positional = []

  for (const arg of args) {
    const pair = arg.match(/^--?([^=\s]+)=(.*)$/)
    if (pair) flags[pair[1]] = pair[2]
    else positional.push(arg)
  }

  return {
    raw,
    type: 'slash',
    namespace,
    action: action ?? '',
    commandId: `/${namespace}${action ? ` ${action}` : ''}`,
    args: positional,
    flags,
  }
}

export function commandMatches(command, query) {
  const normalized = query.toLowerCase().replace(/^\//, '')
  const haystack = `${command.command} ${command.description ?? ''}`.toLowerCase()
  let cursor = 0
  for (const char of normalized) {
    cursor = haystack.indexOf(char, cursor)
    if (cursor === -1) return false
    cursor += 1
  }
  return true
}

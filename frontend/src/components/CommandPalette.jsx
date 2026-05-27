import { useEffect, useMemo, useState } from 'react'
import { commandRegistry } from '../command/commandRegistry'
import { commandRouter } from '../command/commandRouter'
import { activateInlineExtension, loadInstalledExtensions } from '../extensions/extensionRuntime'
import workspaceExtension from '../extensions/coreWorkspaceExtension'

export default function CommandPalette() {
  const [open, setOpen] = useState(false)
  const [input, setInput] = useState('')
  const [result, setResult] = useState(null)
  const [commandsVersion, setCommandsVersion] = useState(0)

  useEffect(() => {
    const dispose = activateInlineExtension(workspaceExtension)
    loadInstalledExtensions().finally(() => setCommandsVersion((value) => value + 1))
    return dispose
  }, [])

  useEffect(() => {
    function onKeyDown(event) {
      if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === 'k') {
        event.preventDefault()
        setOpen((value) => !value)
      }
    }
    window.addEventListener('keydown', onKeyDown)
    return () => window.removeEventListener('keydown', onKeyDown)
  }, [])

  const suggestions = useMemo(
    () => commandRegistry.suggest(input || '/', 8),
    [input, commandsVersion],
  )

  async function submit(event) {
    event.preventDefault()
    if (!input.trim()) return
    const response = await commandRouter.execute(input)
    setResult(response)
  }

  if (!open) return null

  return (
    <div className="command-backdrop" onMouseDown={() => setOpen(false)}>
      <form className="command-palette" onSubmit={submit} onMouseDown={(event) => event.stopPropagation()}>
        <input
          autoFocus
          value={input}
          onChange={(event) => setInput(event.target.value)}
          placeholder="/workspace scan"
          className="command-input"
        />
        <div className="command-suggestions">
          {suggestions.map((command) => (
            <button
              type="button"
              key={command.command}
              className="command-suggestion"
              onClick={() => setInput(command.command)}
            >
              <span>{command.command}</span>
              <small>{command.description}</small>
            </button>
          ))}
        </div>
        {result && <pre className="command-result">{JSON.stringify(result, null, 2)}</pre>}
      </form>
    </div>
  )
}

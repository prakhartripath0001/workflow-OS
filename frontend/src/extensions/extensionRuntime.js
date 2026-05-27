import { commandRegistry } from '../command/commandRegistry'

function createExtensionContext(extension) {
  return {
    extension,
    commands: {
      register(command) {
        commandRegistry.register(command, extension.id)
      },
    },
    workspace: {
      grantFolder: () => window.slashAI.invoke('workspace:grant-folder'),
      listFolders: () => window.slashAI.invoke('workspace:list-folders'),
      scan: (rootPath, options) => window.slashAI.invoke('workspace:scan', { rootPath, options }),
      readFile: (path, maxBytes) => window.slashAI.invoke('workspace:read-file', { path, maxBytes }),
    },
    events: new EventTarget(),
  }
}

export async function loadInstalledExtensions() {
  const extensions = await window.slashAI.invoke('extensions:list-installed')
  for (const extension of extensions) {
    for (const command of extension.commands ?? []) {
      commandRegistry.register(
        {
          command: command.command,
          description: command.description,
          category: extension.name,
        },
        extension.id,
      )
    }
  }
  return extensions
}

export function activateInlineExtension(extensionModule) {
  const extension = extensionModule.default ?? extensionModule
  const ctx = createExtensionContext(extension)
  for (const command of extension.commands ?? []) {
    commandRegistry.register(command, extension.id)
  }
  extension.activate?.(ctx)
  return () => {
    extension.deactivate?.(ctx)
    commandRegistry.unregisterOwner(extension.id)
  }
}

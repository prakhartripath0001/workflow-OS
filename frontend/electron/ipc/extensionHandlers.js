const fs = require('fs/promises')
const path = require('path')
const crypto = require('crypto')
const { app, ipcMain } = require('electron')

function getExtensionRoot() {
  return path.join(app.getPath('userData'), 'extensions')
}

async function listInstalledExtensions() {
  const root = getExtensionRoot()
  await fs.mkdir(root, { recursive: true })
  const entries = await fs.readdir(root, { withFileTypes: true })
  const extensions = []
  for (const entry of entries) {
    if (!entry.isDirectory()) continue
    const manifestPath = path.join(root, entry.name, 'workflowos.extension.json')
    try {
      const manifest = JSON.parse(await fs.readFile(manifestPath, 'utf8'))
      extensions.push({
        ...manifest,
        installPath: path.join(root, entry.name),
        digest: crypto
          .createHash('sha256')
          .update(JSON.stringify(manifest))
          .digest('hex'),
      })
    } catch {
      // Invalid extensions are intentionally skipped at runtime.
    }
  }
  return extensions
}

function registerExtensionHandlers() {
  ipcMain.handle('extensions:list-installed', async () => listInstalledExtensions())
}

module.exports = { getExtensionRoot, registerExtensionHandlers }

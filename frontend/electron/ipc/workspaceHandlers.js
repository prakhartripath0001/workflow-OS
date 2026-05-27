const { dialog, ipcMain } = require('electron')
const { assertPathAllowed, grantWorkspace, listWorkspaces } = require('../services/permissionStore')
const { readTextFile, scanWorkspace } = require('../services/fileSearchService')

function registerWorkspaceHandlers(mainWindow) {
  ipcMain.handle('workspace:grant-folder', async () => {
    const result = await dialog.showOpenDialog(mainWindow, {
      properties: ['openDirectory'],
      title: 'Grant Workflow OS access to a workspace folder',
    })
    if (result.canceled || result.filePaths.length === 0) return null
    const folderPath = await grantWorkspace(result.filePaths[0])
    return { path: folderPath }
  })

  ipcMain.handle('workspace:list-folders', async () => listWorkspaces())

  ipcMain.handle('workspace:scan', async (_event, payload) => {
    await assertPathAllowed(payload.rootPath)
    return scanWorkspace(payload.rootPath, payload.options)
  })

  ipcMain.handle('workspace:read-file', async (_event, payload) => {
    await assertPathAllowed(payload.path)
    return {
      path: payload.path,
      content: await readTextFile(payload.path, payload.maxBytes),
    }
  })
}

module.exports = { registerWorkspaceHandlers }

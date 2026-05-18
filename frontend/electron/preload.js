// Electron preload script — exposes safe APIs to the renderer
// Context isolation is ON; this is the only bridge between Node and the browser

const { contextBridge, shell } = require('electron')

contextBridge.exposeInMainWorld('electron', {
  platform:      process.platform,
  version:       process.versions.electron,
  // Opens a URL in the system default browser (used for OAuth flows)
  openExternal:  (url) => shell.openExternal(url),
})

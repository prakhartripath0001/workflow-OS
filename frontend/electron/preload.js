// Electron preload script — exposes safe APIs to the renderer
// Context isolation is ON; this is the only bridge between Node and the browser

const { contextBridge } = require('electron')

contextBridge.exposeInMainWorld('electron', {
  platform: process.platform,
  version:  process.versions.electron,
})

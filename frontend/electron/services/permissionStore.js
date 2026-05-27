const fs = require('fs/promises')
const path = require('path')
const { app } = require('electron')

const STORE_FILE = 'permissions.json'

function getStorePath() {
  return path.join(app.getPath('userData'), STORE_FILE)
}

async function readStore() {
  try {
    const raw = await fs.readFile(getStorePath(), 'utf8')
    return JSON.parse(raw)
  } catch {
    return { workspaces: [] }
  }
}

async function writeStore(store) {
  await fs.mkdir(app.getPath('userData'), { recursive: true })
  await fs.writeFile(getStorePath(), JSON.stringify(store, null, 2), 'utf8')
}

function normalizeFolder(folderPath) {
  return path.resolve(folderPath)
}

function isPathInside(parent, candidate) {
  const relative = path.relative(normalizeFolder(parent), normalizeFolder(candidate))
  return relative === '' || (!relative.startsWith('..') && !path.isAbsolute(relative))
}

async function grantWorkspace(folderPath) {
  const store = await readStore()
  const normalized = normalizeFolder(folderPath)
  if (!store.workspaces.some((entry) => entry.path === normalized)) {
    store.workspaces.push({ path: normalized, grantedAt: new Date().toISOString() })
    await writeStore(store)
  }
  return normalized
}

async function listWorkspaces() {
  const store = await readStore()
  return store.workspaces
}

async function assertPathAllowed(targetPath) {
  const store = await readStore()
  const allowed = store.workspaces.some((entry) => isPathInside(entry.path, targetPath))
  if (!allowed) {
    throw new Error('Path is outside granted workspace scope')
  }
}

module.exports = {
  assertPathAllowed,
  grantWorkspace,
  isPathInside,
  listWorkspaces,
}

const fs = require('fs/promises')
const path = require('path')

const SUPPORTED_EXTENSIONS = new Set([
  '.txt', '.md', '.pdf', '.docx', '.xlsx', '.json', '.csv', '.java', '.js',
  '.ts', '.py', '.xml', '.yaml', '.yml', '.log',
])

const DEFAULT_IGNORE = [
  '**/.git/**',
  '**/node_modules/**',
  '**/dist/**',
  '**/build/**',
  '**/target/**',
  '**/.idea/**',
  '**/.vscode/**',
]

async function loadGitignore(rootPath) {
  try {
    const raw = await fs.readFile(path.join(rootPath, '.gitignore'), 'utf8')
    return raw
      .split(/\r?\n/)
      .map((line) => line.trim())
      .filter((line) => line && !line.startsWith('#'))
      .map((line) => (line.includes('/') ? `**/${line}` : `**/${line}/**`))
  } catch {
    return []
  }
}

function shouldIgnore(relativePath, patterns) {
  const normalized = relativePath.split(path.sep).join('/')
  return patterns.some((pattern) => {
    const cleaned = pattern.replace(/^\*\*\//, '').replace(/\/\*\*$/, '')
    return normalized === cleaned || normalized.startsWith(`${cleaned}/`) || normalized.includes(`/${cleaned}/`)
  })
}

async function scanWorkspace(rootPath, options = {}) {
  const maxFiles = options.maxFiles ?? 5000
  const maxDepth = options.maxDepth ?? 12
  const ignorePatterns = [...DEFAULT_IGNORE, ...(await loadGitignore(rootPath))]
  const files = []

  async function walk(currentPath, depth) {
    if (files.length >= maxFiles || depth > maxDepth) return
    const entries = await fs.readdir(currentPath, { withFileTypes: true })
    for (const entry of entries) {
      const absolutePath = path.join(currentPath, entry.name)
      const relativePath = path.relative(rootPath, absolutePath)
      if (shouldIgnore(relativePath, ignorePatterns)) continue

      if (entry.isDirectory()) {
        await walk(absolutePath, depth + 1)
        continue
      }

      const ext = path.extname(entry.name).toLowerCase()
      if (!SUPPORTED_EXTENSIONS.has(ext)) continue
      const stat = await fs.stat(absolutePath)
      files.push({
        path: absolutePath,
        relativePath,
        name: entry.name,
        extension: ext.slice(1),
        size: stat.size,
        modifiedAt: stat.mtime.toISOString(),
      })
    }
  }

  await walk(rootPath, 0)
  return files
}

async function readTextFile(filePath, maxBytes = 1024 * 1024) {
  const stat = await fs.stat(filePath)
  if (stat.size > maxBytes) {
    throw new Error(`File exceeds ${maxBytes} byte read limit`)
  }
  return fs.readFile(filePath, 'utf8')
}

module.exports = {
  DEFAULT_IGNORE,
  SUPPORTED_EXTENSIONS,
  readTextFile,
  scanWorkspace,
}

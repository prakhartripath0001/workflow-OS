export default {
  id: 'core.workspace',
  name: 'Workspace',
  version: '0.1.0',
  permissions: ['workspace:read', 'workspace:index'],
  commands: [
    {
      command: '/workspace grant',
      description: 'Grant access to a local workspace folder',
      execute: async (ctx) => ctx.workspace.grantFolder(),
    },
    {
      command: '/workspace scan',
      description: 'Scan indexed files in a granted workspace',
      execute: async (ctx) => {
        const rootPath = ctx.input.args[0] ?? (await ctx.workspace.listFolders())[0]?.path
        if (!rootPath) throw new Error('Grant a workspace folder first')
        return ctx.workspace.scan(rootPath)
      },
    },
    {
      command: '/file read',
      description: 'Read a file inside a granted workspace',
      execute: async (ctx) => {
        const filePath = ctx.input.args[0]
        if (!filePath) throw new Error('Usage: /file read /absolute/path')
        return ctx.workspace.readFile(filePath)
      },
    },
  ],
}

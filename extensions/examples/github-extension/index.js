export default {
  id: 'github-extension',
  name: 'GitHub Extension',
  version: '1.0.0',
  permissions: ['network', 'github', 'storage'],
  commands: [
    {
      command: '/github repos',
      description: 'Get repositories',
      execute: async (ctx) => ctx.github.getRepos(),
    },
    {
      command: '/github create-pr',
      description: 'Create a pull request',
      execute: async (ctx) => ctx.github.createPullRequest(ctx.input.flags),
    },
  ],
}

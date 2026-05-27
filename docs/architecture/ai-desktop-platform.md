# Workflow OS AI Desktop Platform Architecture

## Architecture Decision

Workflow OS should use a hybrid desktop architecture:

- Electron main process owns privileged local capabilities: filesystem, app launching, native dialogs, secure storage, extension process supervision.
- React renderer owns presentation only: command palette, marketplace, workflow builder, streaming result surfaces.
- Spring Boot owns durable product state: users, installed extensions, command history, OAuth tokens, marketplace metadata, workflow rules.
- Local workers own heavy desktop work: indexing, file watching, chunking, embeddings, and extension execution.

The important security boundary is that the renderer never gets Node.js. It calls a narrow preload API, and every privileged action is enforced again in the Electron main process.

## System Diagram

```text
React Renderer
  | window.slashAI.invoke(...)
  v
Electron Preload Allowlist
  | validated IPC channel
  v
Electron Main Process
  |-- Workspace Permission Store
  |-- File Scanner / Watcher
  |-- Extension Runtime Supervisor
  |-- Token Keychain Adapter
  |
  +--> Spring Boot REST API
         |-- Command Service
         |-- Extension Registry
         |-- OAuth Token Vault
         |-- Workspace Index Metadata
         |-- Workflow Automation Engine
         v
       PostgreSQL/MySQL + Vector Store
```

## Extension Runtime

Extensions are declared by `workflowos.extension.json` and installed into the Electron user data directory. The manifest is treated as the source of truth for identity, commands, permissions, version, and signing metadata.

```json
{
  "id": "github-extension",
  "name": "GitHub Extension",
  "version": "1.0.0",
  "entrypoint": "index.js",
  "permissions": ["network", "github", "storage"],
  "commands": [{ "command": "/github repos", "description": "Get repositories" }]
}
```

MVP execution can register manifest commands and route actual execution to Spring Boot integrations. V1 should run extension code in isolated Node worker processes with a capability-based RPC API. Enterprise should require signed extension packages and marketplace verification before loading.

## Slash Command Flow

```text
User presses Cmd/Ctrl+K
  -> React command palette opens
  -> commandParser parses namespace/action/args/flags
  -> commandRegistry resolves local extension command
  -> commandRouter runs middleware
  -> local command executes through allowed preload APIs
  -> otherwise Spring Boot /api/v1/commands/execute runs durable command pipeline
  -> result is returned to the palette and persisted as command history
```

The parser intentionally keeps syntax simple:

- `/namespace action arg1 --flag=value`
- natural language input goes to the AI intent mapper
- command chaining should be represented as an execution graph, not shell-like string concatenation

## Local File Access

Local file access is scoped-folder based, similar to VS Code workspaces. Users explicitly grant a folder through a native dialog. The app stores the granted path under Electron `userData`, then checks every scan/read request against that allowlist.

```text
Renderer request: workspace:read-file
  -> preload channel allowlist
  -> main process handler
  -> permissionStore.assertPathAllowed(path)
  -> fileSearchService.readTextFile(path, maxBytes)
  -> response to renderer
```

Supported indexing pipeline:

```text
Granted Folder
  -> recursive scanner
  -> ignore filters (.git, node_modules, build, target, .gitignore)
  -> metadata table file_index_records
  -> content extraction per file type
  -> chunker
  -> embedding worker
  -> vector DB
  -> RAG context builder
```

For production extraction:

- text/code/log/json/yaml/xml/csv: streaming text reader with byte limits
- PDF: Apache PDFBox backend worker or local Node extractor
- DOCX/XLSX: Apache POI backend worker
- embeddings: OpenAI text embedding API or local `bge-small-en`/`nomic-embed-text`
- vector DB: local LanceDB or Qdrant for desktop; pgvector if backend-centric

## AI Agent Execution

```text
Natural language: "Send yesterday's GitHub commits to Slack"
  -> intent detector
  -> planner selects tools: github.listCommits, slack.sendMessage
  -> permission gate checks github/slack/network
  -> OAuth token resolver decrypts provider tokens
  -> workflow engine builds DAG
  -> executor streams step events to UI
  -> command history stores trace and final result
```

Tool calls must be capability-based. An extension receives `ctx.github` only if the user installed the extension and granted `github`. It receives `ctx.workspace.readFile` only for paths inside granted folders.

## Database Modules

The current implementation adds:

- `extensions`
- `installed_extensions`
- `oauth_tokens`
- `workspace_folders`
- `file_index_records`

Next production tables:

- `extension_permissions`
- `slash_commands`
- `command_aliases`
- `command_history`
- `workflow_definitions`
- `workflow_runs`
- `workflow_run_steps`
- `automation_triggers`
- `vector_chunks`
- `file_tags`

## Security Rules

- Keep `nodeIntegration=false`, `contextIsolation=true`, `sandbox=true`.
- Do not expose generic `ipcRenderer.send`; expose narrow methods or allowlisted channels only.
- Never let extensions receive raw tokens. They call provider APIs through brokered SDK methods.
- Encrypt OAuth tokens with AES-GCM locally/server-side and move the key to OS keychain or KMS in production.
- Validate extension manifests with JSON Schema before install.
- Require package signatures before marketplace distribution.
- Enforce path scope in Electron main, even if the renderer has already checked it.

## Roadmap

MVP:

- Secure Electron shell and command palette.
- Manifest-based local extensions.
- Spring Boot extension registry.
- Scoped workspace folder grant/read/scan.
- Command history and basic AI intent fallback.

V1:

- Worker-process extension host.
- File watcher and incremental index.
- Vector search with LanceDB or Qdrant.
- OAuth provider abstraction for Google, GitHub, Slack, Notion, Jira.
- Workflow DAG executor with streaming step events.

V2:

- Marketplace publishing CLI.
- Extension signing and update channels.
- AI planner with tool registry and approval gates.
- Team/shared workflow templates.
- Background job queues and retry policies.

Enterprise:

- Organization policies for allowed extensions and file scopes.
- Central audit logs.
- SSO/SCIM.
- KMS-backed token vault.
- Remote worker pools for heavy indexing and AI workflows.

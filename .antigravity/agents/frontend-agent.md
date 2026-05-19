# Frontend Agent — Antigravity AI

## Role
Senior React/Electron engineer for SlashAI.

## Rules (Absolute)
1. Functional components + hooks ONLY
2. API calls in `src/services/` — never in component body
3. Zustand for global state — no Redux, no Context for global state
4. IPC via `window.slashAI.*` ONLY — never raw `ipcRenderer`
5. No JWTs in `localStorage`
6. Every component has a test file

## Primary Files Owned
- `frontend/src/**`
- `frontend/electron/**`
- `frontend/package.json`
- `frontend/vite.config.js`

## Common Tasks
- `feat: new page` → create in `src/pages/`, add route in `App.jsx`, lazy load
- `feat: new command` → add to `commandService.js`, create hook in `hooks/`
- `fix: IPC bug` → check `preload.js` whitelist, check `ipc/` handler
- `feat: new Zustand store` → create in `src/store/useXxxStore.js`

import { useEffect, useState, useCallback } from 'react'
import axios from 'axios'

const API_BASE = 'http://localhost:8080/api'

// ─── Status Badge ────────────────────────────────────────────────────────────
function StatusBadge({ status }) {
  const isUp = status === 'UP'
  return (
    <span
      className={`flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold
        ${isUp
          ? 'bg-emerald-500/15 text-emerald-400 ring-1 ring-emerald-500/30'
          : 'bg-red-500/15    text-red-400    ring-1 ring-red-500/30'
        }`}
    >
      <span className={`status-dot animate-pulse-slow ${isUp ? 'bg-emerald-400' : 'bg-red-400'}`} />
      {isUp ? 'Backend Online' : 'Backend Offline'}
    </span>
  )
}

// ─── Info Row ─────────────────────────────────────────────────────────────────
function InfoRow({ label, value }) {
  return (
    <div className="flex items-center justify-between py-2.5 border-b border-white/5 last:border-0">
      <span className="text-sm text-slate-400">{label}</span>
      <span className="text-sm font-medium text-white font-mono">{value ?? '—'}</span>
    </div>
  )
}

// ─── Sidebar Nav Item ─────────────────────────────────────────────────────────
function NavItem({ icon, label, active = false }) {
  return (
    <button
      className={`flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium
        transition-all duration-150
        ${active
          ? 'bg-brand-600/20 text-brand-400 shadow-inner'
          : 'text-slate-400 hover:bg-white/5 hover:text-white'
        }`}
    >
      <span className="text-lg">{icon}</span>
      {label}
    </button>
  )
}

// ─── Main App ────────────────────────────────────────────────────────────────
export default function App() {
  const [health,   setHealth]   = useState(null)
  const [info,     setInfo]     = useState(null)
  const [loading,  setLoading]  = useState(true)
  const [error,    setError]    = useState(null)
  const [lastPing, setLastPing] = useState(null)

  const fetchStatus = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const [healthRes, infoRes] = await Promise.all([
        axios.get(`${API_BASE}/health`),
        axios.get(`${API_BASE}/info`),
      ])
      setHealth(healthRes.data)
      setInfo(infoRes.data)
      setLastPing(new Date().toLocaleTimeString())
    } catch {
      setError('Could not reach the backend. Make sure Spring Boot is running on port 8080.')
      setHealth(null)
      setInfo(null)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { fetchStatus() }, [fetchStatus])

  // Auto-refresh every 30 s
  useEffect(() => {
    const id = setInterval(fetchStatus, 30_000)
    return () => clearInterval(id)
  }, [fetchStatus])

  const platform = window.electron?.platform ?? 'web'

  return (
    <div className="flex h-screen w-screen overflow-hidden bg-surface-900 animate-fade-in">

      {/* ── Sidebar ── */}
      <aside className="flex w-56 flex-shrink-0 flex-col gap-1 border-r border-white/5
                        bg-surface-800 px-3 py-6">
        {/* Logo */}
        <div className="mb-6 flex items-center gap-2.5 px-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-brand-600 shadow-lg shadow-brand-900/60">
            <span className="text-sm">⚡</span>
          </div>
          <span className="text-sm font-bold tracking-wide text-white">Workflow OS</span>
        </div>

        <NavItem icon="🏠" label="Dashboard" active />
        <NavItem icon="🔄" label="Workflows" />
        <NavItem icon="📊" label="Analytics" />
        <NavItem icon="⚙️" label="Settings" />

        {/* Bottom info */}
        <div className="mt-auto px-2">
          <p className="text-[10px] text-slate-600 leading-relaxed">
            Platform: <span className="text-slate-500">{platform}</span>
            <br />
            Electron: <span className="text-slate-500">{window.electron?.version ?? 'N/A'}</span>
          </p>
        </div>
      </aside>

      {/* ── Main ── */}
      <main className="flex flex-1 flex-col overflow-auto">

        {/* Top bar */}
        <header className="flex items-center justify-between border-b border-white/5
                           bg-surface-800/50 px-8 py-4 backdrop-blur-sm">
          <div>
            <h1 className="text-lg font-bold text-white">Dashboard</h1>
            <p className="text-xs text-slate-500">System status &amp; overview</p>
          </div>
          <div className="flex items-center gap-4">
            {health && <StatusBadge status={health.status} />}
            <button
              id="refresh-btn"
              onClick={fetchStatus}
              disabled={loading}
              className="btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span className={loading ? 'animate-spin' : ''}>↻</span>
              {loading ? 'Checking…' : 'Refresh'}
            </button>
          </div>
        </header>

        {/* Content */}
        <div className="flex-1 p-8 animate-slide-up">

          {/* Error banner */}
          {error && (
            <div id="error-banner"
              className="mb-6 rounded-xl border border-red-500/20 bg-red-500/10
                         px-5 py-4 text-sm text-red-400">
              ⚠️ &nbsp;{error}
            </div>
          )}

          {/* Stats row */}
          <div className="mb-8 grid grid-cols-3 gap-4">
            {[
              { label: 'API Status',   value: health?.status    ?? (loading ? '…' : 'DOWN'),  icon: '🟢' },
              { label: 'Service',      value: health?.service   ?? '—',                        icon: '🚀' },
              { label: 'API Version',  value: health?.version   ?? '—',                        icon: '🏷️' },
            ].map((card) => (
              <div key={card.label}
                className="glass-card flex items-center gap-4 px-5 py-4
                           transition-all duration-200 hover:border-white/20 hover:bg-white/8">
                <span className="text-2xl">{card.icon}</span>
                <div>
                  <p className="text-xs text-slate-500">{card.label}</p>
                  <p className="text-base font-semibold text-white">{card.value}</p>
                </div>
              </div>
            ))}
          </div>

          {/* Two-column detail */}
          <div className="grid grid-cols-2 gap-6">

            {/* Health detail */}
            <section className="glass-card p-6">
              <h2 className="mb-4 text-sm font-semibold uppercase tracking-wider text-slate-400">
                Health Details
              </h2>
              {health ? (
                <>
                  <InfoRow label="Status"    value={health.status} />
                  <InfoRow label="Service"   value={health.service} />
                  <InfoRow label="Version"   value={health.version} />
                  <InfoRow label="Timestamp" value={health.timestamp?.split('T').join(' ').slice(0, 19)} />
                </>
              ) : (
                <p className="text-sm text-slate-500">{loading ? 'Loading…' : 'No data'}</p>
              )}
              {lastPing && (
                <p className="mt-4 text-[11px] text-slate-600">Last ping: {lastPing}</p>
              )}
            </section>

            {/* Stack info */}
            <section className="glass-card p-6">
              <h2 className="mb-4 text-sm font-semibold uppercase tracking-wider text-slate-400">
                Tech Stack
              </h2>
              {info ? (
                <>
                  <InfoRow label="App"         value={info.app} />
                  <InfoRow label="Backend"     value={info.stack?.backend} />
                  <InfoRow label="Frontend"    value={info.stack?.frontend} />
                  <InfoRow label="Description" value={info.description} />
                </>
              ) : (
                <p className="text-sm text-slate-500">{loading ? 'Loading…' : 'No data'}</p>
              )}
            </section>

          </div>
        </div>
      </main>
    </div>
  )
}

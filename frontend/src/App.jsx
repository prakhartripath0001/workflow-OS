import { useState, useEffect } from 'react'
import AuthPage from './pages/AuthPage'
import './index.css'

/* ── Simple in-memory dashboard placeholder ── */
function Dashboard({ user, onLogout }) {
  return (
    <div className="dashboard">
      <div className="dash-sidebar">
        <div className="dash-logo">
          <span className="dash-logo-icon">W</span>
          <span className="dash-logo-text">Workflow OS</span>
        </div>
        <nav className="dash-nav">
          {['Dashboard', 'Workflows', 'Analytics', 'Settings'].map(item => (
            <button key={item} className={`dash-nav-item ${item === 'Dashboard' ? 'active' : ''}`}>
              {item}
            </button>
          ))}
        </nav>
        <div className="dash-user">
          <div className="dash-avatar">{user.name?.[0]?.toUpperCase() ?? '?'}</div>
          <div className="dash-user-info">
            <div className="dash-user-name">{user.name}</div>
            <div className="dash-user-email">{user.email}</div>
          </div>
          <button id="logout-btn" className="dash-logout" onClick={onLogout} title="Log out">
            <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2"
              strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
              <polyline points="16 17 21 12 16 7"/>
              <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
          </button>
        </div>
      </div>

      <main className="dash-main">
        <header className="dash-header">
          <div>
            <h1 className="dash-title">Dashboard</h1>
            <p className="dash-subtitle">Welcome back, {user.name?.split(' ')[0]}</p>
          </div>
          <button className="btn-primary">+ New Workflow</button>
        </header>

        <div className="dash-stats">
          {[
            { label: 'Active Workflows', value: '3', color: '#5c7cfa' },
            { label: 'Tasks Complete',   value: '12', color: '#37b24d' },
            { label: 'In Progress',      value: '5',  color: '#f59f00' },
            { label: 'Blocked',          value: '1',  color: '#f03e3e' },
          ].map(s => (
            <div key={s.label} className="stat-card">
              <div className="stat-value" style={{ color: s.color }}>{s.value}</div>
              <div className="stat-label">{s.label}</div>
            </div>
          ))}
        </div>

        <div className="dash-welcome-card glass-card">
          <h2>You're logged in!</h2>
          <p>Your Workflow OS workspace is ready. The backend is connected and your session is active.</p>
          <p style={{ marginTop: '0.5rem', opacity: 0.5, fontSize: '0.8rem' }}>
            Session token: {sessionStorage.getItem('wf_token')?.slice(0, 16)}…
          </p>
        </div>
      </main>
    </div>
  )
}

/* ── App root — handles auth state ── */
export default function App() {
  const [user, setUser] = useState(() => {
    try {
      const stored = sessionStorage.getItem('wf_user')
      return stored ? JSON.parse(stored) : null
    } catch { return null }
  })

  // ── Handle OAuth redirect: backend sends ?wf_token=...&wf_name=...&wf_email=... ──
  useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const token  = params.get('wf_token')
    const id     = params.get('wf_id')
    const name   = params.get('wf_name')?.replace(/\+/g, ' ')
    const email  = params.get('wf_email')

    if (token && email) {
      const userData = { id, name, email, token }
      sessionStorage.setItem('wf_token', token)
      sessionStorage.setItem('wf_user',  JSON.stringify({ id, name, email }))
      setUser(userData)
      // Clean the URL so refreshing doesn't re-process params
      window.history.replaceState({}, '', window.location.pathname)
    }
  }, [])

  function handleAuth(userData) {
    setUser(userData)
  }

  function handleLogout() {
    const token = sessionStorage.getItem('wf_token')
    if (token) {
      fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ token }),
      }).catch(() => {})
    }
    sessionStorage.removeItem('wf_token')
    sessionStorage.removeItem('wf_user')
    setUser(null)
  }

  if (!user) return <AuthPage onAuth={handleAuth} />
  return <Dashboard user={user} onLogout={handleLogout} />
}

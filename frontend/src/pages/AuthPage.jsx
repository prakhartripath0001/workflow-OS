import { useState } from 'react'
import axios from 'axios'

const API = 'http://localhost:8080/api/auth'

/* ─── Icons ──────────────────────────────────────────────────────── */
const IconMail = () => (
  <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2"
    strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
    <rect x="2" y="4" width="20" height="16" rx="2"/>
    <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/>
  </svg>
)

const IconLock = () => (
  <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2"
    strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
  </svg>
)

const IconUser = () => (
  <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2"
    strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
    <circle cx="12" cy="7" r="4"/>
  </svg>
)

const IconEye = ({ open }) => open ? (
  <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2"
    strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
    <circle cx="12" cy="12" r="3"/>
  </svg>
) : (
  <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2"
    strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
    <line x1="1" y1="1" x2="23" y2="23"/>
  </svg>
)

/* ─── Input Field ────────────────────────────────────────────────── */
function InputField({ id, label, type = 'text', value, onChange, icon, placeholder, rightSlot }) {
  return (
    <div className="field-group">
      <label htmlFor={id} className="field-label">{label}</label>
      <div className="field-wrapper">
        <span className="field-icon">{icon}</span>
        <input
          id={id} type={type} value={value} onChange={onChange}
          placeholder={placeholder} autoComplete="off" className="field-input"
        />
        {rightSlot && <span className="field-right">{rightSlot}</span>}
      </div>
    </div>
  )
}

/* ─── Auth Page ──────────────────────────────────────────────────── */
export default function AuthPage({ onAuth }) {
  const [mode, setMode]         = useState('login')
  const [name, setName]         = useState('')
  const [email, setEmail]       = useState('')
  const [password, setPassword] = useState('')
  const [showPw, setShowPw]     = useState(false)
  const [loading, setLoading]   = useState(false)
  const [error, setError]       = useState('')
  const [success, setSuccess]   = useState('')

  const isLogin = mode === 'login'

  function reset() { setName(''); setEmail(''); setPassword(''); setError(''); setSuccess('') }
  function switchMode(m) { reset(); setMode(m) }

  async function handleSubmit(e) {
    e.preventDefault()
    setError(''); setSuccess('')

    if (!email.trim() || !password.trim()) return setError('Email and password are required.')
    if (!isLogin && !name.trim())           return setError('Full name is required.')
    if (password.length < 6)               return setError('Password must be at least 6 characters.')

    setLoading(true)
    try {
      const endpoint = isLogin ? `${API}/login` : `${API}/register`
      const payload  = isLogin
        ? { email: email.trim().toLowerCase(), password }
        : { name: name.trim(), email: email.trim().toLowerCase(), password }

      const { data } = await axios.post(endpoint, payload)

      sessionStorage.setItem('wf_token', data.token)
      sessionStorage.setItem('wf_user',  JSON.stringify({ id: data.id, name: data.name, email: data.email }))

      setSuccess(isLogin ? 'Welcome back! Loading your workspace…' : 'Account created! Signing you in…')
      setTimeout(() => onAuth({ id: data.id, name: data.name, email: data.email, token: data.token }), 900)
    } catch (err) {
      const msg = err?.response?.data?.message || err?.message || 'Something went wrong.'
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-root">
      <div className="blob blob-1" />
      <div className="blob blob-2" />
      <div className="blob blob-3" />
      <div className="grid-overlay" />

      <div className="auth-center">
        {/* Logo */}
        <div className="auth-logo">
          <div className="logo-mark">⚡</div>
          <span className="logo-text">Workflow OS</span>
        </div>

        {/* Card */}
        <div className="auth-card">
          {/* Tabs */}
          <div className="auth-tabs">
            <button id="tab-login"  className={`auth-tab ${isLogin ? 'auth-tab--active' : ''}`}  onClick={() => switchMode('login')}>Sign In</button>
            <button id="tab-signup" className={`auth-tab ${!isLogin ? 'auth-tab--active' : ''}`} onClick={() => switchMode('signup')}>Sign Up</button>
            <div className={`auth-tab-indicator ${isLogin ? 'left' : 'right'}`} />
          </div>

          <div className="auth-body">
            <h1 className="auth-title">{isLogin ? 'Welcome back 👋' : 'Create account 🚀'}</h1>
            <p className="auth-subtitle">
              {isLogin ? 'Sign in to manage your workflows' : 'Get started — it takes 30 seconds'}
            </p>

            <form id="auth-form" onSubmit={handleSubmit} noValidate>
              {!isLogin && (
                <InputField id="auth-name" label="Full Name" value={name}
                  onChange={e => setName(e.target.value)} icon={<IconUser />} placeholder="Jane Doe" />
              )}
              <InputField id="auth-email" label="Email Address" type="email" value={email}
                onChange={e => setEmail(e.target.value)} icon={<IconMail />} placeholder="jane@example.com" />
              <InputField
                id="auth-password" label="Password"
                type={showPw ? 'text' : 'password'} value={password}
                onChange={e => setPassword(e.target.value)} icon={<IconLock />} placeholder="••••••••"
                rightSlot={
                  <button type="button" id="toggle-password" className="pw-toggle"
                    onClick={() => setShowPw(v => !v)} aria-label="Toggle password">
                    <IconEye open={showPw} />
                  </button>
                }
              />

              {error   && <div id="auth-error"   className="auth-alert auth-alert--error">{error}</div>}
              {success && <div id="auth-success" className="auth-alert auth-alert--success">{success}</div>}

              <button id="auth-submit" type="submit" disabled={loading} className="auth-btn">
                {loading ? <span className="btn-spinner" /> : (isLogin ? 'Sign In' : 'Create Account')}
              </button>
            </form>

            {isLogin && (
              <div className="demo-hint">
                <span>🎯 Demo:</span>
                <code>alice@workflowos.dev</code>
                <span>/</span>
                <code>password123</code>
              </div>
            )}

            <p className="auth-switch">
              {isLogin ? "Don't have an account?" : 'Already have an account?'}{' '}
              <button id={isLogin ? 'go-signup' : 'go-login'} className="auth-switch-btn"
                onClick={() => switchMode(isLogin ? 'signup' : 'login')}>
                {isLogin ? 'Sign Up' : 'Sign In'}
              </button>
            </p>
          </div>
        </div>

        <p className="auth-footer">⚡ Workflow OS · Electron · React · Spring Boot · PostgreSQL</p>
      </div>
    </div>
  )
}

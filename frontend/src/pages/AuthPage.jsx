import { useState } from 'react'
import { loginWithPassword, registerUser, initiateOAuthLogin } from '../services/authService'

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
      const normalizedEmail = email.trim().toLowerCase()
      const data = isLogin
        ? await loginWithPassword(normalizedEmail, password)
        : await registerUser(normalizedEmail, password, name.trim())

      sessionStorage.setItem('wf_token', data.token)
      sessionStorage.setItem('wf_user',  JSON.stringify({ id: data.id, name: data.name, email: data.email }))

      setSuccess(isLogin ? 'Welcome back! Loading your workspace…' : 'Account created! Signing you in…')
      setTimeout(() => onAuth({ id: data.id, name: data.name, email: data.email, token: data.token }), 900)
    } catch (err) {
      const msg = err?.code === 'ERR_NETWORK'
        ? 'Backend is not reachable. Start Docker services and wait for the backend to become healthy.'
        : err?.response?.data?.message || err?.message || 'Something went wrong.'
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  /**
   * Opens the Spring Boot OAuth2 authorization URL in the system browser.
   * After the OAuth dance, the backend redirects back to localhost:5173
   * with wf_token + wf_name + wf_email in the query string.
   * App.jsx picks those up and calls onAuth().
   */
  function openOAuth(provider) {
    initiateOAuthLogin(provider)
    setSuccess(`Opening ${provider} login in your browser…`)
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
          <div className="logo-mark">W</div>
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
            <h1 className="auth-title">{isLogin ? 'Welcome back' : 'Create account'}</h1>
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

            {/* OAuth divider */}
            <div className="oauth-divider"><span>or continue with</span></div>

            {/* OAuth buttons */}
            <div className="oauth-buttons">
              <button id="oauth-google" className="oauth-btn" onClick={() => openOAuth('google')}>
                <svg width="18" height="18" viewBox="0 0 24 24">
                  <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                  <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                  <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z"/>
                  <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                </svg>
                Continue with Google
              </button>
              <button id="oauth-github" className="oauth-btn" onClick={() => openOAuth('github')}>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0 1 12 6.844a9.59 9.59 0 0 1 2.504.337c1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.02 10.02 0 0 0 22 12.017C22 6.484 17.522 2 12 2z"/>
                </svg>
                Continue with GitHub
              </button>
            </div>

            {isLogin && (
              <div className="demo-hint">
                <span>Demo:</span>
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

        <p className="auth-footer">Workflow OS · Electron · React · Spring Boot · PostgreSQL</p>
      </div>
    </div>
  )
}

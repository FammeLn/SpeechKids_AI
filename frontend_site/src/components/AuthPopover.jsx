import { useEffect, useRef, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { lockHeader, unlockHeader } from './nav/headerLock'

/**
 * onTryLogin: async ({ email, password }) => ({ ok: true } | { ok: false, reason?: 'email'|'password'|'credentials' })
 * user: truthy => already logged in
 */
export default function AuthPopover({
  open,
  onClose,
  user,
  onTryLogin,
  t = (k) => k,
}) {
  const navigate = useNavigate()
  const location = useLocation()
  const panelRef = useRef(null)

  const closeTimerRef = useRef(null)

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const tt = (key, fallback) => {
    const value = t(key)
    return value === key ? fallback : value
  }

  // закрытие: ESC + клик вне
  useEffect(() => {
    if (!open) return

    const onKey = (e) => e.key === 'Escape' && onClose?.()

    const onClick = (e) => {
      if (e.target.closest('[data-auth-btn="1"]')) return
      if (!panelRef.current) return
      if (!panelRef.current.contains(e.target)) onClose?.()
    }

    window.addEventListener('keydown', onKey)
    window.addEventListener('mousedown', onClick)
    return () => {
      window.removeEventListener('keydown', onKey)
      window.removeEventListener('mousedown', onClick)
    }
  }, [open, onClose])

  useEffect(() => {
    return () => {
      if (closeTimerRef.current) window.clearTimeout(closeTimerRef.current)
    }
  }, [])

  useEffect(() => {
    if (!open) return
    lockHeader('auth-popover')
    return () => unlockHeader('auth-popover')
  }, [open])

  const openBig = (path, state = {}) => {
    // Открываем большой модал “поверх” текущей страницы
    navigate(path, {
      state: {
        backgroundLocation: location,
        ...state,
      },
    })

    // Маленький закрываем параллельно
    onClose?.()
  }

  const goRegister = () => openBig('/register')
  const goRecover = () => openBig('/forgot-password')

  const submit = async (e) => {
    e.preventDefault()
    if (submitting) return

    const payload = { email: email.trim(), password }
    setSubmitting(true)

    try {
      const res = await onTryLogin?.(payload)

      if (res?.ok) {
        onClose?.()
        return
      }

      openBig('/login', {
        prefillEmail: payload.email,
        prefillPassword: payload.password,
        authError: res?.reason || 'credentials',
      })
    } catch {
      openBig('/login', {
        prefillEmail: payload.email,
        prefillPassword: payload.password,
        authError: 'network',
      })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className={`navPop navPop--auth ${open ? 'open' : ''}`}>
      <div className="navPop__panel navPop__panel--auth" ref={panelRef} aria-hidden={!open}>
        {user ? (
          <div className="navPop__body">
            <div className="navPop__title">
              {tt('auth.alreadyLoggedIn', 'Вы уже вошли')}
            </div>

            <div className="navPop__actions">
              <button
                type="button"
                className="navPop__btn navPop__btn--accent"
                onClick={() => {
                  onClose?.()
                  navigate('/account')
                }}
              >
                {t('auth.account')}
              </button>

              <button
                type="button"
                className="navPop__btn navPop__btn--outline"
                onClick={onClose}
              >
                {tt('common.close', 'Закрыть')}
              </button>
            </div>
          </div>
        ) : (
          <form className="navPop__body" onSubmit={submit}>
            <div className="navPop__row">
              <label className="navPop__label">{t('auth.email')}</label>
              <input
                className="navPop__input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@example.com"
                autoComplete="email"
              />
            </div>

            <div className="navPop__row">
              <label className="navPop__label">{t('auth.password')}</label>
              <input
                className="navPop__input"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                type="password"
                autoComplete="current-password"
              />
            </div>

            <div className="navPop__helper">
              <button type="button" className="navPop__link" onClick={goRecover}>
                {t('auth.forgot')}
              </button>
            </div>

            <div className="navPop__actions">
              <button
                type="submit"
                className="navPop__btn navPop__btn--accent"
                disabled={submitting}
              >
                {submitting ? '...' : t('auth.login')}
              </button>

              <button
                type="button"
                className="navPop__btn navPop__btn--outline"
                onClick={goRegister}
                disabled={submitting}
              >
                {t('auth.register')}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}
import { useEffect, useRef, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { lockHeader, unlockHeader } from './nav/headerLock'

/**
 * onTryLogin: async ({ email, password }) => ({ ok: true } | { ok: false, reason?: 'email'|'password'|'credentials' })
 * user: truthy => already logged in
 */
export default function AuthPopover({ open, onClose, user, onTryLogin }) {
  const navigate = useNavigate()
  const location = useLocation()
  const panelRef = useRef(null)

  const closeTimerRef = useRef(null)

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [submitting, setSubmitting] = useState(false)

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
        backgroundLocation: location, // <- ключ к “не уходим со страницы”
        ...state,
      },
    })

    // Маленький закрываем параллельно (не ждём 340ms)
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
        // успех: просто закрываем маленький, большой не открываем
        onClose?.()
        return
      }

      // ошибка: открываем /login поверх текущей страницы и показываем ошибку
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
            <div className="navPop__title">Вы уже вошли</div>

            <div className="navPop__actions">
              <button
                type="button"
                className="navPop__btn navPop__btn--accent"
                onClick={() => {
                  onClose?.()
                  navigate('/account')
                }}
              >
                Аккаунт
              </button>

              <button type="button" className="navPop__btn navPop__btn--outline" onClick={onClose}>
                Закрыть
              </button>
            </div>
          </div>
        ) : (
          <form className="navPop__body" onSubmit={submit}>
            <div className="navPop__row">
              <label className="navPop__label">Почта</label>
              <input
                className="navPop__input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@example.com"
                autoComplete="email"
              />
            </div>

            <div className="navPop__row">
              <label className="navPop__label">Пароль</label>
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
                Забыли пароль?
              </button>
            </div>

            <div className="navPop__actions">
              <button type="submit" className="navPop__btn navPop__btn--accent" disabled={submitting}>
                {submitting ? '...' : 'Войти'}
              </button>

              <button type="button" className="navPop__btn navPop__btn--outline" onClick={goRegister} disabled={submitting}>
                Регистрация
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}
import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Coins } from 'lucide-react'

export default function AuthPopover({ open, onClose, onLogin, user, onLogout }) {
  const navigate = useNavigate()
  const panelRef = useRef(null)

  // таймер, чтобы менять user ПОСЛЕ анимации закрытия
  const closeTimerRef = useRef(null)

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  // helper: закрыть поповер и выполнить действие после анимации
  const closeThen = (fn) => {
    onClose?.()

    // время должно совпадать с transition у .authPanel (у тебя 320ms)
    if (closeTimerRef.current) window.clearTimeout(closeTimerRef.current)
    closeTimerRef.current = window.setTimeout(() => {
      fn?.()
    }, 340)
  }

  // закрытие: ESC + клик вне
  useEffect(() => {
    if (!open) return

    const onKey = (e) => {
      if (e.key === 'Escape') onClose?.()
    }

    const onClick = (e) => {
      // клик по кнопке пользователя не считаем "вне"
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

  // очистка таймера на размонтировании
  useEffect(() => {
    return () => {
      if (closeTimerRef.current) window.clearTimeout(closeTimerRef.current)
    }
  }, [])

  const submit = (e) => {
    e.preventDefault()

    const nextUser = {
      name: email?.split('@')?.[0] || 'User',
      avatarUrl: 'https://i.pravatar.cc/80?img=12',
      balance: 1250,
    }

    closeThen(() => {
      onLogin?.(nextUser)
    })
  }

  const goRegister = () => {
    closeThen(() => navigate('/register'))
  }

  const goRecover = () => {
    closeThen(() => navigate('/recover'))
  }

  const goAccount = () => {
    closeThen(() => navigate('/account'))
  }

  const doLogout = () => {
    closeThen(() => {
      onLogout?.()
    })
  }

  const displayName = user?.name || 'User'
  const balance = typeof user?.balance === 'number' ? user.balance : 0

  return (
    <div className={`authPopover ${open ? 'open' : ''}`}>
      <div className="authPanel" ref={panelRef} aria-hidden={!open}>
        {/* НЕ АВТОРИЗОВАН */}
        {!user && (
          <form className="authBody" onSubmit={submit}>
            <div className="authRow">
              <label className="authLabel">Почта</label>
              <input
                className="authInput"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@example.com"
                autoComplete="email"
              />
            </div>

            <div className="authRow">
              <label className="authLabel">Пароль</label>
              <input
                className="authInput"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                type="password"
                autoComplete="current-password"
              />
            </div>

            <div className="authHelper">
              <button type="button" className="authLinkBtn" onClick={goRecover}>
                Забыли пароль?
              </button>
            </div>

            <div className="authActions">
              <button className="btnAccent" type="submit">
                Войти
              </button>

              <button className="btnOutline" type="button" onClick={goRegister}>
                Регистрация
              </button>
            </div>
          </form>
        )}

        {/* АВТОРИЗОВАН */}
        {!!user && (
          <div className="authBody">
            <div className="authUser">
              <div className="authUserName">{displayName}</div>

              <div className="authBalance">
                <div className="authBalanceLabel">Баланс</div>
                <div className="authBalanceValue">
                  <Coins size={16} className="coinIcon" />
                  {balance}
                </div>
              </div>
            </div>

            <div className="authMenu">
              <button type="button" className="authMenuItem" onClick={goAccount}>
                Аккаунт
              </button>

              <button type="button" className="authMenuItem danger" onClick={doLogout}>
                Выйти
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { Info, Users, Construction, Settings, User, Bot, Coins } from 'lucide-react'

export default function Navbar({ user, settings, onOpenAuth, onOpenSettings, t = (k) => k }) {
  const location = useLocation()
  const navigate = useNavigate()

  // когда открыт модал-роут, реальный pathname = /login,
  // но активность navbar должна считаться по "фоновой" странице
  const bgPath = location.state?.backgroundLocation?.pathname
  const path = bgPath || location.pathname

  const isActive = (to, { end = false } = {}) => {
    if (end) return path === to
    return path === to || path.startsWith(to + '/')
  }

  const isOnUserPage =
    path.startsWith('/user') || path.startsWith('/profile') || path.startsWith('/account')

  const showBalance = !!settings?.showBalance

  return (
    <header className="navbar">
      <div className="navbarInner">
        <div className="navbarShell">
          {/* ЛЕВО: бренд */}
          <div className="brand">
            <Bot size={24} strokeWidth={2.2} />
            Halfi
          </div>

          {/* ЦЕНТР */}
          <nav className="navCenter">
            <Link to="/" className={isActive('/', { end: true }) ? 'navLink active' : 'navLink'}>
              <Info size={16} />
              {t('nav.about')}
            </Link>

            <Link to="/employees" className={isActive('/employees') ? 'navLink active' : 'navLink'}>
              <Users size={16} />
              {t('nav.employees')}
            </Link>

            <Link to="/sandbox" className={isActive('/sandbox') ? 'navLink active' : 'navLink'}>
              <Construction size={16} />
              {t('nav.sandbox')}
            </Link>
          </nav>

          {/* ПРАВО */}
          <div className="navRight">
            <button
              className="iconBtn"
              onClick={onOpenSettings}
              aria-label="Настройки"
              type="button"
              data-settings-btn="1"
            >
              <Settings size={18} />
            </button>

            {/* Баланс: держим в DOM всегда, чтобы был enter/exit transition */}
            <div
              className={['navBalance', showBalance ? 'isShown' : ''].join(' ')}
              aria-hidden={!showBalance}
            >
              <div className="navBalance__value" aria-label="Баланс">
                <Coins size={18} className="navBalance__ico" />
                <span className="navBalance__num">{user?.balance ?? 0}</span>
              </div>

              <button
                type="button"
                className="navBalance__plus"
                aria-label="Пополнить баланс"
                onClick={() => {
                  // TODO: страница/модал пополнения
                  // navigate('/topup')
                  alert('TODO: topup')
                }}
              >
                +
              </button>
            </div>

            <button
              className={`iconBtn ${isOnUserPage ? 'active' : ''}`}
              onClick={onOpenAuth}
              aria-label="Пользователь"
              type="button"
              data-auth-btn="1"
            >
              {user?.avatarUrl ? (
                <img className="avatar" src={user.avatarUrl} alt="User avatar" />
              ) : (
                <User size={18} />
              )}
            </button>
          </div>
        </div>
      </div>
    </header>
  )
}
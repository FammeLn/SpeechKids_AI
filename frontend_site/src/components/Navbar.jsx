import { NavLink, useLocation } from 'react-router-dom'
import {
  Info,
  Users,
  Construction, /* После завершения песочницы заменим на Box */
  Settings,
  User,
  Bot
} from 'lucide-react'

export default function Navbar({ user, onOpenAuth, onOpenSettings, t = (k) => k }) {
  const location = useLocation()

  const isOnUserPage =
    location.pathname.startsWith('/user') ||
    location.pathname.startsWith('/profile')

  return (
    <header className="navbar">
      <div className="navbarInner">
        <div className="navbarShell">

          {/* ЛЕВО: бренд */}
          <div className="brand">
            <Bot size={24} strokeWidth={2.2} />
            Halfi
          </div>

          {/* ЦЕНТР: 3 кнопки */}
          <nav className="navCenter">
            <NavLink
              to="/"
              end
              className={({ isActive }) =>
                isActive ? 'navLink active' : 'navLink'
              }
            >
              <Info size={16} />
              {t('nav.about')}
            </NavLink>

            <NavLink
              to="/friends"
              className={({ isActive }) =>
                isActive ? 'navLink active' : 'navLink'
              }
            >
              <Users size={16} />
              {t('nav.friends')}
            </NavLink>

            <NavLink
              to="/sandbox"
              className={({ isActive }) =>
                isActive ? 'navLink active' : 'navLink'
              }
            >
              <Construction size={16} />
              {t('nav.sandbox')}
            </NavLink>
          </nav>

          {/* ПРАВО: настройки + пользователь */}
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

            <button
              className={`iconBtn ${isOnUserPage ? 'active' : ''}`}
              onClick={onOpenAuth}
              aria-label="Пользователь"
              type="button"
              data-auth-btn="1"
            >
              {user?.avatarUrl ? (
                <img
                  className="avatar"
                  src={user.avatarUrl}
                  alt="User avatar"
                />
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
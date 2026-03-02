import { useEffect, useRef, useState, cloneElement, isValidElement } from 'react'
import { useLocation } from 'react-router-dom'
import Navbar from './Navbar'
import AuthPopover from './AuthPopover'
import TopLoadingBar from './TopLoadingBar'
import SettingsPopover from './SettingsPopover'
import { useSettings } from '../hooks/useSettings'
import { useT } from '../hooks/useT'

export default function Layout({ children }) {
  const location = useLocation()

  // auth popover
  const [drawerOpen, setDrawerOpen] = useState(false)
  const [user, setUser] = useState(null)

  const toggleDrawer = () => setDrawerOpen((v) => !v)
  const closeDrawer = () => setDrawerOpen(false)

  // settings popover
  const { settings, updateSettings } = useSettings()
  const [settingsOpen, setSettingsOpen] = useState(false)

  const toggleSettings = () => setSettingsOpen((v) => !v)
  const closeSettings = () => setSettingsOpen(false)

  // t
  const t = useT(settings.locale)

  // ✅ inject t into children (RouterView)
  const content = isValidElement(children) ? cloneElement(children, { t }) : children

  // lock header while popovers open
  useEffect(() => {
    document.documentElement.dataset.headerLock = (settingsOpen || drawerOpen) ? '1' : '0'
  }, [settingsOpen, drawerOpen])

  // ---------- Locale FX ----------
  const [localeFx, setLocaleFx] = useState('off') // 'off' | 'in' | 'out'
  const localeFxTimersRef = useRef([])

  const clearLocaleFxTimers = () => {
    localeFxTimersRef.current.forEach((id) => clearTimeout(id))
    localeFxTimersRef.current = []
  }

  const requestLocaleChange = (nextLocale) => {
    if (!nextLocale || nextLocale === settings.locale) return

    if (settings.reduceMotion) {
      updateSettings({ locale: nextLocale })
      return
    }

    clearLocaleFxTimers()
    setLocaleFx('in')

    localeFxTimersRef.current.push(
      setTimeout(() => {
        updateSettings({ locale: nextLocale })
        setLocaleFx('out')
      }, 250)
    )

    localeFxTimersRef.current.push(
      setTimeout(() => setLocaleFx('off'), 500)
    )
  }

  // ---------- Theme FX ----------
  const [themeFx, setThemeFx] = useState('off') // 'off' | 'in' | 'hold' | 'out'
  const themeFxTimersRef = useRef([])

  const clearThemeFxTimers = () => {
    themeFxTimersRef.current.forEach((id) => clearTimeout(id))
    themeFxTimersRef.current = []
  }

  const requestThemeChange = (nextTheme) => {
    if (!nextTheme || nextTheme === settings.theme) return

    if (settings.reduceMotion) {
      updateSettings({ theme: nextTheme })
      return
    }

    clearThemeFxTimers()
    setThemeFx('in')

    themeFxTimersRef.current.push(
      setTimeout(() => {
        setThemeFx('hold')
        document.documentElement.dataset.themeFx = 'hold'

        // тема меняется на следующем тике, чтобы transition точно применился
        themeFxTimersRef.current.push(
          setTimeout(() => updateSettings({ theme: nextTheme }), 0)
        )
      }, 250)
    )

    themeFxTimersRef.current.push(
      setTimeout(() => setThemeFx('out'), 750)
    )

    themeFxTimersRef.current.push(
      setTimeout(() => {
        setThemeFx('off')
        delete document.documentElement.dataset.themeFx
      }, 1000)
    )
  }

  // cleanup timers on unmount
  useEffect(() => {
    return () => {
      clearLocaleFxTimers()
      clearThemeFxTimers()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // ---------- Loading bar ----------
  const [barSignal, setBarSignal] = useState({ reqId: 0, finishId: 0 })

  useEffect(() => {
    setBarSignal((s) => ({ ...s, reqId: s.reqId + 1 }))

    const timerId = setTimeout(() => {
      setBarSignal((s) => ({ ...s, finishId: s.finishId + 1 }))
    }, 500)

    return () => clearTimeout(timerId)
  }, [location.pathname])

  return (
    <div>
      {/* Overlays must be outside .app so they don't inherit layout padding logic */}
      <div className={`localeFxOverlay ${localeFx}`} aria-hidden={localeFx === 'off'} />
      <div className={`themeFxOverlay ${themeFx}`} aria-hidden={themeFx === 'off'} />

      {/* hover-зона для показа navbar, когда он скрыт */}
      <div className="navbarPeekZone" />

      <div className="headerShell">
        <Navbar
          user={user}
          onOpenAuth={toggleDrawer}
          onOpenSettings={toggleSettings}
          t={t}
        />
        <TopLoadingBar signal={barSignal} />
      </div>

      {/* only content area uses .app padding-top animation */}
      <div className="app">
        <SettingsPopover
          open={settingsOpen}
          onClose={closeSettings}
          settings={settings}
          onChange={updateSettings}
          onLocaleChange={requestLocaleChange}
          onThemeChange={requestThemeChange}
          t={t}
        />

        <main className="container">
          {content}
        </main>

        <AuthPopover
          open={drawerOpen}
          onClose={closeDrawer}
          onLogin={(u) => setUser(u)}
          user={user}
          onLogout={() => setUser(null)}
          t={t}
        />
      </div>
    </div>
  )
}
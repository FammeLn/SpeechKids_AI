import { useEffect, useRef, useState } from 'react'
import { LOCALES } from '../i18n/locales'

export default function SettingsPopover({ open, onClose, settings, onLocaleChange, onThemeChange, onChange, t }) {
  const panelRef = useRef(null)
  const [localeOpen, setLocaleOpen] = useState(false)

  useEffect(() => {
    if (!open) return

    const onKey = (e) => e.key === 'Escape' && onClose?.()

    const onClick = (e) => {
      if (e.target.closest('[data-settings-btn="1"]')) return
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
    if (!open) setLocaleOpen(false)
  }, [open])

  const yesNo = (v) => (v ? t('common.yes') : t('common.no'))
  const localeShort = settings.locale === 'ru' ? t('locale.ruShort') : t('locale.enShort')

  return (
    <div className={`settingsPopover ${open ? 'open' : ''}`}>
      <div className="settingsPanel" ref={panelRef}>
        {/* Locale */}
        <div className="settingsRow">
          <div className="settingsLabel">{t('settings.locale')}</div>

          <div className={`localeWrap ${localeOpen ? 'open' : ''}`}>
            <button
              type="button"
              className="settingsBtn"
              onClick={() => setLocaleOpen((v) => !v)}
            >
              {LOCALES[settings.locale]?.short} <span>▾</span>
            </button>

            <div className="localeMenu">
              {Object.values(LOCALES).map((l) => (
                <button
                  key={l.code}
                  type="button"
                  className="localeItem"
                  onClick={() => {
                    onLocaleChange?.(l.code)
                    setLocaleOpen(false)
                  }}
                >
                  <span>{l.name}</span>
                  <span className="localeCode">{l.short}</span>
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Theme */}
        <div className="settingsRow">
          <div className="settingsLabel">{t('settings.theme')}</div>
          <button
            className={`settingsBtn ${settings.theme === 'dark' ? 'on' : ''}`}
            type="button"
            onClick={() => {
              const next = settings.theme === 'light' ? 'dark' : 'light'
              onThemeChange?.(next)
            }}
          >
            {settings.theme === 'light' ? t('common.light') : t('common.dark')}
          </button>
        </div>

        {/* Accent */}
        <div className="settingsRow">
          <div className="settingsLabel">{t('settings.accent')}</div>
          <div className="accentGrid">
            {['orange', 'blue', 'green', 'red'].map((c) => (
              <button
                key={c}
                type="button"
                className={`accentDot ${settings.accent === c ? 'active' : ''}`}
                onClick={() => onChange({ accent: c })}
                aria-label={`accent-${c}`}
                data-accent={c}
              />
            ))}
          </div>
        </div>

        {/* Hide navbar */}
        <div className="settingsRow">
          <div className="settingsLabel">{t('settings.hideNavbar')}</div>
          <button
            type="button"
            className={`settingsBtn ${settings.hideNavbar ? 'on' : ''}`}
            onClick={() => onChange({ hideNavbar: !settings.hideNavbar })}
          >
            {yesNo(settings.hideNavbar)}
          </button>
        </div>

        {/* Reduce motion */}
        <div className="settingsRow">
          <div className="settingsLabel">{t('settings.reduceMotion')}</div>
          <button
            type="button"
            className={`settingsBtn ${settings.reduceMotion ? 'on' : ''}`}
            onClick={() => onChange({ reduceMotion: !settings.reduceMotion })}
          >
            {yesNo(settings.reduceMotion)}
          </button>
        </div>
      </div>
    </div>
  )
}
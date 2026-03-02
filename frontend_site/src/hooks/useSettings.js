import { useEffect, useMemo, useState } from 'react'
import { DEFAULT_SETTINGS, SETTINGS_STORAGE_KEY } from '../config/settings'

const ACCENTS = {
  orange: { accent: '#ff7a00', accentSoft: '#ffb26b' },
  blue:   { accent: '#3b82f6', accentSoft: '#93c5fd' },
  green:  { accent: '#22c55e', accentSoft: '#86efac' },
  red:    { accent: '#ef4444', accentSoft: '#fca5a5' },
}

function safeParse(json) {
  try { return JSON.parse(json) } catch { return null }
}

export function useSettings() {
  const [settings, setSettings] = useState(() => {
    const raw = localStorage.getItem(SETTINGS_STORAGE_KEY)
    const parsed = raw ? safeParse(raw) : null
    return { ...DEFAULT_SETTINGS, ...(parsed || {}) }
  })

  const accentVars = useMemo(() => ACCENTS[settings.accent] || ACCENTS.orange, [settings.accent])

  useEffect(() => {
    localStorage.setItem(SETTINGS_STORAGE_KEY, JSON.stringify(settings))

    // html dataset для CSS
    const root = document.documentElement
    root.dataset.theme = settings.theme
    root.dataset.reduceMotion = settings.reduceMotion ? '1' : '0'
    root.dataset.hideNavbar = settings.hideNavbar ? '1' : '0'
    root.dataset.locale = settings.locale

    // CSS variables для акцента
    root.style.setProperty('--accent', accentVars.accent)
    root.style.setProperty('--accent-soft', accentVars.accentSoft)
  }, [settings, accentVars])

  const updateSettings = (patch) => {
    setSettings((s) => ({ ...s, ...patch }))
  }

  return { settings, updateSettings }
}
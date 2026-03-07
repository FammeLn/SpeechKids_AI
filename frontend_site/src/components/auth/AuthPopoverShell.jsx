import { useEffect, useMemo, useRef, useState } from 'react'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { lockHeader, unlockHeader } from '../nav/headerLock' // подстрой путь

export default function AuthPopoverShell() {
  const nav = useNavigate()
  const location = useLocation()

  const [isOpen, setIsOpen] = useState(false)
  const [closing, setClosing] = useState(false)
  const [swap, setSwap] = useState(false)

  const closeTimerRef = useRef(null)
  const swapTimerRef = useRef(null)

  const navTop = 66
  const panelStyle = useMemo(() => ({ '--authNavTop': `${navTop}px` }), [navTop])

  // BACKGROUND (для modal-routes паттерна)
  // Мы будем закрываться nav(-1), а если вдруг зашли прямой ссылкой — уйдём на /
  const hasBackground = !!location.state?.backgroundLocation

  // lock navbar + body scroll, set scrollbar compensation var
useEffect(() => {
  const html = document.documentElement

  // стековый lock
  lockHeader('auth-modal')

  // флаг "большой открыт" оставим как был (тебе нужен для body lock)
  html.setAttribute('data-auth-popover', '1')

  const sbw = window.innerWidth - document.documentElement.clientWidth
  html.style.setProperty('--sbw', `${Math.max(0, sbw)}px`)

  return () => {
    html.removeAttribute('data-auth-popover')
    html.style.removeProperty('--sbw')

    // снимаем только свой ключ
    unlockHeader('auth-modal')
  }
}, [])

  // OPEN after first paint so CSS transitions trigger
  useEffect(() => {
    const reduce = document.documentElement.getAttribute('data-reduce-motion') === '1'
    if (reduce) {
      setIsOpen(true)
      return
    }

    const t = setTimeout(() => {
      requestAnimationFrame(() => setIsOpen(true))
    }, 0)

    return () => clearTimeout(t)
  }, [])

  // SWAP pulse on auth route change
  useEffect(() => {
    const reduce = document.documentElement.getAttribute('data-reduce-motion') === '1'
    if (reduce) return

    setSwap(false)
    // eslint-disable-next-line no-unused-expressions
    document.body.offsetHeight

    requestAnimationFrame(() => {
      setSwap(true)
      if (swapTimerRef.current) window.clearTimeout(swapTimerRef.current)
      swapTimerRef.current = window.setTimeout(() => setSwap(false), 180)
    })

    return () => {
      if (swapTimerRef.current) window.clearTimeout(swapTimerRef.current)
    }
  }, [location.pathname])

  const finishCloseNav = () => {
    // если открыли модалку “поверх” фоновой страницы — просто назад
    if (hasBackground) {
      nav(-1)
      return
    }
    // если зашли прямой ссылкой на /login — закрытие ведёт на главную
    nav('/', { replace: true })
  }

  const doClose = () => {
    const reduce = document.documentElement.getAttribute('data-reduce-motion') === '1'
    if (reduce) {
      finishCloseNav()
      return
    }

    setClosing(true)
    setIsOpen(false)

    if (closeTimerRef.current) window.clearTimeout(closeTimerRef.current)
    closeTimerRef.current = window.setTimeout(() => {
      finishCloseNav()
    }, 260) // должно совпасть с твоим close transition
  }

  // ESC close
  useEffect(() => {
    const onKey = (e) => e.key === 'Escape' && doClose()
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // Navbar clickable: click on header closes modal but doesn't cancel click
  useEffect(() => {
    const onDownCapture = (e) => {
      // подстрой селектор при необходимости:
      const inHeader = e.target.closest('.headerShell')
      if (!inHeader) return
      doClose()
    }

    window.addEventListener('mousedown', onDownCapture, true)
    return () => window.removeEventListener('mousedown', onDownCapture, true)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  useEffect(() => {
    return () => {
      if (closeTimerRef.current) window.clearTimeout(closeTimerRef.current)
      if (swapTimerRef.current) window.clearTimeout(swapTimerRef.current)
    }
  }, [])

  return (
    <div
      className={['authModal', isOpen ? 'isOpen' : '', closing ? 'isOut' : ''].join(' ')}
      style={panelStyle}
      role="dialog"
      aria-modal="true"
    >
      <div className="authModal__backdrop" onMouseDown={doClose} />

      <div className="authModal__panel" aria-label="auth-popover-panel">
        <div
          className={['authModal__leftFx', swap ? 'isSwap' : ''].join(' ')}
          onMouseDown={(e) => e.stopPropagation()}
        >
          <Outlet />
        </div>
      </div>
    </div>
  )
}
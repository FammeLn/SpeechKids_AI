import { useEffect, useMemo, useRef, useState } from 'react'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'

export default function AuthPopoverShell() {
  const nav = useNavigate()
  const location = useLocation()

  const [isOpen, setIsOpen] = useState(false)
  const [closing, setClosing] = useState(false)
  const [swap, setSwap] = useState(false)

  const closeTimerRef = useRef(null)
  const swapTimerRef = useRef(null)

  // top navbar (64 + 2 + 10 = 76)
  const navTop = 76
  const panelStyle = useMemo(() => ({ '--authNavTop': `${navTop}px` }), [navTop])

  // lock navbar + body scroll, set scrollbar compensation var
  useEffect(() => {
    const html = document.documentElement
    html.setAttribute('data-header-lock', '1')
    html.setAttribute('data-auth-popover', '1')

    const sbw = window.innerWidth - document.documentElement.clientWidth
    html.style.setProperty('--sbw', `${Math.max(0, sbw)}px`)

    return () => {
      html.removeAttribute('data-header-lock')
      html.removeAttribute('data-auth-popover')
      html.style.removeProperty('--sbw')
    }
  }, [])

  // OPEN: гарантированно после первого paint
  useEffect(() => {
    const reduce = document.documentElement.getAttribute('data-reduce-motion') === '1'
    if (reduce) {
      setIsOpen(true)
      return
    }

    // 1) первый кадр — компонент уже в DOM со стартовыми opacity/transform из CSS
    // 2) второй кадр — включаем .isOpen -> transition
    const t = setTimeout(() => {
      requestAnimationFrame(() => setIsOpen(true))
    }, 0)

    return () => clearTimeout(t)
  }, [])

  // SWAP on auth route change: делаем "пульс" через два кадра
  useEffect(() => {
    const reduce = document.documentElement.getAttribute('data-reduce-motion') === '1'
    if (reduce) return

    // сбрасываем, чтобы следующий set(true) точно дал изменение стилей
    setSwap(false)

    // forced reflow (важно, иначе может склеиться в один кадр)
    // eslint-disable-next-line no-unused-expressions
    document.body.offsetHeight

    requestAnimationFrame(() => {
      setSwap(true)

      if (swapTimerRef.current) window.clearTimeout(swapTimerRef.current)
      swapTimerRef.current = window.setTimeout(() => {
        setSwap(false)
      }, 160)
    })

    return () => {
      if (swapTimerRef.current) window.clearTimeout(swapTimerRef.current)
    }
  }, [location.pathname])

  const doClose = () => {
    const reduce = document.documentElement.getAttribute('data-reduce-motion') === '1'
    if (reduce) {
      nav('/', { replace: true })
      return
    }

    setClosing(true)
    setIsOpen(false)

    if (closeTimerRef.current) window.clearTimeout(closeTimerRef.current)
    closeTimerRef.current = window.setTimeout(() => {
      nav('/', { replace: true })
    }, 260)
  }

  // ESC close
  useEffect(() => {
    const onKey = (e) => e.key === 'Escape' && doClose()
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
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
      className={[
        'authModal',
        isOpen ? 'isOpen' : '',
        closing ? 'isOut' : '',
      ].join(' ')}
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
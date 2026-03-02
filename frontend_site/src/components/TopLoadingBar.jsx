import { useEffect, useRef, useState } from 'react'

export default function TopLoadingBar({ signal }) {
  const [mode, setMode] = useState('idle') // idle | loading | lightFinishing | repainting
  const [light, setLight] = useState(0)    // 0..1 light fill width
  const [repaint, setRepaint] = useState(1) // 0..1 base repaint width (idle=1)

  const rafRef = useRef(null)
  const lastTRef = useRef(0)

  const finishPendingRef = useRef(false)

  // ✅ guards: чтобы req/finish обрабатывались один раз
  const lastReqIdRef = useRef(0)
  const lastFinishIdRef = useRef(0)

  // ✅ актуальный mode в ref, чтобы читать его в эффектах без deps
  const modeRef = useRef(mode)
  useEffect(() => { modeRef.current = mode }, [mode])

  useEffect(() => {
    const loop = (t) => {
      if (!lastTRef.current) lastTRef.current = t
      const dt = t - lastTRef.current
      lastTRef.current = t

      if (modeRef.current === 'loading') {
        setLight((p) => {
          const target = 0.92
          if (p >= target) return p
          const step = dt / 900
          return Math.min(target, p + step * (1 - p))
        })

        if (finishPendingRef.current) {
          finishPendingRef.current = false
          setMode('lightFinishing')
        }
      }

      if (modeRef.current === 'lightFinishing') {
        setLight((p) => {
          const next = p + dt / 220
          if (next >= 1) {
            setRepaint(0)
            setMode('repainting')
            return 1
          }
          return next
        })
      }

      if (modeRef.current === 'repainting') {
        setRepaint((p) => {
          const next = p + dt / 500
          if (next >= 1) {
            // вернуть в idle и сбросить светлую заливку
            setTimeout(() => {
              setLight(0)
              setRepaint(1)
              setMode('idle')
            }, 60)
            return 1
          }
          return next
        })
      }

      rafRef.current = requestAnimationFrame(loop)
    }

    rafRef.current = requestAnimationFrame(loop)
    return () => {
      if (rafRef.current) cancelAnimationFrame(rafRef.current)
      rafRef.current = null
      lastTRef.current = 0
    }
  }, [])

  // ✅ start loading — только если reqId реально новый
  useEffect(() => {
    const reqId = signal?.reqId ?? 0
    if (!reqId || reqId === lastReqIdRef.current) return
    lastReqIdRef.current = reqId

    setRepaint(1)
    setLight(0)
    finishPendingRef.current = false
    setMode('loading')
  }, [signal?.reqId])

  // ✅ finish loading — только если finishId реально новый
  useEffect(() => {
    const finishId = signal?.finishId ?? 0
    if (!finishId || finishId === lastFinishIdRef.current) return
    lastFinishIdRef.current = finishId

    const m = modeRef.current
    if (m === 'loading') {
      finishPendingRef.current = true
      return
    }

    // если вдруг finish прилетел когда idle (редко) — проиграем финал
    if (m === 'idle') {
      setLight(1)
      setRepaint(0)
      setMode('repainting')
    }
  }, [signal?.finishId])

  return (
    <div className="topBar">
      <div className="topBarBase" />

      <div
        className={`topBarLight ${mode === 'idle' ? 'hidden' : ''}`}
        style={{ transform: `scaleX(${light})` }}
      />

      <div
        className={`topBarRepaint ${mode === 'repainting' ? '' : 'hidden'}`}
        style={{ transform: `scaleX(${repaint})` }}
      />
    </div>
  )
}
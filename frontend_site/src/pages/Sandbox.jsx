import { useEffect, useRef, useState } from 'react'
import { connectSSE } from '../services/realtime/sse'

export default function Sandbox() {
  const [connected, setConnected] = useState(false)
  const [events, setEvents] = useState([])
  const disconnectRef = useRef(null)

  const connect = () => {
    if (disconnectRef.current) return

    // бэкендер завтра сделает этот эндпоинт (пример)
    const disconnect = connectSSE('/api/events/stream', {
      onOpen: () => setConnected(true),
      onMessage: (msg) => {
        setEvents((prev) => [
          { at: new Date().toISOString(), payload: msg },
          ...prev,
        ].slice(0, 50))
      },
      onError: () => {
        // SSE иногда вызывает onerror даже при авто-reconnect — это нормально
      },
    })

    disconnectRef.current = disconnect
  }

  const disconnect = () => {
    disconnectRef.current?.()
    disconnectRef.current = null
    setConnected(false)
  }

  useEffect(() => {
    return () => disconnect()
  }, [])

  return (
    <section>
      <h1>Песочница</h1>
      <p className="muted">
        Здесь тестим интеграцию: SSE/WS, события из Kafka через Spring.
      </p>

      <div className="card" style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
        <button onClick={connect} disabled={connected}>Connect SSE</button>
        <button onClick={disconnect} disabled={!connected}>Disconnect</button>
        <span className="muted">Статус: {connected ? 'connected' : 'disconnected'}</span>
      </div>

      <div className="card">
        <h3>Последние события</h3>
        {events.length === 0 ? (
          <p className="muted">Пока пусто. Когда бэк поднимет /api/events/stream — сюда польются сообщения.</p>
        ) : (
          <ul style={{ margin: 0, paddingLeft: 18 }}>
            {events.map((e, idx) => (
              <li key={idx}>
                <span className="muted">{e.at} </span>
                <code style={{ wordBreak: 'break-word' }}>{JSON.stringify(e.payload)}</code>
              </li>
            ))}
          </ul>
        )}
      </div>
    </section>
  )
}

import { ENV } from '../../config/env'

function toWsUrl(httpUrl) {
  // http://localhost -> ws://localhost
  // https:// -> wss://
  return httpUrl.replace(/^http/, 'ws')
}

/**
 * WebSocket: Kafka -> Spring -> WS -> Browser (+ можно отправлять команды)
 * endpoint пример: /ws
 */
export function connectWS(path, { onMessage, onOpen, onClose, onError } = {}) {
  const base = toWsUrl(ENV.API_URL)
  const ws = new WebSocket(`${base}${path}`)

  ws.onopen = () => onOpen?.()
  ws.onmessage = (evt) => {
    try {
      onMessage?.(JSON.parse(evt.data))
    } catch {
      onMessage?.(evt.data)
    }
  }
  ws.onclose = (evt) => onClose?.(evt)
  ws.onerror = (err) => onError?.(err)

  return {
    send: (data) => ws.send(typeof data === 'string' ? data : JSON.stringify(data)),
    close: () => ws.close(),
  }
}

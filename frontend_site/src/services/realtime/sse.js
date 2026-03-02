import { ENV } from '../../config/env'

/**
 * SSE: Kafka -> Spring -> SSE -> Browser
 * endpoint пример: /api/events/stream
 */
export function connectSSE(path, { onMessage, onOpen, onError } = {}) {
  const url = `${ENV.API_URL}${path}`
  const es = new EventSource(url)

  es.onopen = () => onOpen?.()
  es.onmessage = (evt) => {
    // Обычно Spring шлёт JSON строкой
    try {
      onMessage?.(JSON.parse(evt.data))
    } catch {
      onMessage?.(evt.data)
    }
  }
  es.onerror = (err) => {
    onError?.(err)
  }

  // вернуть функцию "отключиться"
  return () => es.close()
}

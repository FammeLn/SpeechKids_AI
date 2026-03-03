const ATTR_KEYS = 'data-header-lock-keys'
const ATTR_LOCK = 'data-header-lock'

function readKeys(html) {
  const raw = html.getAttribute(ATTR_KEYS) || ''
  return new Set(raw.split(' ').filter(Boolean))
}

function writeKeys(html, set) {
  const next = Array.from(set).join(' ')
  if (next) html.setAttribute(ATTR_KEYS, next)
  else html.removeAttribute(ATTR_KEYS)

  if (set.size > 0) html.setAttribute(ATTR_LOCK, '1')
  else html.removeAttribute(ATTR_LOCK)
}

export function lockHeader(key) {
  const html = document.documentElement
  const set = readKeys(html)
  set.add(key)
  writeKeys(html, set)
}

export function unlockHeader(key) {
  const html = document.documentElement
  const set = readKeys(html)
  set.delete(key)
  writeKeys(html, set)
}
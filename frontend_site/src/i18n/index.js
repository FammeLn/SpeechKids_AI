import ru from './locales/ru'
import en from './locales/en'
import { DEFAULT_LOCALE } from './locales'

const DICTS = { ru, en }

function get(obj, path) {
  return path
    .split('.')
    .reduce((acc, k) => (acc && acc[k] != null ? acc[k] : null), obj)
}

export function createT(locale) {
  const dict = DICTS[locale] || DICTS[DEFAULT_LOCALE]

  return function t(key, params) {
    const raw = get(dict, key) ?? key

    if (!params) return raw

    return String(raw).replace(/\{(\w+)\}/g, (_, name) => {
      return params[name] ?? `{${name}}`
    })
  }
}
import { useMemo } from 'react'
import { createT } from '../i18n'

export function useT(locale) {
  return useMemo(() => createT(locale), [locale])
}
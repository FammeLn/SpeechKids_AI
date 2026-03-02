import { api } from './client'

export function getFriends() {
  return api.get('/api/friends')
}

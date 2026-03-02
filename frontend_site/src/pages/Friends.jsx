import { useEffect, useState } from 'react'
import { getFriends } from '../services/api/friends'

export default function Friends() {
  const [friends, setFriends] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      setError('')
      try {
        const data = await getFriends()
        setFriends(data)
      } catch (e) {
        // пока бэк не готов — показываем мок
        setFriends([
          { id: 1, name: 'Алина', role: 'QA' },
          { id: 2, name: 'Даня', role: 'Backend' },
          { id: 3, name: 'Валя', role: 'Frontend' },
        ])
        setError('API пока недоступен — показаны мок-данные')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  return (
    <section>
      <h1>Приятели</h1>

      {loading && <p>Загрузка…</p>}
      {error && <p className="muted">{error}</p>}

      <div className="grid">
        {friends.map(f => (
          <div className="card" key={f.id}>
            <h3>{f.name}</h3>
            <p className="muted">{f.role}</p>
          </div>
        ))}
      </div>
    </section>
  )
}

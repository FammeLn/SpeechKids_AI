import { Routes, Route } from 'react-router-dom'
import About from '../pages/About'
import Friends from '../pages/Friends'
import Sandbox from '../pages/Sandbox'
import Auth from '../pages/Auth'
import NotFound from '../pages/NotFound'

export default function RouterView({ t }) {
  return (
    <Routes>
      <Route path="/" element={<About t={t} />} />
      <Route path="/friends" element={<Friends t={t} />} />
      <Route path="/sandbox" element={<Sandbox t={t} />} />

      <Route path="/login" element={<Auth t={t} />} />
      <Route path="/register" element={<Auth t={t} />} />
      <Route path="/forgot-password" element={<Auth t={t} />} />

      <Route path="*" element={<NotFound t={t} />} />
    </Routes>
  )
}
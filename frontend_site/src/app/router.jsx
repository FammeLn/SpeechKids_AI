import { Routes, Route } from 'react-router-dom'
import About from '../pages/About'
import Friends from '../pages/Friends'
import Sandbox from '../pages/Sandbox'
import NotFound from '../pages/NotFound'

import Login from '../pages/auth/Login'
import Register from '../pages/auth/Register'
import Recover from '../pages/auth/Recover'

import AuthPopoverShell from '../components/auth/AuthPopoverShell'

export default function RouterView({ t }) {
  return (
    <Routes>
      <Route path="/" element={<About t={t} />} />
      <Route path="/friends" element={<Friends t={t} />} />
      <Route path="/sandbox" element={<Sandbox t={t} />} />

      <Route element={<AuthPopoverShell />}>
        <Route path="/login" element={<Login t={t} />} />
        <Route path="/register" element={<Register t={t} />} />
        <Route path="/forgot-password" element={<Recover t={t} />} />
      </Route>

      <Route path="*" element={<NotFound t={t} />} />
    </Routes>
  )
}
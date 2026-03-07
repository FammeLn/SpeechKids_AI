import { Routes, Route, useLocation } from 'react-router-dom'
import About from '../pages/About'
import EmployeesPage from '../pages/employees/EmployeesPage'
import Sandbox from '../pages/Sandbox'
import NotFound from '../pages/NotFound'

import Login from '../pages/auth/Login'
import Register from '../pages/auth/Register'
import Recover from '../pages/auth/Recover'
import AuthPopoverShell from '../components/auth/AuthPopoverShell'

export default function RouterView({ t }) {
  const location = useLocation()
  const bg = location.state?.backgroundLocation

  return (
    <>
      {/* 1) Фоновые роуты (если есть bg — рендерим фон по bg) */}
      <Routes location={bg || location}>
        <Route path="/" element={<About t={t} />} />
        <Route path="/employees" element={<EmployeesPage t={t} />} />
        <Route path="/sandbox" element={<Sandbox t={t} />} />

        {/* auth также должен рендериться при прямом заходе без bg */}
        <Route element={<AuthPopoverShell />}>
          <Route path="/login" element={<Login t={t} />} />
          <Route path="/register" element={<Register t={t} />} />
          <Route path="/forgot-password" element={<Recover t={t} />} />
        </Route>

        <Route path="*" element={<NotFound t={t} />} />
      </Routes>

      {/* 2) Если есть backgroundLocation — поверх рендерим модал */}
      {bg && (
        <Routes>
          <Route element={<AuthPopoverShell />}>
            <Route path="/login" element={<Login t={t} />} />
            <Route path="/register" element={<Register t={t} />} />
            <Route path="/forgot-password" element={<Recover t={t} />} />
          </Route>
        </Routes>
      )}
    </>
  )
}
import { useMemo, useState } from 'react'
import EmployeesSidebar from './EmployeesSidebar'
import EmployeesOverview from './sections/EmployeesOverview'
import EmployeesAgentTemplate from './sections/EmployeesAgentTemplate'
import EmployeesStats from './sections/EmployeesStats'
import EmployeesManagement from './sections/EmployeesManagement'
import EmployeesPublicCatalog from './sections/EmployeesPublicCatalog'
import EmployeesPublished from './sections/EmployeesPublished'
import EmployeesApps from './sections/EmployeesApps'
import EmployeesSettings from './sections/EmployeesSettings'

export default function EmployeesPage({ t = (k) => k }) {
  const [collapsed, setCollapsed] = useState(false)
  const [activeSection, setActiveSection] = useState('free-agents')

  const agents = useMemo(
    () =>
      Array.from({ length: 12 }, (_, index) => ({
        section: `agent-${index + 1}`,
        label: `${t('employees.sidebar.agent')} ${index + 1}`,
      })),
    [t]
  )

  const content = useMemo(() => {
    if (activeSection === 'free-agents') {
      return <EmployeesAgentTemplate t={t} titleKey="employees.sidebar.freeAgents" />
    }

    if (activeSection.startsWith('agent-')) {
      const id = activeSection.replace('agent-', '')
      return (
        <EmployeesAgentTemplate
          t={t}
          title={`${t('employees.sidebar.agent')} ${id}`}
        />
      )
    }

    switch (activeSection) {
      case 'stats':
        return <EmployeesStats t={t} />
      case 'management':
        return <EmployeesManagement t={t} />
      case 'public':
        return <EmployeesPublicCatalog t={t} />
      case 'published':
        return <EmployeesPublished t={t} />
      case 'apps':
        return <EmployeesApps t={t} />
      case 'settings':
        return <EmployeesSettings t={t} />
      default:
        return <EmployeesOverview t={t} />
    }
  }, [activeSection, t])

  return (
    <section className="employeesPage">
      <EmployeesSidebar
        t={t}
        collapsed={collapsed}
        activeSection={activeSection}
        onSelect={setActiveSection}
        onToggle={() => setCollapsed((v) => !v)}
        agents={agents}
      />

      <main className="employeesContent">
        <div className="employeesContent__card">{content}</div>
      </main>
    </section>
  )
}
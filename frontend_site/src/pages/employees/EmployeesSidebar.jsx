import {
  ChevronLeft,
  ChevronRight,
  Zap,
  Bot,
  BarChart3,
  PanelsTopLeft,
  FolderKanban,
  Globe,
  FileText,
  Blocks,
 Settings2,
  Sparkles,
} from 'lucide-react'

const infoGroups = [
  {
    key: 'employees.sidebar.myEmployees',
    icon: FolderKanban,
    items: [
      { key: 'employees.sidebar.stats', icon: BarChart3, section: 'stats' },
      { key: 'employees.sidebar.management', icon: PanelsTopLeft, section: 'management' },
    ],
  },
  {
    key: 'employees.sidebar.catalog',
    icon: FileText,
    items: [
      { key: 'employees.sidebar.public', icon: Globe, section: 'public' },
      { key: 'employees.sidebar.published', icon: FileText, section: 'published' },
    ],
  },
]

const bottomLinks = [
  {
    key: 'employees.sidebar.apps',
    icon: Blocks,
    section: 'apps',
  },
  {
    key: 'employees.sidebar.settings',
    icon: Settings2,
    section: 'settings',
  },
]

function SidebarLink({
  collapsed,
  isActive,
  onClick,
  title,
  icon: Icon,
  children,
  className = '',
}) {
  return (
    <button
      type="button"
      className={`employeesSidebar__link ${isActive ? 'isActive' : ''} ${className}`.trim()}
      onClick={onClick}
      title={collapsed ? title : ''}
    >
      <span className="employeesSidebar__iconWrap">
        <Icon size={18} strokeWidth={2.2} />
      </span>

      {!collapsed && <span className="employeesSidebar__linkText">{children}</span>}
    </button>
  )
}

export default function EmployeesSidebar({
  t,
  collapsed,
  activeSection,
  onSelect,
  onToggle,
  agents = [],
}) {
  return (
    <aside className={`employeesSidebar ${collapsed ? 'isCollapsed' : ''}`}>
      <div className="employeesSidebar__shell">
        <div className="employeesSidebar__top">
          <section className="employeesSidebar__groupCard employeesSidebar__groupCard--grow">
            <div
              className="employeesSidebar__groupTitle"
              title={collapsed ? t('employees.sidebar.quickAccess') : ''}
            >
              <span className="employeesSidebar__iconWrap">
                <Zap size={18} strokeWidth={2.2} />
              </span>

              {!collapsed && (
                <span className="employeesSidebar__groupText">
                  {t('employees.sidebar.quickAccess')}
                </span>
              )}
            </div>

            <div className="employeesSidebar__groupItems">
              <SidebarLink
                collapsed={collapsed}
                isActive={activeSection === 'free-agents'}
                onClick={() => onSelect('free-agents')}
                title={t('employees.sidebar.freeAgents')}
                icon={Sparkles}
              >
                {t('employees.sidebar.freeAgents')}
              </SidebarLink>
            </div>

            <div className="employeesSidebar__agentsScroller">
              {agents.map((agent) => {
                const AgentIcon = agent.icon || Bot
                const isActive = activeSection === agent.section

                return (
                  <SidebarLink
                    key={agent.section}
                    collapsed={collapsed}
                    isActive={isActive}
                    onClick={() => onSelect(agent.section)}
                    title={agent.label}
                    icon={AgentIcon}
                    className="employeesSidebar__agentLink"
                  >
                    {agent.label}
                  </SidebarLink>
                )
              })}
            </div>
          </section>

          {infoGroups.map((group) => {
            const GroupIcon = group.icon

            return (
              <section className="employeesSidebar__groupCard" key={group.key}>
                <div
                  className="employeesSidebar__groupTitle"
                  title={collapsed ? t(group.key) : ''}
                >
                  <span className="employeesSidebar__iconWrap">
                    <GroupIcon size={18} strokeWidth={2.2} />
                  </span>

                  {!collapsed && (
                    <span className="employeesSidebar__groupText">{t(group.key)}</span>
                  )}
                </div>

                <div className="employeesSidebar__groupItems">
                  {group.items.map((item) => (
                    <SidebarLink
                      key={item.section}
                      collapsed={collapsed}
                      isActive={activeSection === item.section}
                      onClick={() => onSelect(item.section)}
                      title={t(item.key)}
                      icon={item.icon}
                    >
                      {t(item.key)}
                    </SidebarLink>
                  ))}
                </div>
              </section>
            )
          })}
        </div>

        <div className="employeesSidebar__bottom">
          <section className="employeesSidebar__groupCard">
            <div className="employeesSidebar__groupItems">
              {bottomLinks.map((entry) => (
                <SidebarLink
                  key={entry.section}
                  collapsed={collapsed}
                  isActive={activeSection === entry.section}
                  onClick={() => onSelect(entry.section)}
                  title={t(entry.key)}
                  icon={entry.icon}
                >
                  {t(entry.key)}
                </SidebarLink>
              ))}
            </div>
          </section>

          <section className="employeesSidebar__groupCard employeesSidebar__groupCard--footer">
            <button
              type="button"
              className="employeesSidebar__collapseBtn"
              onClick={onToggle}
              aria-label={
                collapsed
                  ? t('employees.sidebar.expand')
                  : t('employees.sidebar.collapse')
              }
              title={
                collapsed
                  ? t('employees.sidebar.expand')
                  : t('employees.sidebar.collapse')
              }
            >
              <span className="employeesSidebar__iconWrap">
                {collapsed ? (
                  <ChevronRight size={18} strokeWidth={2.2} />
                ) : (
                  <ChevronLeft size={18} strokeWidth={2.2} />
                )}
              </span>

              {!collapsed && (
                <span className="employeesSidebar__linkText">
                  {t('employees.sidebar.collapse')}
                </span>
              )}
            </button>
          </section>
        </div>
      </div>
    </aside>
  )
}
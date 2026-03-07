export default function EmployeesAgentTemplate({ t, titleKey, title }) {
  return (
    <section className="employeesContent__section">
      <h2 className="employeesContent__title">{title || t(titleKey)}</h2>
      <p className="employeesContent__text">{t('employees.placeholders.empty')}</p>
    </section>
  )
}
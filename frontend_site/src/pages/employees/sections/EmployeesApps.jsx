export default function EmployeesApps({ t }) {
  return (
    <section className="employeesContent__section">
      <h2 className="employeesContent__title">{t('employees.sidebar.apps')}</h2>
      <p className="employeesContent__text">{t('employees.placeholders.apps')}</p>
    </section>
  )
}
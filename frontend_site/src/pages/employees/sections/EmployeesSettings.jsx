export default function EmployeesSettings({ t }) {
  return (
    <section className="employeesContent__section">
      <h2 className="employeesContent__title">{t('employees.sidebar.settings')}</h2>
      <p className="employeesContent__text">{t('employees.placeholders.settings')}</p>
    </section>
  )
}
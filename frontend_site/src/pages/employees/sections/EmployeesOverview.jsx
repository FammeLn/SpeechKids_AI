export default function EmployeesOverview({ t }) {
  return (
    <section className="employeesContent__section">
      <h1 className="employeesContent__title">{t('employees.title')}</h1>
      <p className="employeesContent__text">{t('employees.placeholders.overview')}</p>
    </section>
  )
}
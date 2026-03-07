export default function EmployeesPublished({ t }) {
  return (
    <section className="employeesContent__section">
      <h2 className="employeesContent__title">{t('employees.sidebar.published')}</h2>
      <p className="employeesContent__text">{t('employees.placeholders.published')}</p>
    </section>
  )
}
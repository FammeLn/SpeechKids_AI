export default function EmployeesPublicCatalog({ t }) {
  return (
    <section className="employeesContent__section">
      <h2 className="employeesContent__title">{t('employees.sidebar.public')}</h2>
      <p className="employeesContent__text">{t('employees.placeholders.public')}</p>
    </section>
  )
}
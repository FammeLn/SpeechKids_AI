export default function EmployeesManagement({ t }) {
  return (
    <section className="employeesContent__section">
      <h2 className="employeesContent__title">{t('employees.sidebar.management')}</h2>
      <p className="employeesContent__text">{t('employees.placeholders.management')}</p>
    </section>
  )
}
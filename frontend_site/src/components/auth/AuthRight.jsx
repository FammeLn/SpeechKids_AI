import { FileText } from 'lucide-react'
import { useLocation, useNavigate } from 'react-router-dom'

export default function AuthRight({ t, active }) {
  const navigate = useNavigate()
  const location = useLocation()

  /**
   * Всегда сохраняем backgroundLocation,
   * чтобы фон не терялся при переключении форм
   */
  const go = (path) => {
    const bg = location.state?.backgroundLocation || location

    navigate(path, {
      replace: true, // не раздуваем историю
      state: {
        backgroundLocation: bg,
      },
    })
  }

  return (
    <div className="authRight">
      {/* CARD 1 */}
      <div className="authRightCard authRightCard--switch">
        <div className="authSwitch">
          <button
            type="button"
            className="authSwitchBtn isLogin"
            disabled={active === 'login'}
            onClick={() => go('/login')}
          >
            {t('auth.login')}
          </button>

          <button
            type="button"
            className="authSwitchBtn isRegister"
            disabled={active === 'register'}
            onClick={() => go('/register')}
          >
            {t('auth.register')}
          </button>

          <button
            type="button"
            className="authSwitchBtn isRecover"
            disabled={active === 'recover'}
            onClick={() => go('/forgot-password')}
          >
            {t('auth.forgot')}
          </button>
        </div>
      </div>

      {/* CARD 2 */}
      <div className="authRightCard authRightCard--docs">
        <div className="authDocs">
          <div className="authDocsTitle">{t('auth.docsTitle')}</div>

          <ul className="authDocsList">
            <li>
              <button
                type="button"
                className="linkBtn"
                onClick={() => alert(t('auth.userAgreement'))}
              >
                <FileText size={16} className="docIco" />
                <span>{t('auth.userAgreement')}</span>
              </button>
            </li>

            <li>
              <button
                type="button"
                className="linkBtn"
                onClick={() => alert(t('auth.privacyPolicy'))}
              >
                <FileText size={16} className="docIco" />
                <span>{t('auth.privacyPolicy')}</span>
              </button>
            </li>

            <li>
              <button
                type="button"
                className="linkBtn"
                onClick={() => alert(t('auth.promosTerms'))}
              >
                <FileText size={16} className="docIco" />
                <span>{t('auth.promosTerms')}</span>
              </button>
            </li>
          </ul>

          <p className="muted micro">{t('auth.docsStub')}</p>
        </div>
      </div>
    </div>
  )
}
import { useEffect, useMemo, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { Mail, Lock, CheckCircle2, AlertTriangle, Info } from 'lucide-react'
import AuthRight from '../../components/auth/AuthRight'

function isValidEmailFormat(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/i.test(email.trim())
}

export default function Login({ t = (k) => k } = {}) {
  const location = useLocation()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [triedSubmit, setTriedSubmit] = useState(false)

  const [authError, setAuthError] = useState(null) // 'email'|'password'|'credentials'|'network'|null

  useEffect(() => {
    const st = location.state
    if (!st) return

    if (typeof st.prefillEmail === 'string') setEmail(st.prefillEmail)
    if (typeof st.prefillPassword === 'string') setPassword(st.prefillPassword)
    if (st.authError) setAuthError(st.authError)

    if (st.authError) setTriedSubmit(true)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [location.key])

  const emailErr = useMemo(() => {
    if (!email.trim()) return 'required'
    if (!isValidEmailFormat(email)) return 'invalid'
    return null
  }, [email])

  const passErr = useMemo(() => {
    if (!password) return 'required'
    return null
  }, [password])

  const ok = !emailErr && !passErr

  const onSubmit = (e) => {
    e.preventDefault()

    if (!ok) {
      setTriedSubmit(true)
      const first = document.querySelector('.input--warn')
      first?.scrollIntoView?.({ behavior: 'smooth', block: 'center' })
      first?.focus?.()
      return
    }

    alert(t('auth.loginStub'))
  }

  const showWarn = triedSubmit && (!ok || !!authError)

  const warnEmail = showWarn && (!!emailErr || authError === 'email' || authError === 'credentials')
  const warnPass = showWarn && (!!passErr || authError === 'password' || authError === 'credentials')

  const IconOk = CheckCircle2
  const IconErr = AlertTriangle
  const IconReq = Info

  const authErrMsg = useMemo(() => {
    if (!authError) return ''
    if (authError === 'credentials') return t('auth.badCredentials')
    if (authError === 'email') return t('auth.emailInvalid')
    if (authError === 'password') return t('auth.passwordInvalid')
    if (authError === 'network') return t('auth.networkError')
    return ''
  }, [authError, t])

  const emailHeader = useMemo(() => {
    if (warnEmail && authErrMsg) return { state: 'err', icon: IconErr, msg: authErrMsg }
    if (!email.trim()) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    if (emailErr === 'invalid') return { state: 'err', icon: IconErr, msg: t('auth.emailInvalid') }
    return { state: 'ok', icon: IconOk, msg: '' }
  }, [email, emailErr, t, warnEmail, authErrMsg])

  const passHeader = useMemo(() => {
    if (warnPass && authErrMsg) return { state: 'err', icon: IconErr, msg: authErrMsg }
    if (!password) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    return { state: 'ok', icon: IconOk, msg: '' }
  }, [password, t, warnPass, authErrMsg])

  function FieldLabel({ icon: Ico, text, state }) {
    return (
      <div className="fLabelLeft">
        {Ico && (
          <Ico
            size={16}
            className={['fIco', state === 'ok' ? 'isOk' : state === 'err' ? 'isErr' : 'isIdle'].join(' ')}
          />
        )}
        <span className="fLabelText">{text}</span>
      </div>
    )
  }

  function FieldMeta({ state, msg }) {
    if (!msg) return null
    return <div className={['fMeta', state === 'err' ? 'isErr' : state === 'ok' ? 'isOk' : 'isIdle'].join(' ')}>{msg}</div>
  }

  return (
    <section className="authPage">
      <div className="authShell">
        <div className="authLeft">
          <form className="authFormCard" onSubmit={onSubmit}>
            <div className="authTitleRow">
              <h1 className="authTitle">{t('auth.loginTitle')}</h1>
              <p className="authSubtitle muted">{t('auth.loginSubtitle')}</p>
            </div>

            <div className="field">
              <div className="fHead">
                <FieldLabel icon={Mail} text={t('auth.email')} state={emailHeader.state} />
                <FieldMeta state={emailHeader.state} msg={emailHeader.msg} />
              </div>

              <input
                className={['input', warnEmail ? 'input--warn' : ''].join(' ')}
                type="email"
                value={email}
                placeholder="name@example.com"
                onChange={(e) => {
                  if (triedSubmit) setTriedSubmit(false)
                  if (authError) setAuthError(null)
                  setEmail(e.target.value)
                }}
                autoComplete="email"
                required
              />
            </div>

            <div className="field">
              <div className="fHead">
                <FieldLabel icon={Lock} text={t('auth.password')} state={passHeader.state} />
                <FieldMeta state={passHeader.state} msg={passHeader.msg} />
              </div>

              <input
                className={['input', warnPass ? 'input--warn' : ''].join(' ')}
                type="password"
                value={password}
                onChange={(e) => {
                  if (triedSubmit) setTriedSubmit(false)
                  if (authError) setAuthError(null)
                  setPassword(e.target.value)
                }}
                autoComplete="current-password"
                required
              />
            </div>

            <div className="authActions">
              <button type="submit" className={['btn', ok ? 'btn--accent' : 'btn--disabled'].join(' ')} aria-disabled={!ok}>
                {t('auth.loginBtn')}
              </button>
            </div>
          </form>
        </div>

        <AuthRight t={t} active="login" />
      </div>
    </section>
  )
}
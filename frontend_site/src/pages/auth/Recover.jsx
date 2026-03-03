import { useMemo, useState } from 'react'
import { Mail, CheckCircle2, AlertTriangle, Info } from 'lucide-react'
import AuthRight from '../../components/auth/AuthRight'

function isValidEmailFormat(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/i.test(email.trim())
}

export default function Recover({ t = (k) => k } = {}) {
  const [email, setEmail] = useState('')
  const [status, setStatus] = useState('idle') // idle | sending | sent
  const [triedSubmit, setTriedSubmit] = useState(false)

  const emailErr = useMemo(() => {
    if (!email.trim()) return 'required'
    if (!isValidEmailFormat(email)) return 'invalid'
    return null
  }, [email])

  const ok = !emailErr

  const onSubmit = (e) => {
    e.preventDefault()

    if (!ok) {
      setTriedSubmit(true)
      const first = document.querySelector('.input--warn')
      first?.scrollIntoView?.({ behavior: 'smooth', block: 'center' })
      first?.focus?.()
      return
    }

    setStatus('sending')
    setTimeout(() => setStatus('sent'), 450)
  }

  const showWarn = triedSubmit && !ok
  const warnEmail = showWarn && !!emailErr

  const IconOk = CheckCircle2
  const IconErr = AlertTriangle
  const IconReq = Info

  const emailHeader = useMemo(() => {
    if (!email.trim()) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    if (emailErr === 'invalid') return { state: 'err', icon: IconErr, msg: t('auth.emailInvalid') }
    return { state: 'ok', icon: IconOk, msg: '' }
  }, [email, emailErr, t])

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
              <h1 className="authTitle">{t('auth.recoverTitle')}</h1>
              <p className="authSubtitle muted">{t('auth.recoverSubtitle')}</p>
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
                  setEmail(e.target.value)
                }}
                autoComplete="email"
                required
              />
            </div>

            <div className="authActions">
              <button
                type="submit"
                className={['btn', ok ? 'btn--accent' : 'btn--disabled'].join(' ')}
                aria-disabled={!ok || status === 'sending'}
              >
                {status === 'sending' ? t('auth.sending') : t('auth.recoverBtn')}
              </button>

              {status === 'sent' && <div className="hint hint--ok">{t('auth.recoverSent')}</div>}
            </div>
          </form>
        </div>

        <AuthRight t={t} active="recover" />
      </div>
    </section>
  )
}
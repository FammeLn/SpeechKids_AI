import { useEffect, useMemo, useRef, useState } from 'react'
import { CheckCircle2, AlertTriangle, Info, Mail, Lock, User, KeyRound } from 'lucide-react'
import AuthRight from '../../components/auth/AuthRight'

// --- helpers ---
function isValidEmailFormat(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/i.test(email.trim())
}

function nicknameFromEmail(email) {
  const at = email.indexOf('@')
  if (at <= 0) return ''
  return email.slice(0, at).replace(/[^\p{L}\p{N}_-]/gu, '')
}

export default function Register({ t = (k) => k } = {}) {
  // ---- REGISTER STATE ----
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [password2, setPassword2] = useState('')
  const [nickName, setNickName] = useState('')

  const [agree, setAgree] = useState(false)
  const [promo, setPromo] = useState(false)

  const [emailCheck, setEmailCheck] = useState('idle') // idle|checking|invalid|ok
  const [emailConfirmed, setEmailConfirmed] = useState(false)

  // code ui
  const [codeSent, setCodeSent] = useState(false)
  const [verifyCode, setVerifyCode] = useState('')
  const [sendStatus, setSendStatus] = useState('idle') // idle|sending|sent
  const [triedSubmit, setTriedSubmit] = useState(false)

  // resend
  const [resendStep, setResendStep] = useState(0) // 0->15, 1->30, 2->60
  const [resendLeft, setResendLeft] = useState(0)
  const resendTimerRef = useRef(null)

  const nickTouchedRef = useRef(false)
  const debounceRef = useRef(null)

  // reset confirm on email change
  useEffect(() => {
    setEmailConfirmed(false)
    setCodeSent(false)
    setVerifyCode('')
    setSendStatus('idle')
    setResendStep(0)
    setResendLeft(0)

    if (resendTimerRef.current) clearInterval(resendTimerRef.current)
    resendTimerRef.current = null
  }, [email])

  // auto nickname from email
  useEffect(() => {
    if (nickTouchedRef.current) return
    const nick = nicknameFromEmail(email)
    if (nick) setNickName(nick)
    if (!email) setNickName('')
  }, [email])

  // email check (format + small "checking" delay)
  useEffect(() => {
    const v = email.trim()
    if (debounceRef.current) clearTimeout(debounceRef.current)

    if (!v) {
      setEmailCheck('idle')
      return
    }
    if (!isValidEmailFormat(v)) {
      setEmailCheck('invalid')
      return
    }

    setEmailCheck('checking')
    debounceRef.current = setTimeout(() => setEmailCheck('ok'), 250)

    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current)
    }
  }, [email])

  // validations
  const nickErr = useMemo(() => {
    const u = nickName.trim()
    if (!u) return 'required'
    if (u.length < 3) return 'min3'
    return null
  }, [nickName])

  const passErr = useMemo(() => {
    if (!password) return 'required'
    if (password.length < 8) return 'min8'
    return null
  }, [password])

  const pass2Err = useMemo(() => {
    if (!password2) return 'required'
    if (password2 !== password) return 'mismatch'
    return null
  }, [password2, password])

  const canSendCode = emailCheck === 'ok' && !emailConfirmed && sendStatus !== 'sending'
  const canVerifyCode = codeSent && !emailConfirmed && verifyCode.trim().length >= 4
  const canResend = codeSent && !emailConfirmed && resendLeft <= 0 && sendStatus !== 'sending'

  const requiredOk =
    emailCheck === 'ok' &&
    emailConfirmed &&
    !passErr &&
    !pass2Err &&
    !nickErr &&
    agree

  // --- send code / resend / countdown ---
  const onSendCode = async () => {
    if (!canSendCode) return
    setSendStatus('sending')

    // TODO (backend): await api.sendEmailCode(email)
    setTimeout(() => {
      setCodeSent(true)
      setSendStatus('sent')
      setResendStep(0)
      setResendLeft(15)
    }, 400)
  }

  const onResend = async () => {
    if (!canResend) return
    setSendStatus('sending')

    // TODO (backend): await api.resendEmailCode(email)
    setTimeout(() => {
      setSendStatus('sent')
      setResendStep((prev) => {
        const nextStep = Math.min(prev + 1, 2)
        const nextDelay = [15, 30, 60][nextStep]
        setResendLeft(nextDelay)
        return nextStep
      })
    }, 400)
  }

  useEffect(() => {
    if (resendLeft <= 0) return

    if (resendTimerRef.current) clearInterval(resendTimerRef.current)

    resendTimerRef.current = setInterval(() => {
      setResendLeft((s) => {
        if (s <= 1) {
          if (resendTimerRef.current) clearInterval(resendTimerRef.current)
          resendTimerRef.current = null
          return 0
        }
        return s - 1
      })
    }, 1000)

    return () => {
      if (resendTimerRef.current) clearInterval(resendTimerRef.current)
    }
  }, [resendLeft])

  const onConfirmEmail = async () => {
    if (!canVerifyCode) return

    // TODO (backend): await api.verifyEmailCode(email, verifyCode)
    if (verifyCode.trim() === '123456') setEmailConfirmed(true)
    else alert(t('auth.invalidCode', { code: '123456' }))
  }

  const onRegister = async (e) => {
    e.preventDefault()

    if (!requiredOk) {
      setTriedSubmit(true)
      const first = document.querySelector('.input--warn')
      first?.scrollIntoView?.({ behavior: 'smooth', block: 'center' })
      first?.focus?.()
      return
    }

    // TODO (backend): await api.register({ email, password, nickName, promo })
    alert(t('auth.registerStub'))
  }

  // warn flags
  const showWarn = triedSubmit && !requiredOk
  const warnEmail = showWarn && (emailCheck !== 'ok' || !emailConfirmed)
  const warnNick = showWarn && !!nickErr
  const warnPass = showWarn && !!passErr
  const warnPass2 = showWarn && !!pass2Err
  const warnAgree = showWarn && !agree

  // headers
  const IconOk = CheckCircle2
  const IconErr = AlertTriangle
  const IconReq = Info

  const emailHeader = useMemo(() => {
    if (!email.trim()) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    if (emailCheck === 'checking') return { state: 'idle', icon: IconReq, msg: t('auth.emailChecking') }
    if (emailCheck === 'invalid') return { state: 'err', icon: IconErr, msg: t('auth.emailInvalid') }
    if (emailCheck === 'ok') return { state: 'ok', icon: IconOk, msg: '' }
    return { state: 'idle', icon: IconReq, msg: '' }
  }, [email, emailCheck, t])

  const nickHeader = useMemo(() => {
    if (!nickName.trim()) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    if (nickErr === 'min3') return { state: 'err', icon: IconErr, msg: t('auth.usernameHint') }
    return { state: 'ok', icon: IconOk, msg: '' }
  }, [nickName, nickErr, t])

  const passHeader = useMemo(() => {
    if (!password) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    if (passErr === 'min8') return { state: 'err', icon: IconErr, msg: t('auth.passwordHint') }
    return { state: 'ok', icon: IconOk, msg: '' }
  }, [password, passErr, t])

  const pass2Header = useMemo(() => {
    if (!password2) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    if (pass2Err === 'mismatch') return { state: 'err', icon: IconErr, msg: t('auth.passwordMismatch') }
    return { state: 'ok', icon: IconOk, msg: '' }
  }, [password2, pass2Err, t])

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

  const yesNo = (v) => (v ? t('common.yes') : t('common.no'))

  return (
      <section className="authPage">
        <div className="authShell">
          <div className="authLeft">
            <form className="authFormCard" onSubmit={onRegister}>
              <div className="authTitleRow">
                <h1 className="authTitle">{t('auth.registerTitle')}</h1>
              </div>

              {/* EMAIL */}
              <div className="field">
                <div className="fHead">
                  <FieldLabel icon={Mail} text={t('auth.email')} state={emailHeader.state} />
                  <FieldMeta state={emailHeader.state} msg={emailHeader.msg} />
                </div>

                <div className="fRow">
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

                {/* EMAIL CODE SLOT (заготовка под бэк) */}
                <div className="emailActions">
                  <div className="codeSlot">
                    <div className="codeSwap">
                      {/* A: send */}
                      <div className={['codeLayer', codeSent ? 'isHidden' : 'isShown'].join(' ')}>
                        <button
                          type="button"
                          className="btn btn--ghost codeSendBtn"
                          onClick={onSendCode}
                          disabled={!canSendCode}
                        >
                          {sendStatus === 'sending' ? t('auth.emailSending') : t('auth.emailSendCode')}
                        </button>
                      </div>

                      {/* B: split */}
                      <div className={['codeLayer', codeSent && !emailConfirmed ? 'isShown' : 'isHidden'].join(' ')}>
                        <div className="codeSplit" aria-label="email-code-split">
                          <input
                            className="input input--code"
                            value={verifyCode}
                            onChange={(e) => setVerifyCode(e.target.value)}
                            placeholder={t('auth.codePlaceholder')}
                            inputMode="numeric"
                            autoComplete="one-time-code"
                          />

                          <button
                            type="button"
                            className="btn btn--accent codeSplit__mid"
                            onClick={onConfirmEmail}
                            disabled={!canVerifyCode}
                          >
                            {t('auth.codeEnter')}
                          </button>

                          <button
                            type="button"
                            className="btn btn--ghost codeSplit__right"
                            onClick={onResend}
                            disabled={!canResend}
                          >
                            {resendLeft > 0 ? t('auth.resendIn', { s: resendLeft }) : t('auth.resend')}
                          </button>
                        </div>
                      </div>

                      {/* C: confirmed */}
                      <div className={['codeLayer', codeSent && emailConfirmed ? 'isShown' : 'isHidden'].join(' ')}>
                        <div className="codeConfirmRow" aria-label="email-confirmed-row">
                          <div className="codeConfirmRow__left">
                            <CheckCircle2 size={18} className="codeConfirmRow__ico" />
                            <span className="codeConfirmRow__text">{t('auth.emailVerified')}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* NICKNAME */}
              <div className="field">
                <div className="fHead">
                  <FieldLabel icon={User} text={t('auth.nickname')} state={nickHeader.state} />
                  <FieldMeta state={nickHeader.state} msg={nickHeader.msg} />
                </div>

                <input
                  className={['input', warnNick ? 'input--warn' : ''].join(' ')}
                  value={nickName}
                  placeholder="nickname"
                  onChange={(e) => {
                    if (triedSubmit) setTriedSubmit(false)
                    nickTouchedRef.current = true
                    setNickName(e.target.value)
                  }}
                  required
                />
              </div>

              {/* PASSWORD */}
              <div className="field">
                <div className="fHead">
                  <FieldLabel icon={Lock} text={t('auth.password')} state={passHeader.state} />
                  <FieldMeta state={passHeader.state} msg={passHeader.msg} />
                </div>

                <input
                  className={['input', warnPass ? 'input--warn' : ''].join(' ')}
                  type="password"
                  value={password}
                  placeholder={t('auth.passwordHint')}
                  onChange={(e) => {
                    if (triedSubmit) setTriedSubmit(false)
                    setPassword(e.target.value)
                  }}
                  autoComplete="new-password"
                  required
                />
              </div>

              {/* CONFIRM */}
              <div className="field">
                <div className="fHead">
                  <FieldLabel icon={KeyRound} text={t('auth.confirmPassword')} state={pass2Header.state} />
                  <FieldMeta state={pass2Header.state} msg={pass2Header.msg} />
                </div>

                <input
                  className={['input', warnPass2 ? 'input--warn' : ''].join(' ')}
                  type="password"
                  value={password2}
                  onChange={(e) => {
                    if (triedSubmit) setTriedSubmit(false)
                    setPassword2(e.target.value)
                  }}
                  autoComplete="new-password"
                  required
                />
              </div>

              {/* TOGGLES */}
              <div className="authToggles">
                <div className="toggleRow">
                  <div className="toggleLabel">
                    <Info size={16} className="toggleIco" />
                    <span>
                      {t('auth.acceptTos')} <span className="reqDot">•</span>
                    </span>
                  </div>

                  <button
                    type="button"
                    className={['toggleBtn', agree ? 'on' : '', warnAgree ? 'toggleBtn--warn' : ''].join(' ')}
                    onClick={() => {
                      if (triedSubmit) setTriedSubmit(false)
                      setAgree((v) => !v)
                    }}
                    aria-pressed={agree}
                  >
                    {yesNo(agree)}
                  </button>
                </div>

                <div className="toggleRow">
                  <div className="toggleLabel">
                    <Mail size={16} className="toggleIco" />
                    <span>{t('auth.promos')}</span>
                  </div>

                  <button
                    type="button"
                    className={['toggleBtn', promo ? 'on' : ''].join(' ')}
                    onClick={() => setPromo((v) => !v)}
                    aria-pressed={promo}
                  >
                    {yesNo(promo)}
                  </button>
                </div>
              </div>

              <div className="authActions">
                {/* важно: не disabled, чтобы работала submit-warn логика */}
                <button
                  type="submit"
                  className={['btn', requiredOk ? 'btn--accent' : 'btn--disabled'].join(' ')}
                  aria-disabled={!requiredOk}
                >
                  {t('auth.submit')}
                </button>
              </div>
            </form>
          </div>

          <AuthRight t={t} active="register" />
        </div>
      </section>
  )
}
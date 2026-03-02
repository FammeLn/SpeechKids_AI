import { useEffect, useMemo, useRef, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import {
  CheckCircle2,
  AlertTriangle,
  Info,
  Mail,
  Lock,
  User,
  KeyRound,
  FileText,
} from 'lucide-react'

// --- helpers ---
function isValidEmailFormat(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/i.test(email.trim())
}

function nicknameFromEmail(email) {
  const at = email.indexOf('@')
  if (at <= 0) return ''
  return email.slice(0, at).replace(/[^\p{L}\p{N}_-]/gu, '')
}

// localStorage mock taken emails
const LS_TAKEN_EMAILS = 'halfi_taken_emails_v1'

function getTakenEmailsSet() {
  try {
    const raw = localStorage.getItem(LS_TAKEN_EMAILS)
    if (!raw) {
      const seed = ['test@halfi.ai', 'admin@halfi.ai', 'demo@halfi.ai']
      localStorage.setItem(LS_TAKEN_EMAILS, JSON.stringify(seed))
      return new Set(seed)
    }
    const arr = JSON.parse(raw)
    return new Set(Array.isArray(arr) ? arr : [])
  } catch {
    return new Set()
  }
}

function addTakenEmail(email) {
  const e = email.trim().toLowerCase()
  const set = getTakenEmailsSet()
  set.add(e)
  localStorage.setItem(LS_TAKEN_EMAILS, JSON.stringify(Array.from(set)))
}

function modeFromUrl(location) {
  const p = location.pathname
  const sp = new URLSearchParams(location.search)
  const q = sp.get('mode')

  if (q === 'login' || q === 'register' || q === 'recover') return q
  if (p.startsWith('/login')) return 'login'
  if (p.startsWith('/register')) return 'register'
  if (p.startsWith('/forgot-password')) return 'recover'
  return 'login'
}

export default function Auth({ t = (k) => k } = {}) {
  const nav = useNavigate()
  const location = useLocation()

  const [mode, setMode] = useState(() => modeFromUrl(location))

  // sync when url changes (back/forward, direct links)
  useEffect(() => {
    setMode(modeFromUrl(location))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [location.pathname, location.search])

  const go = (next) => {
    if (next === mode) return
    if (next === 'login') nav('/login')
    if (next === 'register') nav('/register')
    if (next === 'recover') nav('/forgot-password')
  }

  // ---------------------------
  // REGISTER STATE
  // ---------------------------
  const [rEmail, setREmail] = useState('')
  const [rPassword, setRPassword] = useState('')
  const [rPassword2, setRPassword2] = useState('')
  const [rUsername, setRUsername] = useState('')
  const [rAgree, setRAgree] = useState(false)
  const [rPromo, setRPromo] = useState(false)

  const [rEmailCheck, setREmailCheck] = useState('idle') // idle|checking|invalid|taken|ok
  const [rEmailConfirmed, setREmailConfirmed] = useState(false)

  const [rCodeSent, setRCodeSent] = useState(false)
  const [rVerifyCode, setRVerifyCode] = useState('')
  const [rSendStatus, setRSendStatus] = useState('idle') // idle|sending|sent
  const [rTriedSubmit, setRTriedSubmit] = useState(false)

  const [rResendStep, setRResendStep] = useState(0) // 0->15, 1->30, 2->60
  const [rResendLeft, setRResendLeft] = useState(0) // seconds countdown
  const rResendTimerRef = useRef(null)

  const resendDelay = useMemo(() => {
    return [15, 30, 60][Math.min(rResendStep, 2)]
  }, [rResendStep])

  const rCanResend = rCodeSent && !rEmailConfirmed && rResendLeft <= 0 && rSendStatus !== 'sending'

  const rUsernameTouchedRef = useRef(false)
  const rDebounceRef = useRef(null)

  // reset confirmation on email change
  useEffect(() => {
    setREmailConfirmed(false)
    setRCodeSent(false)
    setRVerifyCode('')
    setRSendStatus('idle')
    setRResendStep(0)
    setRResendLeft(0)
    if (rResendTimerRef.current) clearInterval(rResendTimerRef.current)
    rResendTimerRef.current = null
  }, [rEmail])

  // auto username
  useEffect(() => {
    if (rUsernameTouchedRef.current) return
    const nick = nicknameFromEmail(rEmail)
    if (nick) setRUsername(nick)
    if (!rEmail) setRUsername('')
  }, [rEmail])

  // email check
  useEffect(() => {
    const v = rEmail.trim()
    if (rDebounceRef.current) clearTimeout(rDebounceRef.current)

    if (!v) {
      setREmailCheck('idle')
      return
    }

    if (!isValidEmailFormat(v)) {
      setREmailCheck('invalid')
      return
    }

    setREmailCheck('checking')
    rDebounceRef.current = setTimeout(() => {
      const taken = getTakenEmailsSet()
      setREmailCheck(taken.has(v.toLowerCase()) ? 'taken' : 'ok')
    }, 450)

    return () => {
      if (rDebounceRef.current) clearTimeout(rDebounceRef.current)
    }
  }, [rEmail])

  const rUsernameErr = useMemo(() => {
    const u = rUsername.trim()
    if (!u) return 'required'
    if (u.length < 3) return 'min3'
    return null
  }, [rUsername])

  const rPassErr = useMemo(() => {
    if (!rPassword) return 'required'
    if (rPassword.length < 8) return 'min8'
    return null
  }, [rPassword])

  const rPass2Err = useMemo(() => {
    if (!rPassword2) return 'required'
    if (rPassword2 !== rPassword) return 'mismatch'
    return null
  }, [rPassword2, rPassword])

  const rCanSendCode = rEmailCheck === 'ok' && !rEmailConfirmed && rSendStatus !== 'sending'
  const rCanVerifyCode = rCodeSent && !rEmailConfirmed && rVerifyCode.trim().length >= 4

  const rRequiredOk =
    rEmailCheck === 'ok' &&
    rEmailConfirmed &&
    !rPassErr &&
    !rPass2Err &&
    !rUsernameErr &&
    rAgree

  const onRegisterSendCode = () => {
    if (!rCanSendCode) return
    setRSendStatus('sending')

    setTimeout(() => {
      setRCodeSent(true)
      setRSendStatus('sent')

      // стартуем таймер ресенда
      setRResendStep(0)
      setRResendLeft(15)
    }, 600)
  }

  const onRegisterResend = () => {
  if (!rCanResend) return
  setRSendStatus('sending')

  setTimeout(() => {
    setRSendStatus('sent')
    setRResendStep((s) => Math.min(s + 1, 2))
    // новая задержка по шагу (после инкремента)
    setRResendLeft((_) => [15, 30, 60][Math.min(rResendStep + 1, 2)])
  }, 600)
  }

  const onRegisterConfirmEmail = () => {
    if (!rCanVerifyCode) return
    if (rVerifyCode.trim() === '123456') setREmailConfirmed(true)
    else alert(t('auth.invalidCode', { code: '123456' }))
  }

  const onRegister = (e) => {
    e.preventDefault()

    if (!rRequiredOk) {
      setRTriedSubmit(true)

      // опционально: подсказка UX — прыгаем к первому проблемному полю
      const first = document.querySelector('.input--warn')
      first?.scrollIntoView?.({ behavior: 'smooth', block: 'center' })
      first?.focus?.()
      return
    }

    addTakenEmail(rEmail)
    alert(t('auth.registerSuccess'))
    nav('/')
  }

  const rShowWarn = rTriedSubmit && !rRequiredOk

  const rWarnEmail =
    rShowWarn && (rEmailCheck !== 'ok' || !rEmailConfirmed)

  const rWarnUsername =
    rShowWarn && !!rUsernameErr

  const rWarnPass =
    rShowWarn && !!rPassErr

  const rWarnPass2 =
    rShowWarn && !!rPass2Err

  // соглашение тоже обязательно — подсветим сам toggle
  const rWarnAgree =
    rShowWarn && !rAgree

  useEffect(() => {
  if (rResendLeft <= 0) return
  if (rResendTimerRef.current) clearInterval(rResendTimerRef.current)

  rResendTimerRef.current = setInterval(() => {
    setRResendLeft((s) => {
      if (s <= 1) {
        clearInterval(rResendTimerRef.current)
        rResendTimerRef.current = null
        return 0
      }
      return s - 1
    })
  }, 1000)

  return () => {
    if (rResendTimerRef.current) clearInterval(rResendTimerRef.current)
  }
}, [rResendLeft])

  // ---------------------------
  // LOGIN STATE
  // ---------------------------
  const [lEmail, setLEmail] = useState('')
  const [lPassword, setLPassword] = useState('')

  const lEmailErr = useMemo(() => {
    if (!lEmail.trim()) return 'required'
    if (!isValidEmailFormat(lEmail)) return 'invalid'
    return null
  }, [lEmail])

  const lPassErr = useMemo(() => {
    if (!lPassword) return 'required'
    return null
  }, [lPassword])

  const lOk = !lEmailErr && !lPassErr

  const onLogin = (e) => {
    e.preventDefault()
    if (!lOk) return
    alert(t('auth.loginStub'))
  }

  // ---------------------------
  // RECOVER STATE
  // ---------------------------
  const [fEmail, setFEmail] = useState('')
  const [fStatus, setFStatus] = useState('idle') // idle|sending|sent

  const fEmailErr = useMemo(() => {
    if (!fEmail.trim()) return 'required'
    if (!isValidEmailFormat(fEmail)) return 'invalid'
    return null
  }, [fEmail])

  const onRecover = (e) => {
    e.preventDefault()
    if (fEmailErr) return
    setFStatus('sending')
    setTimeout(() => setFStatus('sent'), 600)
  }

  // ---------------------------
  // Field status icons (inline in label row)
  // ---------------------------
  const IconOk = CheckCircle2
  const IconErr = AlertTriangle
  const IconReq = Info

  function FieldLabel({ icon: Ico, text, state }) {
    // state: 'idle' | 'ok' | 'err'
    return (
      <div className="fLabelLeft">
        {Ico && (
          <Ico
            size={16}
            className={[
              'fIco',
              state === 'ok' ? 'isOk' : state === 'err' ? 'isErr' : 'isIdle',
            ].join(' ')}
          />
        )}
        <span className="fLabelText">{text}</span>
      </div>
    )
  }

  function FieldMeta({ state, msg }) {
    if (!msg) return null
    return (
      <div className={['fMeta', state === 'err' ? 'isErr' : state === 'ok' ? 'isOk' : 'isIdle'].join(' ')}>
        {msg}
      </div>
    )
  }

  // ---------------------------
  // REGISTER field header states
  // ---------------------------
  const rEmailHeader = useMemo(() => {
    if (!rEmail.trim())
      return { state: 'idle', icon: IconReq, msg: t('auth.required') }

    if (rEmailCheck === 'checking')
      return { state: 'idle', icon: IconReq, msg: t('auth.emailChecking') }

    if (rEmailCheck === 'invalid')
      return { state: 'err', icon: IconErr, msg: t('auth.emailInvalid') }

    if (rEmailCheck === 'taken')
      return { state: 'err', icon: IconErr, msg: t('auth.emailTaken') }

    if (rEmailCheck === 'ok')
      return { state: 'ok', icon: IconOk, msg: '' } // 👈 убрали позитивный текст

    return { state: 'idle', icon: IconReq, msg: '' }
  }, [rEmail, rEmailCheck, t])

  const rUserHeader = useMemo(() => {
    if (!rUsername.trim()) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    if (rUsernameErr === 'min3') return { state: 'err', icon: IconErr, msg: t('auth.usernameHint') }
    return { state: 'ok', icon: IconOk, msg: '' }
  }, [rUsername, rUsernameErr, t])

  const rPassHeader = useMemo(() => {
    if (!rPassword) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    if (rPassErr === 'min8') return { state: 'err', icon: IconErr, msg: t('auth.passwordHint') }
    return { state: 'ok', icon: IconOk, msg: '' }
  }, [rPassword, rPassErr, t])

  const rPass2Header = useMemo(() => {
    if (!rPassword2) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    if (rPass2Err === 'mismatch') return { state: 'err', icon: IconErr, msg: t('auth.passwordMismatch') }
    return { state: 'ok', icon: IconOk, msg: '' }
  }, [rPassword2, rPass2Err, t])

  // login headers
  const lEmailHeader = useMemo(() => {
    if (!lEmail.trim()) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    if (lEmailErr === 'invalid') return { state: 'err', icon: IconErr, msg: t('auth.emailInvalid') }
    return { state: 'ok', icon: IconOk, msg: '' }
  }, [lEmail, lEmailErr, t])

  const lPassHeader = useMemo(() => {
    if (!lPassword) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    return { state: 'ok', icon: IconOk, msg: '' }
  }, [lPassword, t])

  // recover headers
  const fEmailHeader = useMemo(() => {
    if (!fEmail.trim()) return { state: 'idle', icon: IconReq, msg: t('auth.required') }
    if (fEmailErr === 'invalid') return { state: 'err', icon: IconErr, msg: t('auth.emailInvalid') }
    return { state: 'ok', icon: IconOk, msg: '' }
  }, [fEmail, fEmailErr, t])

  // ---------------------------
  // Right buttons styles: login filled accent, register outlined, recover neutral
  // ---------------------------
  const rightButtons = (
    <>
      {/* CARD 1: switch buttons */}
      <div className="authRightCard authRightCard--switch">
        <div className="authSwitch">
          <button
            type="button"
            className="authSwitchBtn isLogin"
            disabled={mode === 'login'}
            onClick={() => go('login')}
          >
            {t('auth.login')}
          </button>

          <button
            type="button"
            className="authSwitchBtn isRegister"
            disabled={mode === 'register'}
            onClick={() => go('register')}
          >
            {t('auth.register')}
          </button>

          <button
            type="button"
            className="authSwitchBtn isRecover"
            disabled={mode === 'recover'}
            onClick={() => go('recover')}
          >
            {t('auth.forgot')}
          </button>
        </div>
      </div>

      {/* CARD 2: docs (stretches) */}
      <div className="authRightCard authRightCard--docs">
        <div className="authDocs">
          <div className="authDocsTitle">{t('auth.docsTitle')}</div>
          <ul className="authDocsList">
            <li>
              <button type="button" className="linkBtn" onClick={() => alert(t('auth.userAgreement'))}>
                <FileText size={16} className="docIco" />
                <span>{t('auth.userAgreement')}</span>
              </button>
            </li>
            <li>
              <button type="button" className="linkBtn" onClick={() => alert(t('auth.privacyPolicy'))}>
                <FileText size={16} className="docIco" />
                <span>{t('auth.privacyPolicy')}</span>
              </button>
            </li>
            <li>
              <button type="button" className="linkBtn" onClick={() => alert(t('auth.promosTerms'))}>
                <FileText size={16} className="docIco" />
                <span>{t('auth.promosTerms')}</span>
              </button>
            </li>
          </ul>

          <p className="muted micro">{t('auth.docsStub')}</p>
        </div>
      </div>
    </>
  )

  // ---------------------------
  // Checkbox → settings-like toggles
  // ---------------------------
  const yesNo = (v) => (v ? t('common.yes') : t('common.no'))

  return (
    <section className="authPage">
      <div className="authShell">
        {/* LEFT: form */}
        <div className="authLeft">
          {mode === 'register' && (
            <form className="authFormCard" onSubmit={onRegister}>
              <div className="authTitleRow">
                <h1 className="authTitle">{t('auth.registerTitle')}</h1>
              </div>

              {/* Email */}
              <div className="field">
                <div className="fHead">
                  <FieldLabel icon={Mail} text={t('auth.email')} state={rEmailHeader.state} />
                  <FieldMeta state={rEmailHeader.state} msg={rEmailHeader.msg} />
                </div>

                <div className="fRow">
                  <input
                    className={['input', rWarnEmail ? 'input--warn' : ''].join(' ')}
                    type="email"
                    value={rEmail}
                    placeholder="name@example.com"
                    onChange={(e) => setREmail(e.target.value)}
                    autoComplete="email"
                    required
                  />
                </div>

                <div className="emailActions">
                  <div className="codeSlot">
                    <div className="codeSwap">
                      {/* LAYER A: send button */}
                      <div className={['codeLayer', rCodeSent ? 'isHidden' : 'isShown'].join(' ')}>
                        <button
                          type="button"
                          className="btn btn--ghost codeSendBtn"
                          onClick={onRegisterSendCode}
                          disabled={!rCanSendCode}
                        >
                          {rSendStatus === 'sending'
                            ? t('auth.emailSending')
                            : t('auth.emailSendCode')}
                        </button>
                      </div>

                      {/* LAYER B: split (input + enter + resend) */}
                      <div className={['codeLayer', rCodeSent && !rEmailConfirmed ? 'isShown' : 'isHidden'].join(' ')}>
                        <div className="codeSplit" aria-label="email-code-split">
                          <input
                            className="input input--code"
                            value={rVerifyCode}
                            onChange={(e) => setRVerifyCode(e.target.value)}
                            placeholder={t('auth.codePlaceholder')}
                            inputMode="numeric"
                            autoComplete="one-time-code"
                          />

                          <button
                            type="button"
                            className="btn btn--accent codeSplit__mid"
                            onClick={onRegisterConfirmEmail}
                            disabled={!rCanVerifyCode}
                          >
                            {t('auth.codeEnter')}
                          </button>

                          <button
                            type="button"
                            className="btn btn--ghost codeSplit__right"
                            onClick={onRegisterResend}
                            disabled={!rCanResend}
                          >
                            {rResendLeft > 0
                              ? t('auth.resendIn', { s: rResendLeft })
                              : t('auth.resend')}
                          </button>
                        </div>
                      </div>

                      {/* LAYER C: confirmed */}
                      <div className={['codeLayer', rCodeSent && rEmailConfirmed ? 'isShown' : 'isHidden'].join(' ')}>
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

              {/* Username */}
              <div className="field">
                <div className="fHead">
                  <FieldLabel icon={User} text={t('auth.username')} state={rUserHeader.state} />
                  <FieldMeta state={rUserHeader.state} msg={rUserHeader.msg} />
                </div>

                <input
                  className={['input', rWarnUsername ? 'input--warn' : ''].join(' ')}
                  value={rUsername}
                  placeholder="nickname"
                  onChange={(e) => {
                    rUsernameTouchedRef.current = true
                    setRUsername(e.target.value)
                  }}
                  required
                />
              </div>

              {/* Password */}
              <div className="field">
                <div className="fHead">
                  <FieldLabel icon={Lock} text={t('auth.password')} state={rPassHeader.state} />
                  <FieldMeta state={rPassHeader.state} msg={rPassHeader.msg} />
                </div>

                <input
                  className={['input', rWarnPass ? 'input--warn' : ''].join(' ')}
                  type="password"
                  value={rPassword}
                  placeholder={t('auth.passwordHint')}
                  onChange={(e) => setRPassword(e.target.value)}
                  autoComplete="new-password"
                  required
                />
              </div>

              {/* Confirm */}
              <div className="field">
                <div className="fHead">
                  <FieldLabel icon={KeyRound} text={t('auth.confirmPassword')} state={rPass2Header.state} />
                  <FieldMeta state={rPass2Header.state} msg={rPass2Header.msg} />
                </div>

                <input
                  className={['input', rWarnPass2 ? 'input--warn' : ''].join(' ')}
                  type="password"
                  value={rPassword2}
                  onChange={(e) => setRPassword2(e.target.value)}
                  autoComplete="new-password"
                  required
                />
              </div>

              {/* Toggles */}
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
                    className={['toggleBtn', rAgree ? 'on' : '', rWarnAgree ? 'toggleBtn--warn' : ''].join(' ')}
                    onClick={() => {
                      setRTriedSubmit(false) // убираем “режим ругани” после действия пользователя (по желанию)
                      setRAgree((v) => !v)
                    }}
                    aria-pressed={rAgree}
                  >
                    {yesNo(rAgree)}
                  </button>
                </div>

                <div className="toggleRow">
                  <div className="toggleLabel">
                    <Mail size={16} className="toggleIco" />
                    <span>{t('auth.promos')}</span>
                  </div>
                  <button
                    type="button"
                    className={['toggleBtn', rPromo ? 'on' : ''].join(' ')}
                    onClick={() => setRPromo((v) => !v)}
                    aria-pressed={rPromo}
                  >
                    {yesNo(rPromo)}
                  </button>
                </div>
              </div>

              <div className="authActions">
                <button
                  type="submit"
                  className={['btn', rRequiredOk ? 'btn--accent' : 'btn--disabled'].join(' ')}
                  aria-disabled={!rRequiredOk}
                >
                  {t('auth.submit')}
                </button>
              </div>
            </form>
          )}

          {mode === 'login' && (
            <form className="authFormCard" onSubmit={onLogin}>
              <div className="authTitleRow">
                <h1 className="authTitle">{t('auth.loginTitle')}</h1>
                <p className="authSubtitle muted">{t('auth.loginSubtitle')}</p>
              </div>

              <div className="field">
                <div className="fHead">
                  <FieldLabel icon={Mail} text={t('auth.email')} state={lEmailHeader.state} />
                  <FieldMeta state={lEmailHeader.state} msg={lEmailHeader.msg} />
                </div>
                <input
                  className="input"
                  type="email"
                  value={lEmail}
                  placeholder="name@example.com"
                  onChange={(e) => setLEmail(e.target.value)}
                  autoComplete="email"
                  required
                />
              </div>

              <div className="field">
                <div className="fHead">
                  <FieldLabel icon={Lock} text={t('auth.password')} state={lPassHeader.state} />
                  <FieldMeta state={lPassHeader.state} msg={lPassHeader.msg} />
                </div>
                <input
                  className="input"
                  type="password"
                  value={lPassword}
                  onChange={(e) => setLPassword(e.target.value)}
                  autoComplete="current-password"
                  required
                />
              </div>

              <div className="authActions">
                <button
                  type="submit"
                  className={['btn', lOk ? 'btn--accent' : 'btn--disabled'].join(' ')}
                  disabled={!lOk}
                >
                  {t('auth.loginBtn')}
                </button>
              </div>
            </form>
          )}

          {mode === 'recover' && (
            <form className="authFormCard" onSubmit={onRecover}>
              <div className="authTitleRow">
                <h1 className="authTitle">{t('auth.recoverTitle')}</h1>
                <p className="authSubtitle muted">{t('auth.recoverSubtitle')}</p>
              </div>

              <div className="field">
                <div className="fHead">
                  <FieldLabel icon={Mail} text={t('auth.email')} state={fEmailHeader.state} />
                  <FieldMeta state={fEmailHeader.state} msg={fEmailHeader.msg} />
                </div>
                <input
                  className="input"
                  type="email"
                  value={fEmail}
                  placeholder="name@example.com"
                  onChange={(e) => setFEmail(e.target.value)}
                  autoComplete="email"
                  required
                />
              </div>

              <div className="authActions">
                <button
                  type="submit"
                  className={['btn', !fEmailErr ? 'btn--accent' : 'btn--disabled'].join(' ')}
                  disabled={!!fEmailErr || fStatus === 'sending'}
                >
                  {fStatus === 'sending' ? t('auth.sending') : t('auth.recoverBtn')}
                </button>

                {fStatus === 'sent' && (
                  <div className="hint hint--ok">{t('auth.recoverSent')}</div>
                )}
              </div>
            </form>
          )}
        </div>

        {/* RIGHT: fixed/sticky */}
        <div className="authRight">
          {rightButtons}
        </div>
      </div>
    </section>
  )
}
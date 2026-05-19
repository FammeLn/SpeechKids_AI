package com.halfi.notification.service

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(private val mailSender: JavaMailSender) {

    fun sendVerificationEmail(toEmail: String, code: String, type: String = "REGISTRATION") {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")
        
        helper.setTo(toEmail)
        helper.setFrom("no-reply@halfi.com")
        
        val subject: String
        val title: String
        val bodyContent: String
        
        if (type == "RESET_PASSWORD") {
            subject = "Сброс пароля Halfi"
            title = "Восстановление доступа"
            
            // MAGIC LINK (Кликабельная кнопка: DeepLink в мобильное приложение)
            val resetLink = "halfi://reset-password?email=${toEmail}&code=${code}"
            
            bodyContent = """
                <p>Нажмите на кнопку ниже, чтобы безопасно сбросить ваш пароль.</p>
                <br/>
                <a href="${resetLink}" style="display: inline-block; padding: 14px 28px; font-size: 16px; font-weight: bold; color: white !important; background-color: #2563eb; text-decoration: none; border-radius: 8px; box-shadow: 0 4px 6px rgba(37, 99, 235, 0.2);">
                    Сбросить пароль
                </a>
                <br/><br/>
            """.trimIndent()
        } else {
            subject = "Код подтверждения Halfi"
            title = "Добро пожаловать в Halfi!"
            
            bodyContent = """
                <p>Ваш секретный код для активации аккаунта:</p>
                <div style="font-size: 28px; font-weight: bold; padding: 15px; background-color: #f3f4f6; border-radius: 8px; display: inline-block; letter-spacing: 8px; color: #1f2937;">
                    $code
                </div>
            """.trimIndent()
        }
        
        helper.setSubject(subject)

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f9fafb; padding: 40px 0;">
                <div style="max-width: 500px; margin: 0 auto; background-color: white; border-radius: 12px; padding: 30px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); text-align: center;">
                    <h2 style="color: #111827; margin-bottom: 20px;">$title</h2>
                    <div style="color: #4b5563; font-size: 16px; line-height: 1.6;">
                        $bodyContent
                    </div>
                    <div style="margin-top: 35px; padding-top: 20px; border-top: 1px solid #e5e7eb; font-size: 12px; color: #9ca3af;">
                        Если вы не запрашивали это письмо, просто проигнорируйте его. С вашей безопасностью всё в порядке.
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
        
        helper.setText(htmlContent, true) // true означает HTML
        
        mailSender.send(message)
        println("Email sent to $toEmail (Type: $type)")
    }
}

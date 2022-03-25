package dev.foraged.forums.captcha

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
data class CaptchaSettings(var site: String? = null, var secret: String? = null)
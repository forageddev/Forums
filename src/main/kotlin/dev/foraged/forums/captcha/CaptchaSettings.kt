package dev.foraged.forums.captcha

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
data class CaptchaSettings(val site: String? = null, val secret: String? = null)
package dev.foraged.forums.captcha.service

import dev.foraged.forums.captcha.CaptchaSettings
import dev.foraged.forums.captcha.GoogleResponse
import dev.foraged.forums.captcha.exeption.InvalidCaptchaException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.client.RestOperations
import java.net.URI
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest

class CaptchaService : ICaptchaService
{
    @Autowired lateinit var request: HttpServletRequest
    @Autowired lateinit var captchaSettings: CaptchaSettings
    @Autowired lateinit var restTemplate: RestOperations

    @Throws(InvalidCaptchaException::class)
    override fun processResponse(response: String)
    {
        if (!responseSanityCheck(response))
        {
            throw InvalidCaptchaException("Response contains invalid characters")
        }
        val verifyUri = URI.create(
            String.format(
                "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
                captchaSettings.secret, response, clientIP
            )
        )
        val googleResponse = restTemplate!!.getForObject(verifyUri, GoogleResponse::class.java)
        if (!googleResponse.isSuccess)
        {
            throw InvalidCaptchaException("reCaptcha was not successfully validated")
        }
    }

    private fun responseSanityCheck(response: String): Boolean
    {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches()
    }

    private val clientIP: String
        private get()
        {
            val xfHeader = request!!.getHeader("X-Forwarded-For") ?: return request.remoteAddr
            return xfHeader.split(",".toRegex()).toTypedArray()[0]
        }

    companion object
    {
        private val RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+")
    }
}
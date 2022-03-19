package dev.foraged.forums.captcha.service

import dev.foraged.forums.captcha.exeption.InvalidCaptchaException

interface ICaptchaService
{
    @Throws(InvalidCaptchaException::class)
    fun processResponse(response: String)
}
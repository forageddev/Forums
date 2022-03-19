package dev.foraged.forums.captcha

import com.fasterxml.jackson.annotation.*
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("success", "challenge_ts", "hostname", "error-codes")
class GoogleResponse
{
    @JsonProperty("success")
    var isSuccess = false

    @JsonProperty("challenge_ts")
    var challengeTs: String? = null

    @JsonProperty("hostname")
    var hostname: String? = null

    @JsonProperty("error-codes")
    var errorCodes: Array<ErrorCode> = arrayOf()

    enum class ErrorCode
    {
        MissingSecret, InvalidSecret, MissingResponse, InvalidResponse;

        companion object
        {
            private val errorsMap: MutableMap<String, ErrorCode> = HashMap(4)
            @JsonCreator
            fun forValue(value: String): ErrorCode?
            {
                return errorsMap[value.lowercase(Locale.getDefault())]
            }

            init
            {
                errorsMap["missing-input-secret"] = MissingSecret
                errorsMap["invalid-input-secret"] = InvalidSecret
                errorsMap["missing-input-response"] = MissingResponse
                errorsMap["invalid-input-response"] = InvalidResponse
            }
        }
    }

    @JsonIgnore
    fun hasClientError(): Boolean
    {
        val errors = errorCodes ?: return false
        for (error in errors)
        {
            when (error)
            {
                ErrorCode.InvalidResponse, ErrorCode.MissingResponse -> return true
                else ->
                {
                }
            }
        }
        return false
    }

    override fun toString(): String
    {
        return "GoogleResponse{" + "success=" + isSuccess + ", challengeTs='" + challengeTs + '\'' + ", hostname='" + hostname + '\'' + ", errorCodes=" + Arrays.toString(
            errorCodes
        ) + '}'
    }
}
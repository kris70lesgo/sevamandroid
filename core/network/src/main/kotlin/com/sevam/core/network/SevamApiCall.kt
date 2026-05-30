package com.sevam.core.network

import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.IOException

class SevamApiException(
    override val message: String,
    val code: Int? = null,
) : IOException(message)

suspend fun <T> executeApiCall(
    json: Json,
    call: suspend () -> Response<T>,
): Result<T> {
    return runCatching {
        val response = call()
        if (response.isSuccessful) {
            response.body() ?: throw SevamApiException("Empty response body", response.code())
        } else {
            val message = response.errorBody()
                ?.string()
                ?.let { raw ->
                    runCatching { json.decodeFromString(ApiErrorDto.serializer(), raw).error }
                        .getOrNull()
                }
                ?.takeIf { it.isNotBlank() }
                ?: "Request failed with ${response.code()}"
            throw SevamApiException(message, response.code())
        }
    }
}

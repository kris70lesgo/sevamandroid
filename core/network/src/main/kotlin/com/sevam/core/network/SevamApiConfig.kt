package com.sevam.core.network

data class SevamApiConfig(
    val baseUrl: String,
) {
    val isConfigured: Boolean
        get() = baseUrl.isNotBlank()

    fun normalizedBaseUrl(): String {
        val trimmed = baseUrl.trim()
        if (trimmed.isBlank()) return ""
        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }
}

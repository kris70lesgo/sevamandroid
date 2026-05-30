package com.sevam.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

data class StoredSession(
    val phoneNumber: String,
    val accessToken: String,
)

class SessionStore private constructor(
    private val preferences: SharedPreferences,
) {
    fun restore(): StoredSession? {
        val phoneNumber = preferences.getString(KEY_PHONE, null) ?: return null
        val accessToken = preferences.getString(KEY_TOKEN, null) ?: return null
        return StoredSession(
            phoneNumber = phoneNumber,
            accessToken = accessToken,
        )
    }

    fun save(session: StoredSession) {
        preferences.edit {
            putString(KEY_PHONE, session.phoneNumber)
            putString(KEY_TOKEN, session.accessToken)
        }
    }

    fun clear() {
        preferences.edit { clear() }
    }

    companion object {
        private const val FILE_NAME = "sevam_secure_session"
        private const val KEY_PHONE = "phone_number"
        private const val KEY_TOKEN = "access_token"

        fun from(context: Context): SessionStore {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val preferences = EncryptedSharedPreferences.create(
                context,
                FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )

            return SessionStore(preferences)
        }
    }
}

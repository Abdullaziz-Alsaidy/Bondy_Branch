package com.bondy.bondybranch.utility

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 * Centralized SharedPreferences helper for persisting lightweight app data such as auth token.
 */
@Singleton
class PreferenceStorage @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? = prefs.getString(KEY_AUTH_TOKEN, null)

    fun clearAuthToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    fun saveRefreshToken(token: String) {
        prefs.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun clearRefreshToken() {
        prefs.edit().remove(KEY_REFRESH_TOKEN).apply()
    }

    fun saveUserRole(role: String) {
        prefs.edit().putString(KEY_USER_ROLE, role).apply()
    }

    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)

    fun clearUserRole() {
        prefs.edit().remove(KEY_USER_ROLE).apply()
    }

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? =
        prefs.getString(key, defaultValue)

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS_FILE_NAME = "bondy_preferences"
        const val KEY_AUTH_TOKEN = "key_auth_token"
        const val KEY_REFRESH_TOKEN = "key_refresh_token"
        const val KEY_USER_ROLE = "key_user_role"
    }
}

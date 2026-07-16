package com.example.find.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.find.data.api.ApiClient
import com.example.find.data.model.UserRole
import com.example.find.data.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "find_session")

class SessionManager(private val context: Context) {

    private val tokenKey = stringPreferencesKey("token")
    private val userIdKey = stringPreferencesKey("user_id")
    private val emailKey = stringPreferencesKey("email")
    private val roleKey = stringPreferencesKey("role")

    val sessionFlow: Flow<UserSession?> = context.dataStore.data.map { prefs ->
        val token = prefs[tokenKey] ?: return@map null
        val userId = prefs[userIdKey]?.toLongOrNull() ?: return@map null
        val email = prefs[emailKey] ?: return@map null
        val role = prefs[roleKey]?.let { runCatching { UserRole.valueOf(it) }.getOrNull() } ?: return@map null
        UserSession(token, userId, email, role)
    }

    suspend fun saveSession(session: UserSession) {
        ApiClient.setAuthToken(session.token)
        context.dataStore.edit { prefs ->
            prefs[tokenKey] = session.token
            prefs[userIdKey] = session.userId.toString()
            prefs[emailKey] = session.email
            prefs[roleKey] = session.role.name
        }
    }

    suspend fun clearSession() {
        ApiClient.setAuthToken(null)
        context.dataStore.edit { it.clear() }
    }

    suspend fun restoreToken(token: String) {
        ApiClient.setAuthToken(token)
    }
}

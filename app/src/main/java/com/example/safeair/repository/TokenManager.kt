package com.example.safeair.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")
class TokenManager private constructor(context: Context) {
    private val dataStore = context.dataStore
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")

        @Volatile
        private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
        suspend fun saveToken(accessToken: String) {
            dataStore.edit { preferences ->
                preferences[KEY_ACCESS_TOKEN] = accessToken
            }
        }
        val accessToken: Flow<String?> = dataStore.data.map { preferences ->
            preferences[KEY_ACCESS_TOKEN]
        }
        suspend fun getAccessToken(): String? = accessToken.first()

        suspend fun clearTokens() {
            dataStore.edit { preferences ->
                preferences.remove(KEY_ACCESS_TOKEN)

            }
        }

    }



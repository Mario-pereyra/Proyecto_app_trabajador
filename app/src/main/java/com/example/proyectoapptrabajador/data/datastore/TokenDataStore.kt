package com.example.proyectoapptrabajador.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_token_trabajador")

class TokenDataStore(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("access_token")
    }

    val getToken: Flow<String?> = context.dataStore.data.map { preferences ->
        val token = preferences[TOKEN_KEY]
        Log.d("TokenDataStore", "Token obtenido del DataStore: ${if (token != null) "***${token.takeLast(10)}" else "null"}")
        token
    }

    suspend fun saveToken(token: String) {
        Log.d("TokenDataStore", "Guardando token en DataStore: ***${token.takeLast(10)}")
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
        Log.d("TokenDataStore", "Token guardado exitosamente")
    }
}
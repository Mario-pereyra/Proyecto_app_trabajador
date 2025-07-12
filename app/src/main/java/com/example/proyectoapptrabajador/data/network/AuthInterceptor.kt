package com.example.proyectoapptrabajador.data.network

import android.content.Context
import com.example.proyectoapptrabajador.data.datastore.TokenDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val tokenDataStore = TokenDataStore(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenDataStore.getToken().first()
        }
        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}


package com.example.proyectoapptrabajador.data.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val BASE_URL = "http://trabajos.jmacboy.com/api/"

    // La función ahora necesita el Contexto de la aplicación para crear el interceptor
    fun getInstance(context: Context): ApiClient {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context.applicationContext))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiClient::class.java)
    }
}
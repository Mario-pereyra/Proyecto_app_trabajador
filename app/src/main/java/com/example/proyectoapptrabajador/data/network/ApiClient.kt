package com.example.proyectoapptrabajador.data.network

import com.example.proyectoapptrabajador.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiClient {
    @POST("worker/login")
    suspend fun loginWorker(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @Multipart
    @POST("worker/register")
    suspend fun registerWorker(@PartMap params: Map<String, @JvmSuppressWildcards RequestBody>, @Part profilePicture: MultipartBody.Part): Response<WorkerRegisterResponse>

    @GET("appointments")
    suspend fun getAppointments(): Response<List<Cita>>

    @GET("appointments/{id}")
    suspend fun getAppointmentDetails(@Path("id") id: Int): Response<Cita>

    @POST("appointments/{id}/confirm")
    suspend fun confirmAppointment(@Path("id") id: Int): Response<Cita>

    @POST("appointments/{id}/finalize")
    suspend fun finalizeAppointment(@Path("id") id: Int): Response<Cita>

    @GET("appointments/{id}/chats")
    suspend fun getChatMessages(@Path("id") appointmentId: Int): Response<List<MensajeChat>>

    @POST("appointments/{id}/chats")
    suspend fun sendChatMessage(@Path("id") appointmentId: Int, @Body message: MensajeChatRequest): Response<MensajeChat>

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>
}
package com.example.proyectoapptrabajador.data.network

import com.example.proyectoapptrabajador.data.model.CalificarCitaRequest
import com.example.proyectoapptrabajador.data.model.Categoria
import com.example.proyectoapptrabajador.data.model.Cita
import com.example.proyectoapptrabajador.data.model.ConcretarCitaRequest
import com.example.proyectoapptrabajador.data.model.CrearCitaRequest
import com.example.proyectoapptrabajador.data.model.LoginRequest
import com.example.proyectoapptrabajador.data.model.LoginResponse
import com.example.proyectoapptrabajador.data.model.MensajeChat
import com.example.proyectoapptrabajador.data.model.MensajeChatRequest
import com.example.proyectoapptrabajador.data.model.RegisterRequest
import com.example.proyectoapptrabajador.data.model.RegisterResponse
import com.example.proyectoapptrabajador.data.model.Trabajador
import com.example.proyectoapptrabajador.data.model.TrabajadorDetalle
import com.example.proyectoapptrabajador.data.model.UpdateCategoriesRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiClient {

    // --- Autenticación ---
    @POST("client/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("client/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse> // Ahora esperamos el cuerpo de la respuesta

    // --- Categorías ---
    @GET("categories")
    suspend fun getCategories(): Response<List<Categoria>>

    // --- Trabajadores ---
    @GET("categories/{id}/workers")
    suspend fun getWorkersByCategory(@Path("id") categoryId: Int): Response<List<Trabajador>>

    @GET("workers/{id}")
    suspend fun getWorkerDetail(@Path("id") workerId: Int): Response<TrabajadorDetalle>

    // --- Citas (Appointments) ---
    @GET("appointments")
    suspend fun getAppointments(): Response<List<Cita>>

    @POST("appointments")
    suspend fun createAppointment(@Body crearCitaRequest: CrearCitaRequest): Response<Cita>

    @POST("appointments/{id}/make")
    suspend fun makeAppointment(
        @Path("id") appointmentId: Int,
        @Body concretarCitaRequest: ConcretarCitaRequest
    ): Response<Cita>

    @GET("appointments/{id}")
    suspend fun getAppointmentDetails(@Path("id") appointmentId: Int): Response<Cita>

    // --- Chat ---
    @GET("appointments/{id}/chats")
    suspend fun getChatMessages(@Path("id") appointmentId: Int): Response<List<MensajeChat>>

    @POST("appointments/{id}/chats")
    suspend fun sendChatMessage(
        @Path("id") appointmentId: Int,
        @Body mensajeChatRequest: MensajeChatRequest
    ): Response<Unit>

    // --- Calificación ---
    @POST("appointments/{id}/review")
    suspend fun postReview(
        @Path("id") appointmentId: Int,
        @Body calificarCitaRequest: CalificarCitaRequest
    ): Response<Unit>

    // --- MÉTODOS EXISTENTES ---
    // ...existing methods...

    // --- AÑADIR/MODIFICAR PARA EL TRABAJADOR ---
    // Auth Trabajador
    @POST("worker/login")
    suspend fun loginWorker(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("worker/register")
    suspend fun registerWorker(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    // Perfil Trabajador
    @Multipart
    @POST("workers/profile-picture")
    suspend fun uploadProfilePicture(@Part picture: MultipartBody.Part): Response<Unit>

    @POST("workers/{id}/categories")
    suspend fun updateWorkerCategories(@Path("id") workerId: Int, @Body categories: UpdateCategoriesRequest): Response<Unit>

    // Citas (Lado Trabajador)
    @POST("appointments/{id}/confirm")
    suspend fun confirmAppointment(@Path("id") appointmentId: Int): Response<Unit>

    @POST("appointments/{id}/finalize")
    suspend fun finalizeAppointment(@Path("id") appointmentId: Int): Response<Unit>
}

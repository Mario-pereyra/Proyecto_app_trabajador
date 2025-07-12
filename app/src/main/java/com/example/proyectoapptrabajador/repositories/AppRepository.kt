package com.example.proyectoapptrabajador.repositories

import com.example.proyectoapptrabajador.data.datastore.TokenDataStore
import com.example.proyectoapptrabajador.data.network.ApiClient
import com.example.proyectoapptrabajador.data.model.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class AppRepository(
    private val apiClient: ApiClient,
    private val tokenDataStore: TokenDataStore
) {
    // --- Autenticación y Sesión ---
    suspend fun saveToken(token: String) = tokenDataStore.saveToken(token)
    suspend fun clearToken() = tokenDataStore.clearToken()
    fun getToken(): Flow<String?> = tokenDataStore.getToken()

    // --- Categorías ---
    suspend fun getCategories() = apiClient.getCategories()

    // --- Citas (Appointments) ---
    suspend fun getAppointments() = apiClient.getAppointments()
    suspend fun getAppointmentDetails(appointmentId: Int) = apiClient.getAppointmentDetails(appointmentId)

    // --- Chat ---
    suspend fun getChatMessages(appointmentId: Int) = apiClient.getChatMessages(appointmentId)
    suspend fun sendChatMessage(id: Int, request: MensajeChatRequest) = apiClient.sendChatMessage(id, request)

    // --- FUNCIONES PARA EL TRABAJADOR ---
    suspend fun loginWorker(email: String, pass: String): Response<LoginResponse> {
        val request = LoginRequest(email, pass)
        return apiClient.loginWorker(request)
    }

    suspend fun registerWorker(
        params: Map<String, RequestBody>,
        profilePicture: MultipartBody.Part
    ) = apiClient.registerWorker(params, profilePicture)

    suspend fun updateProfilePicture(workerId: Int, picture: MultipartBody.Part) = apiClient.updateProfilePicture(workerId, picture)
    suspend fun updateWorkerCategories(id: Int, request: UpdateCategoriesRequest) = apiClient.updateWorkerCategories(id, request)
    suspend fun confirmAppointment(appointmentId: Int) = apiClient.confirmAppointment(appointmentId)
    suspend fun finalizeAppointment(appointmentId: Int) = apiClient.finalizeAppointment(appointmentId)
}

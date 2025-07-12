package com.example.proyectoapptrabajador.repositories

import com.example.proyectoapptrabajador.data.datastore.TokenDataStore
import com.example.proyectoapptrabajador.data.network.ApiClient
import com.example.proyectoapptrabajador.data.model.CalificarCitaRequest
import com.example.proyectoapptrabajador.data.model.ConcretarCitaRequest
import com.example.proyectoapptrabajador.data.model.CrearCitaRequest
import com.example.proyectoapptrabajador.data.model.LoginRequest
import com.example.proyectoapptrabajador.data.model.MensajeChatRequest
import com.example.proyectoapptrabajador.data.model.RegisterRequest
import com.example.proyectoapptrabajador.data.model.UpdateCategoriesRequest
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

class AppRepository(
    private val apiClient: ApiClient,
    private val tokenDataStore: TokenDataStore
) {
    // --- Autenticación y Sesión ---
    suspend fun login(loginRequest: LoginRequest) = apiClient.login(loginRequest)
    suspend fun register(registerRequest: RegisterRequest) = apiClient.register(registerRequest)
    suspend fun saveToken(token: String) = tokenDataStore.saveToken(token)
    suspend fun clearToken() = tokenDataStore.clearToken()
    fun getToken(): Flow<String?> = tokenDataStore.getToken()

    // --- Categorías ---
    suspend fun getCategories() = apiClient.getCategories()

    // --- Trabajadores ---
    suspend fun getWorkersByCategory(categoryId: Int) = apiClient.getWorkersByCategory(categoryId)
    suspend fun getWorkerDetail(workerId: Int) = apiClient.getWorkerDetail(workerId)

    // --- Citas (Appointments) ---
    suspend fun getAppointments() = apiClient.getAppointments()
    suspend fun createAppointment(request: CrearCitaRequest) = apiClient.createAppointment(request)
    suspend fun makeAppointment(id: Int, request: ConcretarCitaRequest) = apiClient.makeAppointment(id, request)
    suspend fun getAppointmentDetails(appointmentId: Int) = apiClient.getAppointmentDetails(appointmentId)

    // --- Chat ---
    suspend fun getChatMessages(appointmentId: Int) = apiClient.getChatMessages(appointmentId)
    suspend fun sendChatMessage(id: Int, request: MensajeChatRequest) = apiClient.sendChatMessage(id, request)

    // --- Calificación (Review) ---
    suspend fun postReview(id: Int, request: CalificarCitaRequest) = apiClient.postReview(id, request)

    // --- NUEVAS FUNCIONES PARA EL TRABAJADOR ---
    suspend fun loginWorker(loginRequest: LoginRequest) = apiClient.loginWorker(loginRequest)
    suspend fun registerWorker(registerRequest: RegisterRequest) = apiClient.registerWorker(registerRequest)
    suspend fun uploadProfilePicture(picture: MultipartBody.Part) = apiClient.uploadProfilePicture(picture)
    suspend fun updateWorkerCategories(id: Int, request: UpdateCategoriesRequest) = apiClient.updateWorkerCategories(id, request)
    suspend fun confirmAppointment(id: Int) = apiClient.confirmAppointment(id)
    suspend fun finalizeAppointment(id: Int) = apiClient.finalizeAppointment(id)
}

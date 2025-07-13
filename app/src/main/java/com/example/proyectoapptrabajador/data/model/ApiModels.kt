package com.example.proyectoapptrabajador.data.model

import com.google.gson.annotations.SerializedName

// Petición y Respuesta de Login
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val access_token: String)

// Respuesta del Registro del Trabajador
data class WorkerRegisterResponse(val id: Int, val name: String, val email: String, val profile: Profile, val worker: WorkerInfo)
data class Profile(val id: Int, val name: String, val last_name: String, val type: Int)
data class WorkerInfo(val id: Int, val user_id: Int, val picture_url: String?, val average_rating: String?, val reviews_count: Int?)

// Citas (Appointments)
data class Cita(
    val id: Int,
    val status: Int,
    @SerializedName("appointment_date") val appointmentDate: String?,
    @SerializedName("appointment_time") val appointmentTime: String?,
    val latitude: String?,
    val longitude: String?,
    val client: User,
    val category: Category
)
data class User(val id: Int, val name: String, val profile: Profile)
data class Category(val id: Int, val name: String)

// Chat
data class MensajeChat(val id: Int, val sender_id: Int, val message: String)
data class MensajeChatRequest(val message: String, val receiver_id: Int)
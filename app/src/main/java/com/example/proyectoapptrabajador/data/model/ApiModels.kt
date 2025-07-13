package com.example.proyectoapptrabajador.data.model

import com.google.gson.annotations.SerializedName

// Petición y Respuesta de Login
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val access_token: String)

// Respuesta del Registro del Trabajador
data class WorkerRegisterResponse(
    val id: Int,
    val name: String,
    val email: String,
    val profile: Profile,
    val worker: WorkerInfo
)
data class Profile(val id: Int, val name: String, val last_name: String, val type: Int)
data class WorkerInfo(val id: Int, val user_id: Int, val picture_url: String?, val average_rating: String?, val reviews_count: Int?)

// Nuevos modelos para las peticiones secuenciales
data class WorkerRegisterRequest(
    val name: String,
    val lastName: String,
    val email: String,
    val password: String
)

data class WorkerCategoriesRequest(
    val categories: List<CategoryId>
)

data class CategoryId(val id: Int)

data class WorkerCategoriesResponse(
    val id: Int,
    val user_id: Int,
    val picture_url: String?,
    val average_rating: String?,
    val reviews_count: Int,
    val user: User,
    val categories: List<CategoryWithPivot>,
    val reviews: List<Review>
)

data class CategoryWithPivot(
    val id: Int,
    val name: String,
    val pivot: PivotData
)

data class PivotData(
    val worker_id: Int,
    val category_id: Int,
    val created_at: String,
    val updated_at: String
)

data class Review(
    val id: Int,
    val worker_id: Int,
    val user_id: Int,
    val appointment_id: Int,
    val rating: Int,
    val comment: String?,
    val is_done: Int
)

// Citas (Appointments) - Actualizado para coincidir con la respuesta real de la API
data class Cita(
    val id: Int,
    val worker_id: Int,
    val user_id: Int,
    val appointment_date: String?,
    val appointment_time: String?,
    val category_selected_id: Int,
    val latitude: String?,
    val longitude: String?,
    val status: Int,
    val worker: WorkerDetail,
    val category: Category,
    val client: ClientDetail
)

data class WorkerDetail(
    val id: Int,
    val user_id: Int,
    val picture_url: String?,
    val average_rating: String?,
    val reviews_count: Int,
    val user: User
)

data class ClientDetail(
    val id: Int,
    val name: String,
    val email: String,
    val profile: Profile
)

data class User(val id: Int, val name: String, val email: String, val profile: Profile)
data class Category(val id: Int, val name: String)

// Chat
data class MensajeChat(val id: Int, val sender_id: Int, val message: String)
data class MensajeChatRequest(val message: String, val receiver_id: Int)

// Respuesta del endpoint GET /me
data class MeResponse(
    val id: Int,
    val name: String,
    val email: String,
    val profile: Profile,
    val worker: WorkerInfo
)

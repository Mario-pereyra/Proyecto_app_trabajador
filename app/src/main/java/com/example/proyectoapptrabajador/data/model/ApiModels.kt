package com.example.proyectoapptrabajador.data.model

import com.google.gson.annotations.SerializedName

// Petición para /client/login
data class LoginRequest(
    val email: String,
    val password: String
)

// Respuesta de /client/login
data class LoginResponse(
    @SerializedName("access_token") val accessToken: String
)

// Petición para /client/register
data class RegisterRequest(
    val name: String,
    val lastName: String,
    val email: String,
    val password: String
)

// Petición para /appointments (Crear cita inicial)
data class CrearCitaRequest(
    @SerializedName("worker_id") val workerId: String,
    @SerializedName("category_selected_id") val categorySelectedId: Int
)

// Petición para /appointments/{id}/make
data class ConcretarCitaRequest(
    @SerializedName("appointment_date") val appointmentDate: String,
    @SerializedName("appointment_time") val appointmentTime: String,
    val latitude: String,  // Cambiado de Double a String
    val longitude: String  // Cambiado de Double a String
)

// Petición para /appointments/{id}/review
data class CalificarCitaRequest(
    val rating: Int,
    val review: String
)

// Petición para /appointments/{id}/chats
data class MensajeChatRequest(
    val message: String,
    @SerializedName("receiver_id") val receiverId: Int
)

// Modelo para un mensaje individual en la respuesta de /appointments/{id}/chats
data class MensajeChat(
    val id: Int,
    @SerializedName("appointment_id") val appointmentId: Int,
    @SerializedName("sender_id") val senderId: Int,
    @SerializedName("receiver_id") val receiverId: Int,
    @SerializedName("date_sent") val dateSent: String,
    val message: String,
    val appointment: AppointmentInfo,
    val sender: ChatUser,
    val receiver: ChatUser
)

// Modelo para la información del appointment dentro del mensaje
data class AppointmentInfo(
    val id: Int,
    @SerializedName("worker_id") val workerId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("appointment_date") val appointmentDate: String?,
    @SerializedName("appointment_time") val appointmentTime: String?,
    @SerializedName("category_selected_id") val categorySelectedId: Int,
    val latitude: Double?,
    val longitude: Double?,
    val status: Int
)

// Modelo para usuarios en el chat (sender/receiver)
data class ChatUser(
    val id: Int,
    val name: String,
    val email: String
)

// Modelo para el usuario que hace la reseña
data class ReviewUser(
    val id: Int,
    val name: String,
    @SerializedName("last_name") val lastName: String,
    val type: Int
)

// Modelo corregido para una reseña
data class Resena(
    val id: Int,
    @SerializedName("worker_id") val workerId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("appointment_id") val appointmentId: Int,
    val rating: Int,
    val comment: String?, // El comentario puede ser nulo
    @SerializedName("is_done") val isDone: Int,
    val user: ReviewUser // Información del usuario que hizo la reseña
)

data class TrabajadorDetalle(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("picture_url") val pictureUrl: String?,
    @SerializedName("average_rating") val averageRating: String, // La API lo manda como String
    @SerializedName("reviews_count") val reviewsCount: Int,
    val user: User, // Reutilizamos la clase User que ya tenemos
    val categories: List<Categoria>, // Ahora es una lista de objetos Categoria
    val reviews: List<Resena> // Usa la nueva clase Resena
)

// Petición para /workers/{id}/categories
data class UpdateCategoriesRequest(
    val categories: List<Int>
)

// Respuesta de /client/register
data class RegisterResponse(
    val message: String,
    val user: RegisteredUser? = null
)

data class RegisteredUser(
    val id: Int,
    val name: String,
    val lastName: String,
    val email: String
)

data class Profile(
    val id: Int,
    val name: String,
    @SerializedName("last_name") val lastName: String,
    val type: Int
)

data class ErrorResponse(
    val message: String,
    val errors: Map<String, List<String>>
)

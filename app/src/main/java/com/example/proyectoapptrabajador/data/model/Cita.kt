package com.example.proyectoapptrabajador.data.model

import com.google.gson.annotations.SerializedName

data class Cita(
    val id: Int,
    @SerializedName("worker_id") val workerId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("appointment_date") val appointmentDate: String?,
    @SerializedName("appointment_time") val appointmentTime: String?,
    @SerializedName("category_selected_id") val categorySelectedId: Int,
    val latitude: Double?,
    val longitude: Double?,
    val status: Int,
    val worker: WorkerInfo,
    val category: Categoria,
    val client: User?
)

data class WorkerInfo(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("picture_url") val pictureUrl: String?,
    @SerializedName("average_rating") val averageRating: String, // La API lo devuelve como String
    @SerializedName("reviews_count") val reviewsCount: Int,
    val user: User?
)


package com.example.proyectoapptrabajador.data.model

import com.google.gson.annotations.SerializedName

data class Trabajador(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("picture_url") val pictureUrl: String?,
    @SerializedName("average_rating") val averageRating: Float,
    @SerializedName("reviews_count") val reviewsCount: Int,
    val user: User
)

data class User(
    val id: Int,
    val name: String,
    @SerializedName("last_name") val lastName: String,
    val type: Int
)


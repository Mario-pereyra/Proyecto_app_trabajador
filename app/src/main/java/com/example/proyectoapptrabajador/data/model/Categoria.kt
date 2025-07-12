package com.example.proyectoapptrabajador.data.model

import com.google.gson.annotations.SerializedName

data class Categoria(
    val id: Int,
    val name: String,
    val pivot: Pivot
)

data class Pivot(
    @SerializedName("worker_id") val workerId: Int,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

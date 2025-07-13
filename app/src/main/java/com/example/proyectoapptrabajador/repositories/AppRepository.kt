package com.example.proyectoapptrabajador.repositories

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.example.proyectoapptrabajador.data.datastore.TokenDataStore
import com.example.proyectoapptrabajador.data.model.*
import com.example.proyectoapptrabajador.data.network.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AppRepository(
    private val apiClient: ApiClient,
    private val tokenDataStore: TokenDataStore
) {
    suspend fun loginWorker(email: String, pass: String) =
        apiClient.loginWorker(LoginRequest(email, pass))

    suspend fun saveToken(token: String) {
        tokenDataStore.saveToken(token)
    }

    // --- FUNCIONES NUEVAS AÑADIDAS ---

    suspend fun getCategories() = apiClient.getCategories()

    suspend fun registerWorker(
        name: String,
        lastName: String,
        email: String,
        pass: String,
        categories: Set<Int>,
        pictureUri: Uri,
        contentResolver: ContentResolver
    ): retrofit2.Response<WorkerRegisterResponse> {
        // 1. Crear mapa de parámetros de texto
        val params = mutableMapOf<String, RequestBody>()
        params["name"] = name.toRequestBody("text/plain".toMediaTypeOrNull())
        params["lastName"] = lastName.toRequestBody("text/plain".toMediaTypeOrNull())
        params["email"] = email.toRequestBody("text/plain".toMediaTypeOrNull())
        params["password"] = pass.toRequestBody("text/plain".toMediaTypeOrNull())

        categories.forEachIndexed { index, categoryId ->
            params["categories[${index}][id]"] = categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }

        // 2. Crear parte del archivo de imagen
        val inputStream = contentResolver.openInputStream(pictureUri)
        val fileRequestBody = inputStream!!.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
        var fileName = "profile_picture.jpg"
        contentResolver.query(pictureUri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if(nameIndex != -1) fileName = cursor.getString(nameIndex)
            }
        }
        val picturePart = MultipartBody.Part.createFormData("picture", fileName, fileRequestBody)

        // 3. Llamar a la API
        return apiClient.registerWorker(params, picturePart)
    }
}
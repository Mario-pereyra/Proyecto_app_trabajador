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
import android.util.Log

class AppRepository(
    private val apiClient: ApiClient,
    private val tokenDataStore: TokenDataStore
) {
    suspend fun loginWorker(email: String, pass: String) =
        apiClient.loginWorker(LoginRequest(email, pass))

    suspend fun saveToken(token: String) {
        tokenDataStore.saveToken(token)
    }

    suspend fun getCategories() = apiClient.getCategories()

    // Registro secuencial en 3 pasos
    suspend fun registerWorkerStep1(
        name: String,
        lastName: String,
        email: String,
        password: String
    ) = apiClient.registerWorker(WorkerRegisterRequest(name, lastName, email, password))

    suspend fun uploadProfilePicture(
        pictureUri: Uri,
        contentResolver: ContentResolver
    ): retrofit2.Response<Void> {
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
        return apiClient.uploadProfilePicture(picturePart)
    }

    suspend fun assignCategories(
        categories: Set<Int>
    ): retrofit2.Response<WorkerCategoriesResponse> {
        try {
            Log.d("AppRepository", "=== INICIANDO ASIGNACIÓN DE CATEGORÍAS ===")
            Log.d("AppRepository", "Categorías a asignar: ${categories.joinToString(", ")}")

            // Primero obtenemos la información del usuario autenticado
            Log.d("AppRepository", "Obteniendo información del usuario con GET /me...")
            val meResponse = apiClient.getMe()

            if (!meResponse.isSuccessful) {
                val errorBody = meResponse.errorBody()?.string()
                Log.e("AppRepository", "Error en GET /me - Código: ${meResponse.code()}")
                Log.e("AppRepository", "Error en GET /me - Body: $errorBody")
                throw Exception("No se pudo obtener la información del trabajador: ${meResponse.code()} - $errorBody")
            }

            val meData = meResponse.body()
            Log.d("AppRepository", "GET /me exitoso - Respuesta: $meData")

            val workerId = meData?.worker?.id
            if (workerId == null) {
                Log.e("AppRepository", "Worker ID no encontrado en la respuesta de GET /me")
                throw Exception("ID del trabajador no encontrado en la respuesta")
            }

            Log.d("AppRepository", "Worker ID obtenido: $workerId")

            // Ahora asignamos las categorías usando el ID obtenido
            Log.d("AppRepository", "Realizando POST /workers/$workerId/categories...")
            val categoriesResponse = apiClient.assignCategories(
                workerId,
                WorkerCategoriesRequest(categories.map { CategoryId(it) })
            )

            if (!categoriesResponse.isSuccessful) {
                val errorBody = categoriesResponse.errorBody()?.string()
                Log.e("AppRepository", "Error en POST categories - Código: ${categoriesResponse.code()}")
                Log.e("AppRepository", "Error en POST categories - Body: $errorBody")
            } else {
                Log.d("AppRepository", "POST categories exitoso - Respuesta: ${categoriesResponse.body()}")
            }

            Log.d("AppRepository", "=== FIN ASIGNACIÓN DE CATEGORÍAS ===")
            return categoriesResponse

        } catch (e: Exception) {
            Log.e("AppRepository", "Excepción durante assignCategories", e)
            throw e
        }
    }

    suspend fun getCitas() = apiClient.getAppointments()

    suspend fun confirmCita(citaId: Int) = apiClient.confirmAppointment(citaId)

    suspend fun finalizeCita(citaId: Int) = apiClient.finalizeAppointment(citaId)

    suspend fun getMe() = apiClient.getMe()

    suspend fun getChatMessages(appointmentId: Int) = apiClient.getChatMessages(appointmentId)

    suspend fun sendChatMessage(appointmentId: Int, message: String, receiverId: Int) =
        apiClient.sendChatMessage(appointmentId, MensajeChatRequest(message, receiverId))

    suspend fun getAppointmentDetails(appointmentId: Int) = apiClient.getAppointmentDetails(appointmentId)
}
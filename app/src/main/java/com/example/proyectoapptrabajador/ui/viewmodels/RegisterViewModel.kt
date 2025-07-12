package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.RegisterRequest
import com.example.proyectoapptrabajador.data.model.ErrorResponse
import com.example.proyectoapptrabajador.repositories.AppRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AppRepository) : ViewModel() {

    // LiveData para el estado del registro
    private val _registrationStatus = MutableLiveData<Boolean>()
    val registrationStatus: LiveData<Boolean> = _registrationStatus

    // LiveData para comunicar errores a la UI
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun register(nombre: String, apellido: String, email: String, pass: String) {
        viewModelScope.launch {
            try {
                val request = RegisterRequest(nombre, apellido, email, pass)
                val response = repository.registerWorker(request) // Cambiado a registerWorker

                if (response.isSuccessful) {
                    _registrationStatus.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        try {
                            android.util.Log.e("RegisterViewModel", "ErrorBody: $errorBody")
                            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                            val mainMsg = errorResponse.message
                            val details = errorResponse.errors.entries.joinToString("\n") { (field, msgs) ->
                                "$field: ${msgs.joinToString(", ")}"
                            }
                            val message = if (details.isNotBlank()) "$mainMsg\n$details" else mainMsg
                            _errorMessage.value = message
                        } catch (e: Exception) {
                            android.util.Log.e("RegisterViewModel", "Error parseando errorBody", e)
                            _errorMessage.value = "Error en el registro (código ${response.code()})\n$errorBody"
                        }
                    } else {
                        _errorMessage.value = "Error desconocido en el registro."
                    }
                    _registrationStatus.value = false
                }
            } catch (e: java.net.UnknownHostException) {
                _errorMessage.value = "Error de conexión. Verifique su red."
                _registrationStatus.value = false
            } catch (e: Exception) {
                android.util.Log.e("RegisterViewModel", "Error inesperado", e)
                _errorMessage.value = "Error inesperado: ${e.localizedMessage}"
                _registrationStatus.value = false
            }
        }
    }
}

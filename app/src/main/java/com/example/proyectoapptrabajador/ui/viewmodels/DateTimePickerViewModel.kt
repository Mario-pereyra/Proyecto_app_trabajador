package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.ConcretarCitaRequest
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class DateTimePickerViewModel(private val repository: AppRepository) : ViewModel() {

    private val _appointmentMadeStatus = MutableLiveData<Boolean>()
    val appointmentMadeStatus: LiveData<Boolean> = _appointmentMadeStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun makeAppointment(id: Int, request: ConcretarCitaRequest) {
        viewModelScope.launch {
            try {
                // LOG: Información antes de enviar al servidor
                android.util.Log.d("DateTimePickerViewModel", "=== ENVIANDO REQUEST AL SERVIDOR ===")
                android.util.Log.d("DateTimePickerViewModel", "URL: POST /appointments/$id/make")
                android.util.Log.d("DateTimePickerViewModel", "Request Body:")
                android.util.Log.d("DateTimePickerViewModel", "  appointment_date: ${request.appointmentDate}")
                android.util.Log.d("DateTimePickerViewModel", "  appointment_time: ${request.appointmentTime}")
                android.util.Log.d("DateTimePickerViewModel", "  latitude: ${request.latitude}")
                android.util.Log.d("DateTimePickerViewModel", "  longitude: ${request.longitude}")

                val response = repository.makeAppointment(id, request)

                // LOG: Información de la respuesta del servidor
                android.util.Log.d("DateTimePickerViewModel", "=== RESPUESTA DEL SERVIDOR ===")
                android.util.Log.d("DateTimePickerViewModel", "Response Code: ${response.code()}")
                android.util.Log.d("DateTimePickerViewModel", "Response Message: ${response.message()}")
                android.util.Log.d("DateTimePickerViewModel", "Is Successful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val cita = response.body()
                    android.util.Log.d("DateTimePickerViewModel", "=== CITA CREADA EXITOSAMENTE ===")
                    android.util.Log.d("DateTimePickerViewModel", "Cita ID: ${cita?.id}")
                    android.util.Log.d("DateTimePickerViewModel", "Worker ID: ${cita?.worker?.id}")
                    android.util.Log.d("DateTimePickerViewModel", "User ID: ${cita?.client?.id}")
                    android.util.Log.d("DateTimePickerViewModel", "Status: ${cita?.status}")
                    android.util.Log.d("DateTimePickerViewModel", "Fecha final: ${cita?.appointmentDate}")
                    android.util.Log.d("DateTimePickerViewModel", "Hora final: ${cita?.appointmentTime}")
                    android.util.Log.d("DateTimePickerViewModel", "Latitud final: ${cita?.latitude}")
                    android.util.Log.d("DateTimePickerViewModel", "Longitud final: ${cita?.longitude}")
                    android.util.Log.d("DateTimePickerViewModel", "Response Body completo: $cita")
                    _appointmentMadeStatus.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("DateTimePickerViewModel", "=== ERROR EN LA RESPUESTA ===")
                    android.util.Log.e("DateTimePickerViewModel", "Error Body: $errorBody")

                    // Manejar el error específico de chat requerido
                    if (errorBody?.contains("Appointment needs at least a chat to continue") == true) {
                        _errorMessage.value = "Necesitas enviar al menos un mensaje en el chat antes de concretar la cita"
                    } else {
                        _errorMessage.value = "Error al concretar la cita: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DateTimePickerViewModel", "=== EXCEPCIÓN ===")
                android.util.Log.e("DateTimePickerViewModel", "Error: ${e.message}")
                android.util.Log.e("DateTimePickerViewModel", "Stack trace: ", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }
}

package com.example.proyectoapptrabajador.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.Cita
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class AppointmentDialogViewModel(private val repository: AppRepository) : ViewModel() {

    private val _appointmentDetails = MutableLiveData<Cita>()
    val appointmentDetails: LiveData<Cita> = _appointmentDetails

    private val _confirmResult = MutableLiveData<Boolean>()
    val confirmResult: LiveData<Boolean> = _confirmResult

    private val _finalizeResult = MutableLiveData<Boolean>()
    val finalizeResult: LiveData<Boolean> = _finalizeResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun loadAppointmentDetails(appointmentId: Int) {
        viewModelScope.launch {
            try {
                Log.d("AppointmentDialogViewModel", "Cargando detalles de la cita: $appointmentId")
                val response = repository.getAppointmentDetails(appointmentId)

                if (response.isSuccessful) {
                    _appointmentDetails.value = response.body()
                    Log.d("AppointmentDialogViewModel", "Detalles cargados: ${response.body()}")
                } else {
                    Log.e("AppointmentDialogViewModel", "Error al cargar detalles: ${response.code()}")
                    _errorMessage.value = "Error al cargar información de la cita"
                }
            } catch (e: Exception) {
                Log.e("AppointmentDialogViewModel", "Excepción al cargar detalles", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }

    fun confirmAppointment(appointmentId: Int) {
        val cita = _appointmentDetails.value
        if (cita == null) {
            _errorMessage.value = "Información de cita no disponible"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Obtener información del trabajador actual
                val meResponse = repository.getMe()
                if (!meResponse.isSuccessful) {
                    _errorMessage.value = "Error al obtener información del trabajador"
                    _isLoading.value = false
                    return@launch
                }

                val workerId = meResponse.body()?.worker?.id?.toString()
                if (workerId == null) {
                    _errorMessage.value = "ID del trabajador no encontrado"
                    _isLoading.value = false
                    return@launch
                }

                Log.d("AppointmentDialogViewModel", "Confirmando cita: $appointmentId")
                Log.d("AppointmentDialogViewModel", "Worker ID: $workerId")
                Log.d("AppointmentDialogViewModel", "Category ID: ${cita.category_selected_id}")

                val response = repository.confirmCita(
                    appointmentId,
                    workerId,
                    cita.category_selected_id
                )

                if (response.isSuccessful) {
                    Log.d("AppointmentDialogViewModel", "Cita confirmada exitosamente")
                    _confirmResult.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AppointmentDialogViewModel", "Error al confirmar cita - Código: ${response.code()}")
                    Log.e("AppointmentDialogViewModel", "Error al confirmar cita - Body: $errorBody")
                    _errorMessage.value = "Error al confirmar la cita: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("AppointmentDialogViewModel", "Excepción al confirmar cita", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun finalizeAppointment(appointmentId: Int) {
        val cita = _appointmentDetails.value
        if (cita == null) {
            _errorMessage.value = "Información de cita no disponible"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Obtener información del trabajador actual
                val meResponse = repository.getMe()
                if (!meResponse.isSuccessful) {
                    _errorMessage.value = "Error al obtener información del trabajador"
                    _isLoading.value = false
                    return@launch
                }

                val workerId = meResponse.body()?.worker?.id?.toString()
                if (workerId == null) {
                    _errorMessage.value = "ID del trabajador no encontrado"
                    _isLoading.value = false
                    return@launch
                }

                Log.d("AppointmentDialogViewModel", "Finalizando cita: $appointmentId")
                Log.d("AppointmentDialogViewModel", "Worker ID: $workerId")
                Log.d("AppointmentDialogViewModel", "Category ID: ${cita.category_selected_id}")

                val response = repository.finalizeCita(
                    appointmentId,
                    workerId,
                    cita.category_selected_id
                )

                if (response.isSuccessful) {
                    Log.d("AppointmentDialogViewModel", "Cita finalizada exitosamente")
                    _finalizeResult.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AppointmentDialogViewModel", "Error al finalizar cita - Código: ${response.code()}")
                    Log.e("AppointmentDialogViewModel", "Error al finalizar cita - Body: $errorBody")
                    _errorMessage.value = "Error al finalizar la cita: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("AppointmentDialogViewModel", "Excepción al finalizar cita", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

package com.example.proyectoapptrabajador.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.Cita
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class CitasViewModel(private val repository: AppRepository) : ViewModel() {

    private val _citas = MutableLiveData<List<Cita>>()
    val citas: LiveData<List<Cita>> = _citas

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun loadCitas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getCitas()
                if (response.isSuccessful) {
                    _citas.value = response.body() ?: emptyList()
                    Log.d("CitasViewModel", "Citas cargadas: ${response.body()?.size}")
                } else {
                    _citas.value = emptyList()
                    Log.e("CitasViewModel", "Error al cargar citas: ${response.code()}")
                    _errorMessage.value = "Error al cargar las citas"
                }
            } catch (e: Exception) {
                _citas.value = emptyList()
                Log.e("CitasViewModel", "Excepción al cargar citas", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun confirmCita(citaId: Int) {
        viewModelScope.launch {
            try {
                // Primero obtenemos los detalles de la cita para obtener el categoryId
                val citaResponse = repository.getAppointmentDetails(citaId)
                if (!citaResponse.isSuccessful) {
                    Log.e("CitasViewModel", "Error al obtener detalles de cita: ${citaResponse.code()}")
                    _errorMessage.value = "Error al obtener información de la cita"
                    return@launch
                }

                val cita = citaResponse.body()
                if (cita == null) {
                    _errorMessage.value = "No se pudo obtener información de la cita"
                    return@launch
                }

                // Obtenemos el workerId usando GET /me
                val meResponse = repository.getMe()
                if (!meResponse.isSuccessful) {
                    Log.e("CitasViewModel", "Error al obtener información del trabajador: ${meResponse.code()}")
                    _errorMessage.value = "Error al obtener información del trabajador"
                    return@launch
                }

                val workerId = meResponse.body()?.worker?.id?.toString()
                if (workerId == null) {
                    _errorMessage.value = "ID del trabajador no encontrado"
                    return@launch
                }

                Log.d("CitasViewModel", "Confirmando cita $citaId con workerId: $workerId, categoryId: ${cita.category_selected_id}")

                // Ahora confirmamos la cita con los parámetros correctos
                val response = repository.confirmCita(citaId, workerId, cita.category_selected_id)
                if (response.isSuccessful) {
                    Log.d("CitasViewModel", "Cita confirmada: $citaId")
                    loadCitas() // Recargar las citas
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CitasViewModel", "Error al confirmar cita: ${response.code()}")
                    Log.e("CitasViewModel", "Error body: $errorBody")
                    _errorMessage.value = "Error al confirmar la cita: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("CitasViewModel", "Excepción al confirmar cita", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }
}

package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.CrearCitaRequest
import com.example.proyectoapptrabajador.data.model.Cita
import com.example.proyectoapptrabajador.data.model.TrabajadorDetalle
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class WorkerDetailViewModel(private val repository: AppRepository) : ViewModel() {

    private val _workerDetails = MutableLiveData<TrabajadorDetalle>()
    val workerDetails: LiveData<TrabajadorDetalle> = _workerDetails

    private val _newAppointment = MutableLiveData<Cita>()
    val newAppointment: LiveData<Cita> = _newAppointment

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchWorkerDetails(workerId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getWorkerDetail(workerId)
                if (response.isSuccessful && response.body() != null) {
                    _workerDetails.value = response.body()!!
                } else {
                    _errorMessage.value = "Error al obtener detalles del trabajador."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión."
            }
        }
    }

    fun createAppointment(workerId: String, categoryId: Int) {
        viewModelScope.launch {
            try {
                val request = CrearCitaRequest(workerId, categoryId)
                val response = repository.createAppointment(request)
                if (response.isSuccessful && response.body() != null) {
                    _newAppointment.value = response.body()!!
                } else {
                    _errorMessage.value = "No se pudo crear la cita."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión al crear la cita."
            }
        }
    }

    fun clearNewAppointment() {
        _newAppointment.value = null
    }
}

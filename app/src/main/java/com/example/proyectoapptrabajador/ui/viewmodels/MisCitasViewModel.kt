package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.Cita
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class AppointmentsViewModel(private val repository: AppRepository) : ViewModel() {

    private val _appointments = MutableLiveData<List<Cita>>()
    val appointments: LiveData<List<Cita>> = _appointments

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchAppointments() {
        viewModelScope.launch {
            try {
                val response = repository.getAppointments()
                if (response.isSuccessful) {
                    _appointments.value = response.body()
                } else {
                    _errorMessage.value = "Error al obtener las citas."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión."
            }
        }
    }

    fun confirmAppointment(appointmentId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.confirmAppointment(appointmentId)
                if (response.isSuccessful) {
                    fetchAppointments()
                } else {
                    _errorMessage.value = "Error al confirmar la cita."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión."
            }
        }
    }

    fun finalizeAppointment(appointmentId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.finalizeAppointment(appointmentId)
                if (response.isSuccessful) {
                    fetchAppointments()
                } else {
                    _errorMessage.value = "Error al finalizar el trabajo."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión."
            }
        }
    }
}

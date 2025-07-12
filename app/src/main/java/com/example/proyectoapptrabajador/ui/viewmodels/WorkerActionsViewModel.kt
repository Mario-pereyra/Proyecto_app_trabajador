package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class WorkerActionsViewModel(private val repository: AppRepository) : ViewModel() {

    private val _confirmStatus = MutableLiveData<Boolean>()
    val confirmStatus: LiveData<Boolean> = _confirmStatus

    private val _finalizeStatus = MutableLiveData<Boolean>()
    val finalizeStatus: LiveData<Boolean> = _finalizeStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun confirmAppointment(appointmentId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.confirmAppointment(appointmentId)
                if (response.isSuccessful) {
                    _confirmStatus.value = true
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
                    _finalizeStatus.value = true
                } else {
                    _errorMessage.value = "Error al finalizar la cita."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión."
            }
        }
    }

    fun clearStatus() {
        _confirmStatus.value = null
        _finalizeStatus.value = null
    }
}

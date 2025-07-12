package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.CalificarCitaRequest
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: AppRepository) : ViewModel() {

    private val _reviewStatus = MutableLiveData<Boolean>()
    val reviewStatus: LiveData<Boolean> = _reviewStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun postReview(appointmentId: Int, rating: Int, comment: String) {
        viewModelScope.launch {
            try {
                val request = CalificarCitaRequest(rating, comment)
                val response = repository.postReview(appointmentId, request)
                if (response.isSuccessful) {
                    _reviewStatus.value = true
                } else {
                    _errorMessage.value = "Error al enviar la calificación."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión."
            }
        }
    }
}

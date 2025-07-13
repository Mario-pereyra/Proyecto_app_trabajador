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
                val response = repository.confirmCita(citaId)
                if (response.isSuccessful) {
                    Log.d("CitasViewModel", "Cita confirmada: $citaId")
                    loadCitas() // Recargar las citas
                } else {
                    Log.e("CitasViewModel", "Error al confirmar cita: ${response.code()}")
                    _errorMessage.value = "Error al confirmar la cita"
                }
            } catch (e: Exception) {
                Log.e("CitasViewModel", "Excepción al confirmar cita", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }
}

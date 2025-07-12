package com.example.proyectoapptrabajador.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.Category
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class WorkerRegisterViewModel(private val repository: AppRepository) : ViewModel() {
    // LiveData para guardar los datos entre pasos
    val name = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val profilePictureUri = MutableLiveData<Uri>()
    val selectedCategories = MutableLiveData<List<Int>>()

    // LiveData para las categorías disponibles
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    // LiveData para estados de registro
    private val _registrationStatus = MutableLiveData<Boolean>()
    val registrationStatus: LiveData<Boolean> = _registrationStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val response = repository.getCategories()
                if (response.isSuccessful) {
                    _categories.value = response.body()
                } else {
                    _errorMessage.value = "Error al cargar categorías"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }

    // Lógica para registrar al trabajador al final del flujo
    fun registerWorker() {
        // Aquí irá la lógica para construir el Multipart y llamar al repositorio
        _registrationStatus.value = true // Simulado por ahora
    }
}

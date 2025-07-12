package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.Trabajador
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class WorkersViewModel(private val repository: AppRepository) : ViewModel() {

    private val _workers = MutableLiveData<List<Trabajador>>()
    val workers: LiveData<List<Trabajador>> = _workers

    private val _originalWorkers = mutableListOf<Trabajador>()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchWorkers(categoryId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getWorkersByCategory(categoryId)
                if (response.isSuccessful && response.body() != null) {
                    _originalWorkers.clear()
                    _originalWorkers.addAll(response.body()!!)
                    _workers.value = _originalWorkers
                } else {
                    _errorMessage.value = "Error al obtener los trabajadores."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión."
            }
        }
    }

    fun filterWorkers(query: String) {
        if (query.isEmpty()) {
            _workers.value = _originalWorkers
        } else {
            val filteredList = _originalWorkers.filter {
                val fullName = "${it.user.name} ${it.user.lastName}"
                fullName.contains(query, ignoreCase = true)
            }
            _workers.value = filteredList
        }
    }
}

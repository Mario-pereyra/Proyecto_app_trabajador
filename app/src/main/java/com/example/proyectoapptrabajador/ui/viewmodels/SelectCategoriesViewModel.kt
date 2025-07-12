package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.Categoria
import com.example.proyectoapptrabajador.data.model.UpdateCategoriesRequest
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class SelectCategoriesViewModel(private val repository: AppRepository) : ViewModel() {

    private val _categories = MutableLiveData<List<Categoria>>()
    val categories: LiveData<List<Categoria>> = _categories

    private val _selectedCategories = MutableLiveData<Set<Int>>(emptySet())
    val selectedCategories: LiveData<Set<Int>> = _selectedCategories

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> = _updateStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = repository.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    _categories.value = response.body()!!
                } else {
                    _errorMessage.value = "Error al obtener las categorías."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión."
            }
        }
    }

    fun toggleCategory(categoryId: Int) {
        val currentSelection = _selectedCategories.value ?: emptySet()
        val newSelection = if (currentSelection.contains(categoryId)) {
            currentSelection - categoryId
        } else {
            currentSelection + categoryId
        }
        _selectedCategories.value = newSelection
    }

    fun updateWorkerCategories(workerId: Int) {
        val selectedIds = _selectedCategories.value?.toList() ?: emptyList()
        if (selectedIds.isEmpty()) {
            _errorMessage.value = "Selecciona al menos una categoría."
            return
        }

        viewModelScope.launch {
            try {
                val request = UpdateCategoriesRequest(selectedIds)
                val response = repository.updateWorkerCategories(workerId, request)
                if (response.isSuccessful) {
                    _updateStatus.value = true
                } else {
                    _errorMessage.value = "Error al actualizar las categorías."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión."
            }
        }
    }
}

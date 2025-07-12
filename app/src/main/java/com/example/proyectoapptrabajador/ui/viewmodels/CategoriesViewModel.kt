package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.Categoria
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class CategoriesViewModel(private val repository: AppRepository) : ViewModel() {

    private val _categories = MutableLiveData<List<Categoria>>()
    val categories: LiveData<List<Categoria>> = _categories

    private val _originalCategories = mutableListOf<Categoria>()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus: LiveData<Boolean> = _logoutStatus

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = repository.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    _originalCategories.clear()
                    _originalCategories.addAll(response.body()!!)
                    _categories.value = _originalCategories
                } else {
                    _errorMessage.value = "Error al obtener las categorías."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión."
            }
        }
    }

    fun filterCategories(query: String) {
        if (query.isEmpty()) {
            _categories.value = _originalCategories
        } else {
            val filteredList = _originalCategories.filter {
                it.name.contains(query, ignoreCase = true)
            }
            _categories.value = filteredList
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearToken()
            _logoutStatus.value = true
        }
    }
}

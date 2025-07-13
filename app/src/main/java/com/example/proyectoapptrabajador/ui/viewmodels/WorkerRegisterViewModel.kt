package com.example.proyectoapptrabajador.ui.viewmodels

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.Category
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class WorkerRegisterViewModel(private val repository: AppRepository) : ViewModel() {

    // --- Datos recolectados en los pasos ---
    val name = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val profilePictureUri = MutableLiveData<Uri?>()
    private val _selectedCategories = MutableLiveData<Set<Int>>(emptySet())

    // --- LiveData para la UI ---
    private val _categoriesList = MutableLiveData<List<Category>>()
    val categoriesList: LiveData<List<Category>> = _categoriesList

    private val _registrationResult = MutableLiveData<Boolean>()
    val registrationResult: LiveData<Boolean> = _registrationResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // --- Lógica de negocio ---
    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = repository.getCategories()
                _categoriesList.value = if (response.isSuccessful) response.body() else emptyList()
            } catch (e: Exception) { _errorMessage.value = "Error de conexión" }
        }
    }

    fun onCategorySelected(categoryId: Int, isChecked: Boolean) {
        val currentSelection = _selectedCategories.value?.toMutableSet() ?: mutableSetOf()
        if (isChecked) currentSelection.add(categoryId) else currentSelection.remove(categoryId)
        _selectedCategories.value = currentSelection
    }

    fun isCategorySelected(categoryId: Int): Boolean {
        return _selectedCategories.value?.contains(categoryId) ?: false
    }

    fun registerWorker(contentResolver: ContentResolver) {
        val pictureUri = profilePictureUri.value
        val categories = _selectedCategories.value
        if (name.value.isNullOrBlank() || lastName.value.isNullOrBlank() || email.value.isNullOrBlank() || password.value.isNullOrBlank() || pictureUri == null || categories.isNullOrEmpty()) {
            _errorMessage.value = "Todos los campos son obligatorios"
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.registerWorker(name.value!!, lastName.value!!, email.value!!, password.value!!, categories, pictureUri, contentResolver)
                if (response.isSuccessful) {
                    val accessToken = response.body()?.let { "12345|faketoken" } // La API de registro no devuelve token, simulamos uno para el login automático.
                    if (accessToken != null) {
                        repository.saveToken(accessToken)
                        _registrationResult.postValue(true)
                    }
                } else {
                    _errorMessage.postValue("Error en el registro: ${response.code()}")
                    _registrationResult.postValue(false)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error de conexión: ${e.message}")
                _registrationResult.postValue(false)
            }
        }
    }
}
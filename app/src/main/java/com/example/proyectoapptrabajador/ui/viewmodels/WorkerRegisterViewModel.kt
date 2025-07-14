package com.example.proyectoapptrabajador.ui.viewmodels

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
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

    private val _basicRegistrationResult = MutableLiveData<Boolean>()
    val basicRegistrationResult: LiveData<Boolean> = _basicRegistrationResult

    private val _photoUploadResult = MutableLiveData<Boolean>()
    val photoUploadResult: LiveData<Boolean> = _photoUploadResult

    private val _categoriesAssignResult = MutableLiveData<Boolean>()
    val categoriesAssignResult: LiveData<Boolean> = _categoriesAssignResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // --- Lógica de negocio ---
    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = repository.getCategories()
                if (response.isSuccessful) {
                    _categoriesList.value = response.body() ?: emptyList()
                    Log.d("WorkerRegisterViewModel", "Categorías obtenidas: ${response.body()}")
                } else {
                    _errorMessage.value = "Error al cargar las ocupaciones"
                    Log.e("WorkerRegisterViewModel", "Error al obtener categorías: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión al cargar ocupaciones"
                Log.e("WorkerRegisterViewModel", "Excepción al obtener categorías", e)
            }
        }
    }

    fun createCategory(name: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = repository.createCategory(name)
                if (response.isSuccessful) {
                    Log.d("WorkerRegisterViewModel", "Categoría creada exitosamente: ${response.body()}")
                    // Refrescar la lista de categorías después de crear una nueva
                    fetchCategories()
                } else {
                    _errorMessage.value = "Error al crear la ocupación"
                    Log.e("WorkerRegisterViewModel", "Error al crear categoría: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión al crear ocupación"
                Log.e("WorkerRegisterViewModel", "Excepción al crear categoría", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onCategorySelected(categoryId: Int, isChecked: Boolean) {
        val currentSelection = _selectedCategories.value?.toMutableSet() ?: mutableSetOf()
        if (isChecked) {
            currentSelection.add(categoryId)
        } else {
            currentSelection.remove(categoryId)
        }
        _selectedCategories.value = currentSelection
        Log.d("WorkerRegisterViewModel", "Categoría $categoryId ${if (isChecked) "seleccionada" else "deseleccionada"}. Total: ${currentSelection.size}")
    }

    fun isCategorySelected(categoryId: Int): Boolean {
        return _selectedCategories.value?.contains(categoryId) ?: false
    }

    fun getSelectedCategoriesDebug(): String {
        val categories = _selectedCategories.value ?: emptySet()
        return if (categories.isEmpty()) {
            "Ninguna categoría seleccionada"
        } else {
            "IDs: ${categories.joinToString(", ")} (Total: ${categories.size})"
        }
    }

    // PASO 1: Registro básico de datos personales
    fun registerWorkerBasicData() {
        if (name.value.isNullOrBlank() || lastName.value.isNullOrBlank() ||
            email.value.isNullOrBlank() || password.value.isNullOrBlank()) {
            _errorMessage.value = "Todos los campos son obligatorios"
            return
        }

        Log.d("WorkerRegisterViewModel", "Iniciando registro básico con datos: " +
            "name=${name.value}, lastName=${lastName.value}, email=${email.value}")

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val registerResponse = repository.registerWorkerStep1(
                    name.value!!,
                    lastName.value!!,
                    email.value!!,
                    password.value!!
                )

                if (!registerResponse.isSuccessful) {
                    Log.e("WorkerRegisterViewModel", "Error en registro básico: ${registerResponse.code()}")
                    _errorMessage.postValue("Error en el registro: ${registerResponse.code()}")
                    _isLoading.value = false
                    return@launch
                }

                Log.d("WorkerRegisterViewModel", "Registro básico completado")
                _basicRegistrationResult.postValue(true)
                _isLoading.value = false

            } catch (e: Exception) {
                Log.e("WorkerRegisterViewModel", "Excepción durante el registro básico", e)
                _errorMessage.postValue("Error de conexión: ${e.message}")
                _basicRegistrationResult.postValue(false)
                _isLoading.value = false
            }
        }
    }

    // PASO 2: Subir foto de perfil (requiere token)
    fun uploadProfilePicture(contentResolver: ContentResolver) {
        val pictureUri = profilePictureUri.value

        if (pictureUri == null) {
            _errorMessage.value = "Selecciona una foto de perfil"
            return
        }

        Log.d("WorkerRegisterViewModel", "Subiendo foto de perfil...")
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val uploadResponse = repository.uploadProfilePicture(pictureUri, contentResolver)

                if (!uploadResponse.isSuccessful) {
                    Log.e("WorkerRegisterViewModel", "Error al subir foto: ${uploadResponse.code()}")
                    _errorMessage.postValue("Error al subir la foto: ${uploadResponse.code()}")
                    _isLoading.value = false
                    return@launch
                }

                Log.d("WorkerRegisterViewModel", "Foto subida exitosamente")
                _photoUploadResult.postValue(true)
                _isLoading.value = false

            } catch (e: Exception) {
                Log.e("WorkerRegisterViewModel", "Excepción al subir foto", e)
                _errorMessage.postValue("Error de conexión: ${e.message}")
                _photoUploadResult.postValue(false)
                _isLoading.value = false
            }
        }
    }

    // PASO 3: Asignar categorías (requiere token)
    fun assignCategories() {
        val categories = _selectedCategories.value

        if (categories.isNullOrEmpty()) {
            _errorMessage.value = "Selecciona al menos una ocupación"
            return
        }

        Log.d("WorkerRegisterViewModel", "Asignando categorías: ${categories.size}")
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val categoriesResponse = repository.assignCategories(categories)

                if (!categoriesResponse.isSuccessful) {
                    Log.e("WorkerRegisterViewModel", "Error al asignar categorías: ${categoriesResponse.code()}")
                    _errorMessage.postValue("Error al asignar categorías: ${categoriesResponse.code()}")
                    _isLoading.value = false
                    return@launch
                }

                Log.d("WorkerRegisterViewModel", "Categorías asignadas exitosamente: ${categoriesResponse.body()}")
                _categoriesAssignResult.postValue(true)
                _isLoading.value = false

            } catch (e: Exception) {
                Log.e("WorkerRegisterViewModel", "Excepción al asignar categorías", e)
                _errorMessage.postValue("Error de conexión: ${e.message}")
                _categoriesAssignResult.postValue(false)
                _isLoading.value = false
            }
        }
    }
}
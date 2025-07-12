package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.Categoria
import com.example.proyectoapptrabajador.data.model.UpdateCategoriesRequest
import com.example.proyectoapptrabajador.data.model.TrabajadorDetalle
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class EditProfileViewModel(private val repository: AppRepository) : ViewModel() {

    private val _workerProfile = MutableLiveData<TrabajadorDetalle>()
    val workerProfile: LiveData<TrabajadorDetalle> = _workerProfile

    private val _allCategories = MutableLiveData<List<Categoria>>()
    val allCategories: LiveData<List<Categoria>> = _allCategories

    private val _selectedCategories = MutableLiveData<Set<Int>>()
    val selectedCategories: LiveData<Set<Int>> = _selectedCategories

    private val _photoUploadStatus = MutableLiveData<Boolean>()
    val photoUploadStatus: LiveData<Boolean> = _photoUploadStatus

    private val _categoriesUpdateStatus = MutableLiveData<Boolean>()
    val categoriesUpdateStatus: LiveData<Boolean> = _categoriesUpdateStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _selectedImageUri = MutableLiveData<String?>()
    val selectedImageUri: LiveData<String?> = _selectedImageUri

    fun loadWorkerProfile(workerId: Int) {
        viewModelScope.launch {
            try {
                // Cargar perfil del trabajador
                val profileResponse = repository.getWorkerDetail(workerId)
                if (profileResponse.isSuccessful && profileResponse.body() != null) {
                    val profile = profileResponse.body()!!
                    _workerProfile.value = profile

                    // Establecer categorías actualmente seleccionadas
                    val currentCategoryIds = profile.categories.map { it.id }.toSet()
                    _selectedCategories.value = currentCategoryIds
                }

                // Cargar todas las categorías disponibles
                val categoriesResponse = repository.getCategories()
                if (categoriesResponse.isSuccessful && categoriesResponse.body() != null) {
                    _allCategories.value = categoriesResponse.body()!!
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el perfil."
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

    fun setSelectedImageUri(uri: String?) {
        _selectedImageUri.value = uri
    }

    fun uploadProfilePicture(imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response = repository.uploadProfilePicture(imagePart)
                if (response.isSuccessful) {
                    _photoUploadStatus.value = true
                } else {
                    _errorMessage.value = "Error al subir la imagen."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión al subir la imagen."
            }
        }
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
                    _categoriesUpdateStatus.value = true
                } else {
                    _errorMessage.value = "Error al actualizar las categorías."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión al actualizar categorías."
            }
        }
    }

    fun saveAllChanges(workerId: Int) {
        // Primero actualizar categorías
        updateWorkerCategories(workerId)

        // Si hay una nueva imagen seleccionada, subirla también
        // (esto se maneja por separado cuando el usuario selecciona la foto)
    }

    fun clearStatus() {
        _photoUploadStatus.value = null
        _categoriesUpdateStatus.value = null
    }
}

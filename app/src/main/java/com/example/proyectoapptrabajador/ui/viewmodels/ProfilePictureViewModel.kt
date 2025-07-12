package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfilePictureViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uploadStatus = MutableLiveData<Boolean>()
    val uploadStatus: LiveData<Boolean> = _uploadStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _selectedImageUri = MutableLiveData<String?>()
    val selectedImageUri: LiveData<String?> = _selectedImageUri

    fun setSelectedImageUri(uri: String?) {
        _selectedImageUri.value = uri
    }

    fun uploadProfilePicture(imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response = repository.uploadProfilePicture(imagePart)
                if (response.isSuccessful) {
                    _uploadStatus.value = true
                } else {
                    _errorMessage.value = "Error al subir la imagen."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión."
            }
        }
    }

    fun skipProfilePicture() {
        // Si el usuario decide omitir la foto, simplemente marcamos como completado
        _uploadStatus.value = true
    }
}

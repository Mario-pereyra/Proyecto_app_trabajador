package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AppRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun iniciarSesion(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Por favor, complete todos los campos."
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.loginWorker(email, pass)
                if (response.isSuccessful) {
                    val accessToken = response.body()?.access_token
                    if (!accessToken.isNullOrBlank()) {
                        repository.saveToken(accessToken)
                        _loginResult.postValue(true)
                    } else {
                        _errorMessage.postValue("Respuesta inesperada del servidor.")
                        _loginResult.postValue(false)
                    }
                } else {
                    _errorMessage.postValue("Email o contraseña incorrectos.")
                    _loginResult.postValue(false)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error de conexión. Verifique su red.")
                _loginResult.postValue(false)
            }
        }
    }
}
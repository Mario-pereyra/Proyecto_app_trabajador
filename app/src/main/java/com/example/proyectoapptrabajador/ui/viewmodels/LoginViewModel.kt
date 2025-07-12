package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.LoginRequest
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AppRepository) : ViewModel() {

    // LiveData para el estado del login
    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    // LiveData para comunicar errores a la UI
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // LiveData para el estado de la sesión (si ya hay un token)
    private val _sessionStatus = MutableLiveData<Boolean>()
    val sessionStatus: LiveData<Boolean> = _sessionStatus

    // Verifica si ya existe un token en DataStore
    fun checkSession() {
        viewModelScope.launch {
            val token = repository.getToken().first() // Lee el token una sola vez
            _sessionStatus.value = !token.isNullOrBlank()
        }
    }

    // Ejecuta el proceso de login
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, pass)
                val response = repository.loginWorker(request) // Cambiado a loginWorker
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.accessToken
                    repository.saveToken(token) // Guarda el token
                    _loginStatus.value = true
                } else {
                    _errorMessage.value = "Credenciales inválidas"
                    _loginStatus.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión. Verifique su red."
                _loginStatus.value = false
            }
        }
    }
}

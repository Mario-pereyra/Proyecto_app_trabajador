package com.example.proyectoapptrabajador.ui.viewmodels

import android.util.Log
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

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

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
                        _successMessage.postValue("Se inició sesión correctamente")
                    } else {
                        Log.e("LoginViewModel", "Respuesta inesperada del servidor. Email: $email, Password: $pass")
                        _errorMessage.postValue("Respuesta inesperada del servidor.")
                        _loginResult.postValue(false)
                    }
                } else {
                    Log.e("LoginViewModel", "Error en login: ${response.errorBody()?.string()} Email: $email, Password: $pass")
                    _errorMessage.postValue("Email o contraseña incorrectos.")
                    _loginResult.postValue(false)
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Excepción en login: ${e.message} Email: $email, Password: $pass")
                _errorMessage.postValue("Error de conexión. Verifique su red.")
                _loginResult.postValue(false)
            }
        }
    }
}
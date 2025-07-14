package com.example.proyectoapptrabajador.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.Cita
import com.example.proyectoapptrabajador.data.model.MensajeChat
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: AppRepository) : ViewModel() {

    private val _mensajes = MutableLiveData<List<MensajeChat>>()
    val mensajes: LiveData<List<MensajeChat>> = _mensajes

    private val _citaDetails = MutableLiveData<Cita>()
    val citaDetails: LiveData<Cita> = _citaDetails

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSendingMessage = MutableLiveData<Boolean>()
    val isSendingMessage: LiveData<Boolean> = _isSendingMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _messageSent = MutableLiveData<Boolean>()
    val messageSent: LiveData<Boolean> = _messageSent

    private var currentAppointmentId: Int? = null
    private var currentWorkerId: Int? = null
    private var isPolling = false

    fun startChat(appointmentId: Int) {
        currentAppointmentId = appointmentId
        Log.d("ChatViewModel", "Iniciando chat para appointment ID: $appointmentId")

        // Cargar detalles de la cita y obtener información del trabajador PRIMERO
        // Solo después cargar mensajes para asegurar que currentWorkerId esté disponible
        loadAppointmentDetailsAndThenMessages(appointmentId)

        // Iniciar polling automático cada 30 segundos
        startAutoRefresh()
    }

    private fun loadAppointmentDetailsAndThenMessages(appointmentId: Int) {
        viewModelScope.launch {
            try {
                Log.d("ChatViewModel", "Cargando detalles de la cita...")

                // PRIMERO: Obtener información del trabajador actual usando GET /me
                val meResponse = repository.getMe()
                if (meResponse.isSuccessful) {
                    currentWorkerId = meResponse.body()?.id
                    Log.d("ChatViewModel", "Worker actual ID obtenido: $currentWorkerId")
                } else {
                    Log.e("ChatViewModel", "Error al obtener información del trabajador: ${meResponse.code()}")
                    _errorMessage.value = "Error al obtener información del trabajador"
                    return@launch
                }

                // SEGUNDO: Cargar detalles de la cita
                val response = repository.getAppointmentDetails(appointmentId)
                if (response.isSuccessful) {
                    val cita = response.body()
                    _citaDetails.value = cita
                    Log.d("ChatViewModel", "Cliente ID: ${cita?.user_id}")
                    Log.d("ChatViewModel", "Detalles de cita cargados: $cita")
                } else {
                    Log.e("ChatViewModel", "Error al cargar detalles de cita: ${response.code()}")
                    _errorMessage.value = "Error al cargar información de la cita"
                    return@launch
                }

                // TERCERO: Ahora que tenemos currentWorkerId, cargar mensajes
                Log.d("ChatViewModel", "Cargando mensajes con workerId ya disponible...")
                loadChatMessages(appointmentId)

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Excepción al cargar detalles de cita", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }

    fun loadChatMessages(appointmentId: Int) {
        viewModelScope.launch {
            try {
                Log.d("ChatViewModel", "Cargando mensajes del chat...")
                val response = repository.getChatMessages(appointmentId)

                if (response.isSuccessful) {
                    val messages = response.body() ?: emptyList()
                    _mensajes.value = messages
                    Log.d("ChatViewModel", "Mensajes cargados: ${messages.size}")
                } else {
                    Log.e("ChatViewModel", "Error al cargar mensajes: ${response.code()}")
                    _errorMessage.value = "Error al cargar mensajes"
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Excepción al cargar mensajes", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }

    fun sendMessage(message: String) {
        val appointmentId = currentAppointmentId
        val cita = _citaDetails.value

        if (appointmentId == null || cita == null) {
            _errorMessage.value = "Error: Información de cita no disponible"
            return
        }

        if (message.trim().isEmpty()) {
            _errorMessage.value = "El mensaje no puede estar vacío"
            return
        }

        _isSendingMessage.value = true

        viewModelScope.launch {
            try {
                Log.d("ChatViewModel", "Enviando mensaje: '$message'")
                Log.d("ChatViewModel", "Receiver ID (cliente): ${cita.user_id}")

                val response = repository.sendChatMessage(appointmentId, message, cita.user_id)

                if (response.isSuccessful) {
                    Log.d("ChatViewModel", "Mensaje enviado exitosamente")
                    _messageSent.value = true

                    // Recargar mensajes inmediatamente después de enviar
                    loadChatMessages(appointmentId)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ChatViewModel", "Error al enviar mensaje - Código: ${response.code()}")
                    Log.e("ChatViewModel", "Error al enviar mensaje - Body: $errorBody")
                    _errorMessage.value = "Error al enviar mensaje: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Excepción al enviar mensaje", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isSendingMessage.value = false
            }
        }
    }

    private fun startAutoRefresh() {
        if (isPolling) return

        isPolling = true
        viewModelScope.launch {
            while (isPolling && currentAppointmentId != null) {
                delay(30000) // 30 segundos
                if (isPolling && currentAppointmentId != null) {
                    Log.d("ChatViewModel", "Auto-refrescando mensajes...")
                    loadChatMessages(currentAppointmentId!!)
                }
            }
        }
    }

    fun stopAutoRefresh() {
        isPolling = false
        Log.d("ChatViewModel", "Auto-refresh detenido")
    }

    fun isMyMessage(senderId: Int): Boolean {
        return senderId == currentWorkerId
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }
}

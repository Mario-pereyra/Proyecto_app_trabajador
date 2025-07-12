package com.example.proyectoapptrabajador.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoapptrabajador.data.model.Cita
import com.example.proyectoapptrabajador.data.model.MensajeChat
import com.example.proyectoapptrabajador.data.model.MensajeChatRequest
import com.example.proyectoapptrabajador.data.model.TrabajadorDetalle
import com.example.proyectoapptrabajador.repositories.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: AppRepository) : ViewModel() {

    private val _messages = MutableLiveData<List<MensajeChat>>()
    val messages: LiveData<List<MensajeChat>> = _messages

    private val _appointmentDetails = MutableLiveData<Cita>()
    val appointmentDetails: LiveData<Cita> = _appointmentDetails

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _workerDetails = MutableLiveData<TrabajadorDetalle>()
    val workerDetails: LiveData<TrabajadorDetalle> = _workerDetails

    private var pollingJob: Job? = null

    fun startPollingMessages(appointmentId: Int) {
        pollingJob?.cancel() // Cancela cualquier sondeo anterior
        pollingJob = viewModelScope.launch {
            while (true) {
                fetchMessages(appointmentId)
                delay(30000) // Espera 30 segundos
            }
        }
    }

    fun fetchAppointmentDetails(appointmentId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getAppointmentDetails(appointmentId)
                if (response.isSuccessful) {
                    _appointmentDetails.value = response.body()
                } else {
                    _errorMessage.value = "Error al obtener detalles de la cita."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión."
            }
        }
    }

    private fun fetchMessages(appointmentId: Int) {
        viewModelScope.launch {
            try {
                android.util.Log.d("ChatViewModel", "Intentando obtener mensajes para cita ID: $appointmentId")
                val response = repository.getChatMessages(appointmentId)
                if (response.isSuccessful) {
                    val messages = response.body()
                    android.util.Log.d("ChatViewModel", "Mensajes obtenidos: ${messages?.size ?: 0}")
                    _messages.value = messages
                } else {
                    android.util.Log.e("ChatViewModel", "Error en respuesta: ${response.code()} - ${response.message()}")
                    _errorMessage.value = "Error al cargar mensajes."
                }
            } catch (e: Exception) {
                android.util.Log.e("ChatViewModel", "Excepción al obtener mensajes: ${e.message}")
                _errorMessage.value = "Error de conexión."
            }
        }
    }

    fun sendMessage(appointmentId: Int, messageText: String, receiverId: Int) {
        viewModelScope.launch {
            android.util.Log.d("ChatViewModel", "=== INICIANDO ENVÍO DE MENSAJE ===")
            android.util.Log.d("ChatViewModel", "Appointment ID: $appointmentId")
            android.util.Log.d("ChatViewModel", "Mensaje: '$messageText'")
            android.util.Log.d("ChatViewModel", "Receiver ID: $receiverId")

            val request = MensajeChatRequest(messageText, receiverId)
            android.util.Log.d("ChatViewModel", "Request creado: message='${request.message}', receiver_id=${request.receiverId}")

            try {
                android.util.Log.d("ChatViewModel", "Llamando a repository.sendChatMessage...")
                val response = repository.sendChatMessage(appointmentId, request)
                android.util.Log.d("ChatViewModel", "Respuesta recibida - Código: ${response.code()}")
                android.util.Log.d("ChatViewModel", "Respuesta exitosa: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    android.util.Log.d("ChatViewModel", "✅ Mensaje enviado exitosamente")
                    android.util.Log.d("ChatViewModel", "Refrescando mensajes...")
                    // Refresca los mensajes inmediatamente después de enviar
                    fetchMessages(appointmentId)
                } else {
                    android.util.Log.e("ChatViewModel", "❌ Error al enviar mensaje - Código: ${response.code()}")
                    android.util.Log.e("ChatViewModel", "Error body: ${response.errorBody()?.string()}")
                    _errorMessage.value = "No se pudo enviar el mensaje."
                }
            } catch (e: Exception) {
                android.util.Log.e("ChatViewModel", "❌ Excepción al enviar mensaje: ${e.message}")
                android.util.Log.e("ChatViewModel", "Stack trace: ${e.stackTrace.contentToString()}")
                _errorMessage.value = "Error de conexión."
            }
            android.util.Log.d("ChatViewModel", "=== FIN ENVÍO DE MENSAJE ===")
        }
    }

    fun fetchWorkerDetails(workerId: Int) {
        viewModelScope.launch {
            try {
                android.util.Log.d("ChatViewModel", "Intentando obtener detalles del trabajador ID: $workerId")
                val response = repository.getWorkerDetail(workerId)
                if (response.isSuccessful && response.body() != null) {
                    val worker = response.body()!!
                    android.util.Log.d("ChatViewModel", "Trabajador obtenido: ${worker.user.name} ${worker.user.lastName}")
                    _workerDetails.value = worker
                } else {
                    android.util.Log.e("ChatViewModel", "Error obteniendo trabajador: ${response.code()} - ${response.message()}")
                    _errorMessage.value = "Error al obtener detalles del trabajador."
                }
            } catch (e: Exception) {
                android.util.Log.e("ChatViewModel", "Excepción obteniendo trabajador: ${e.message}")
                _errorMessage.value = "Error de conexión."
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel() // Detiene el sondeo cuando el ViewModel se destruye
    }
}

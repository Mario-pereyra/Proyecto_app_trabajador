package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectoapptrabajador.databinding.FragmentChatBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.adapters.ChatAdapter
import com.example.proyectoapptrabajador.ui.viewmodels.ChatViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val args: ChatFragmentArgs by navArgs()

    private val chatViewModel: ChatViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        setupObservers()

        // Iniciar el chat con el ID de la cita
        chatViewModel.startChat(args.appointmentId)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter { senderId ->
            chatViewModel.isMyMessage(senderId)
        }

        binding.rvMensajes.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true // Los mensajes nuevos aparecen abajo
            }
            adapter = chatAdapter
        }
    }

    private fun setupClickListeners() {
        // Botón volver
        binding.btnVolver.setOnClickListener {
            findNavController().navigateUp()
        }

        // Botón enviar mensaje
        binding.btnEnviar.setOnClickListener {
            val mensaje = binding.etMensaje.text.toString().trim()
            if (mensaje.isNotEmpty()) {
                chatViewModel.sendMessage(mensaje)
                binding.etMensaje.text?.clear()
            }
        }

        // Botón mapa (si hay coordenadas)
        binding.btnMapa.setOnClickListener {
            val citaDetails = chatViewModel.citaDetails.value
            if (citaDetails?.latitude != null && citaDetails.longitude != null) {
                val action = ChatFragmentDirections.actionChatFragmentToMapaFragment(
                    citaDetails.latitude,
                    citaDetails.longitude
                )
                findNavController().navigate(action)
            } else {
                Toast.makeText(context, "No hay ubicación disponible para esta cita", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón concretar cita (para citas solicitadas - status 1)
        binding.btnConcretarCita.setOnClickListener {
            val citaDetails = chatViewModel.citaDetails.value
            if (citaDetails != null) {
                val action = ChatFragmentDirections.actionChatFragmentToConfirmAppointmentDialog(citaDetails.id)
                findNavController().navigate(action)
            }
        }

        // Botón finalizar trabajo (para citas aceptadas - status 2)
        binding.btnFinalizarTrabajo.setOnClickListener {
            val citaDetails = chatViewModel.citaDetails.value
            if (citaDetails != null) {
                val action = ChatFragmentDirections.actionChatFragmentToFinalizeAppointmentDialog(citaDetails.id)
                findNavController().navigate(action)
            }
        }
    }

    private fun setupObservers() {
        // Observar mensajes del chat
        chatViewModel.mensajes.observe(viewLifecycleOwner) { mensajes ->
            chatAdapter.updateMensajes(mensajes)

            // Scroll hacia abajo cuando lleguen mensajes nuevos
            if (mensajes.isNotEmpty()) {
                binding.rvMensajes.scrollToPosition(mensajes.size - 1)
            }
        }

        // Observar detalles de la cita
        chatViewModel.citaDetails.observe(viewLifecycleOwner) { cita ->
            binding.tvClienteNombre.text = "${cita.client.name} ${cita.client.profile.last_name}"
            binding.tvCategoria.text = cita.category.name

            // Mostrar/ocultar botón de mapa según disponibilidad de coordenadas
            binding.btnMapa.visibility = if (cita.latitude != null && cita.longitude != null) {
                View.VISIBLE
            } else {
                View.GONE
            }

            // Mostrar botones según el estado de la cita
            when (cita.status) {
                0 -> {
                    // Cita pendiente - solo chat disponible
                    binding.btnConcretarCita.visibility = View.GONE
                    binding.btnFinalizarTrabajo.visibility = View.GONE
                }
                1 -> {
                    // Cita solicitada - mostrar botón de concretar prominente
                    binding.btnConcretarCita.visibility = View.VISIBLE
                    binding.btnFinalizarTrabajo.visibility = View.GONE
                    // Hacer el botón más visible con animación
                    binding.btnConcretarCita.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(200)
                        .withEndAction {
                            binding.btnConcretarCita.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(200)
                                .start()
                        }
                        .start()
                }
                2 -> {
                    // Cita concretada - mostrar botón de finalizar
                    binding.btnConcretarCita.visibility = View.GONE
                    binding.btnFinalizarTrabajo.visibility = View.VISIBLE
                    // Hacer el botón más visible con animación
                    binding.btnFinalizarTrabajo.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(200)
                        .withEndAction {
                            binding.btnFinalizarTrabajo.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(200)
                                .start()
                        }
                        .start()
                }
                3 -> {
                    // Cita finalizada - ocultar todos los botones
                    binding.btnConcretarCita.visibility = View.GONE
                    binding.btnFinalizarTrabajo.visibility = View.GONE
                }
                else -> {
                    // Otros estados - ocultar ambos botones
                    binding.btnConcretarCita.visibility = View.GONE
                    binding.btnFinalizarTrabajo.visibility = View.GONE
                }
            }
        }

        // Observar estado de carga
        chatViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observar estado de envío de mensaje
        chatViewModel.isSendingMessage.observe(viewLifecycleOwner) { isSending ->
            binding.btnEnviar.isEnabled = !isSending
            binding.etMensaje.isEnabled = !isSending
        }

        // Observar confirmación de mensaje enviado
        chatViewModel.messageSent.observe(viewLifecycleOwner) { sent ->
            if (sent) {
                // El mensaje se envió exitosamente, el EditText ya se limpió
                Toast.makeText(context, "Mensaje enviado", Toast.LENGTH_SHORT).show()
            }
        }

        // Observar errores
        chatViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Detener el auto-refresh cuando se sale del fragment
        chatViewModel.stopAutoRefresh()

        _binding = null
    }
}

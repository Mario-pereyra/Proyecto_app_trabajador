package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentChatBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.adapters.ChatAdapter
import com.example.proyectoapptrabajador.ui.viewmodels.ChatViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.WorkerActionsViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    private val workerActionsViewModel: WorkerActionsViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    private val args: ChatFragmentArgs by navArgs()
    private lateinit var chatAdapter: ChatAdapter
    private var currentAppointment: com.example.proyectoapptrabajador.data.model.Cita? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Habilitar menú para el ícono de finalizar
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar con un valor temporal, se actualizará cuando lleguen los appointmentDetails
        setupRecyclerView(1) // Valor temporal
        setupObservers()
        setupEventListeners()

        // Obtener detalles completos de la cita para determinar qué mostrar
        viewModel.fetchAppointmentDetails(args.appointmentId)
        viewModel.startPollingMessages(args.appointmentId)

        Log.d("ChatFragment", "Abriendo chat para appointmentId: ${args.appointmentId}")
    }

    private fun setupRecyclerView(currentUserId: Int) {
        chatAdapter = ChatAdapter(emptyList(), currentUserId)
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true // Para que la lista empiece desde abajo
        }
        binding.rvChat.adapter = chatAdapter
    }

    private fun setupObservers() {
        // Observer para los detalles completos de la cita
        viewModel.appointmentDetails.observe(viewLifecycleOwner) { cita ->
            currentAppointment = cita
            Log.d("ChatFragment", "=== INFORMACIÓN DE LA CITA (TRABAJADOR) ===")
            Log.d("ChatFragment", "ID de la cita: ${cita.id}")
            Log.d("ChatFragment", "ID del CLIENTE: ${cita.userId}")
            Log.d("ChatFragment", "ID del TRABAJADOR: ${cita.workerId}")
            Log.d("ChatFragment", "Status de la cita: ${cita.status}")

            // Actualizar el adapter con el userId correcto
            setupRecyclerView(cita.workerId) // El trabajador ve sus propios mensajes

            // Si ya hay mensajes, actualizar el adapter
            val currentMessages = viewModel.messages.value
            if (!currentMessages.isNullOrEmpty()) {
                chatAdapter.updateData(currentMessages)
            }

            // Mostrar información del cliente en lugar del trabajador
            binding.txtWorkerName.text = cita.client?.name ?: "Cliente"

            // Cargar foto del cliente si está disponible
            // Por ahora usamos un placeholder ya que el cliente no tiene foto en el modelo
            binding.imgWorkerPhoto.setImageResource(R.mipmap.ic_launcher)

            // Lógica específica del trabajador según el estado de la cita
            handleAppointmentStatus(cita)
        }

        // Observer para mensajes
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            Log.d("ChatFragment", "Mensajes cargados: ${messages?.size ?: 0}")
            chatAdapter.updateData(messages)
            if (messages.isNotEmpty()) {
                binding.rvChat.scrollToPosition(messages.size - 1)
            }
        }

        // Observer para acciones del trabajador
        workerActionsViewModel.confirmStatus.observe(viewLifecycleOwner) { confirmed ->
            if (confirmed == true) {
                Toast.makeText(requireContext(), "Cita confirmada exitosamente", Toast.LENGTH_SHORT).show()
                // Recargar detalles de la cita para actualizar el estado
                viewModel.fetchAppointmentDetails(args.appointmentId)
                workerActionsViewModel.clearStatus()
            }
        }

        workerActionsViewModel.finalizeStatus.observe(viewLifecycleOwner) { finalized ->
            if (finalized == true) {
                Toast.makeText(requireContext(), "Trabajo finalizado exitosamente", Toast.LENGTH_SHORT).show()
                // Recargar detalles de la cita para actualizar el estado
                viewModel.fetchAppointmentDetails(args.appointmentId)
                workerActionsViewModel.clearStatus()
            }
        }

        workerActionsViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Log.e("ChatFragment", "Error: $it")
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleAppointmentStatus(cita: com.example.proyectoapptrabajador.data.model.Cita) {
        when (cita.status) {
            1 -> { // CONCRETADA POR CLIENTE
                // Mostrar popup para confirmar la cita
                showConfirmAppointmentDialog(cita)
            }
            2 -> { // CONFIRMADA POR TRABAJADOR
                // Mostrar ícono de finalizar en el menú
                requireActivity().invalidateOptionsMenu()
            }
            3 -> { // FINALIZADA
                // No mostrar acciones adicionales
                requireActivity().invalidateOptionsMenu()
            }
        }
    }

    private fun showConfirmAppointmentDialog(cita: com.example.proyectoapptrabajador.data.model.Cita) {
        AlertDialog.Builder(requireContext())
            .setTitle("Concretar cita")
            .setMessage("El cliente ha concretado una cita para ${cita.appointmentDate} a las ${cita.appointmentTime}. ¿Confirmas que puedes realizar el trabajo?")
            .setPositiveButton("Sí") { _, _ ->
                workerActionsViewModel.confirmAppointment(cita.id)
            }
            .setNeutralButton("Ver ubicación") { _, _ ->
                // Navegar al mapa para ver la ubicación
                if (cita.latitude != null && cita.longitude != null) {
                    val action = ChatFragmentDirections.actionChatFragmentToMapFragment(
                        cita.id,
                        cita.latitude.toFloat(),
                        cita.longitude.toFloat()
                    )
                    findNavController().navigate(action)
                }
            }
            .setNegativeButton("No") { _, _ ->
                // El trabajador rechaza la cita (aquí se podría implementar lógica adicional)
                Toast.makeText(requireContext(), "Cita no confirmada", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Solo mostrar el menú si la cita está confirmada por el trabajador
        if (currentAppointment?.status == 2) {
            inflater.inflate(R.menu.chat_worker_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_finalize_work -> {
                showFinalizeWorkDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFinalizeWorkDialog() {
        currentAppointment?.let { cita ->
            AlertDialog.Builder(requireContext())
                .setTitle("Finalizar trabajo")
                .setMessage("¿Has completado el trabajo? Esta acción marcará la cita como finalizada.")
                .setPositiveButton("Sí") { _, _ ->
                    workerActionsViewModel.finalizeAppointment(cita.id)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun setupEventListeners() {
        binding.btnSendMessage.setOnClickListener {
            val message = binding.editTxtMessage.text.toString()
            Log.d("ChatFragment", "Botón enviar presionado. Mensaje: '$message'")

            if (message.isNotEmpty()) {
                Log.d("ChatFragment", "Mensaje no está vacío, intentando enviar...")

                // El receiver_id siempre es el workerId ya que esta es la app del cliente
                var receiverId: Int? = null

                // Intentar obtener workerId desde los mensajes existentes primero
                val messages = viewModel.messages.value
                if (!messages.isNullOrEmpty()) {
                    receiverId = messages.first().appointment.workerId
                    Log.d("ChatFragment", "Worker ID obtenido desde mensajes: $receiverId")
                } else {
                    // Si no hay mensajes, usar appointmentDetails
                    val appointmentDetails = viewModel.appointmentDetails.value
                    if (appointmentDetails != null) {
                        receiverId = appointmentDetails.workerId
                        Log.d("ChatFragment", "Worker ID obtenido desde appointmentDetails: $receiverId")
                    }
                }

                if (receiverId != null) {
                    Log.d("ChatFragment", "Enviando mensaje: '$message' al trabajador: $receiverId")
                    viewModel.sendMessage(args.appointmentId, message, receiverId)
                    binding.editTxtMessage.text.clear()
                    Log.d("ChatFragment", "Campo de texto limpiado")
                } else {
                    Log.e("ChatFragment", "ERROR: No se pudo determinar el workerId")
                    Toast.makeText(context, "Error: No se puede enviar el mensaje", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.w("ChatFragment", "ADVERTENCIA: El mensaje está vacío, no se envía")
            }
        }

        binding.btnConcretar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Concretar Cita")
                .setMessage("¿Está seguro que desea concretar una cita?")
                .setPositiveButton("Si") { _, _ ->
                    // Navegar al mapa - en la app del trabajador, esto no debería usarse
                    // ya que el trabajador no concreta citas, solo las confirma
                    // Pero si necesitas navegar al mapa, usa coordenadas por defecto
                    val action = ChatFragmentDirections.actionChatFragmentToMapFragment(
                        args.appointmentId,
                        0f, // latitude por defecto
                        0f  // longitude por defecto
                    )
                    findNavController().navigate(action)
                }
                .setNegativeButton("No", null)
                .show()
        }

        binding.btnCalificar.setOnClickListener {
            val dialog = ReviewDialogFragment.Companion.newInstance(args.appointmentId)
            dialog.show(childFragmentManager, "ReviewDialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

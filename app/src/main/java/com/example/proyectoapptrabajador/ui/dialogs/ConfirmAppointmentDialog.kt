package com.example.proyectoapptrabajador.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyectoapptrabajador.databinding.DialogConfirmAppointmentBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.viewmodels.AppointmentDialogViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class ConfirmAppointmentDialog : DialogFragment() {

    private var _binding: DialogConfirmAppointmentBinding? = null
    private val binding get() = _binding!!

    private val args: ConfirmAppointmentDialogArgs by navArgs()

    private val viewModel: AppointmentDialogViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogConfirmAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()

        // Cargar detalles de la cita
        viewModel.loadAppointmentDetails(args.appointmentId)
    }

    private fun setupObservers() {
        viewModel.appointmentDetails.observe(viewLifecycleOwner) { cita ->
            Log.d("ConfirmAppointmentDialog", "=== ACTUALIZANDO UI CON DETALLES DE CITA ===")
            Log.d("ConfirmAppointmentDialog", "Cita recibida: $cita")

            binding.tvClienteNombre.text = "Cliente: ${cita.client.name} ${cita.client.profile.last_name}"
            binding.tvCategoria.text = "Servicio: ${cita.category.name}"

            // Mostrar fecha y hora de la cita
            val fechaHora = if (cita.appointment_date != null && cita.appointment_time != null) {
                "${formatearFecha(cita.appointment_date)} - ${formatearHora(cita.appointment_time)}"
            } else {
                "Fecha y hora por definir"
            }
            binding.tvFechaHora.text = "Fecha: $fechaHora"

            // Mostrar/ocultar botón de ubicación según disponibilidad de coordenadas
            if (cita.latitude != null && cita.longitude != null) {
                binding.btnVerUbicacion.visibility = View.VISIBLE
                Log.d("ConfirmAppointmentDialog", "Botón Ver Ubicación habilitado - Lat: ${cita.latitude}, Lng: ${cita.longitude}")
            } else {
                binding.btnVerUbicacion.visibility = View.GONE
                Log.d("ConfirmAppointmentDialog", "Botón Ver Ubicación deshabilitado - No hay coordenadas")
            }
        }

        viewModel.confirmResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "¡Cita confirmada exitosamente!", Toast.LENGTH_LONG).show()
                dismiss()
                // Navegar de vuelta al fragmento de citas para que se actualice la lista
                findNavController().navigateUp()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSi.isEnabled = !isLoading
            binding.btnNo.isEnabled = !isLoading
            binding.btnVerUbicacion.isEnabled = !isLoading
            binding.btnSi.text = if (isLoading) "Confirmando..." else "Sí, Confirmar"
        }
    }

    private fun setupClickListeners() {
        binding.btnVerUbicacion.setOnClickListener {
            val cita = viewModel.appointmentDetails.value
            if (cita?.latitude != null && cita.longitude != null) {
                Log.d("ConfirmAppointmentDialog", "Navegando al mapa con coordenadas: ${cita.latitude}, ${cita.longitude}")
                val action = ConfirmAppointmentDialogDirections
                    .actionConfirmAppointmentDialogToMapaFragment(cita.latitude, cita.longitude)
                findNavController().navigate(action)
            } else {
                Toast.makeText(context, "No hay ubicación disponible para esta cita", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSi.setOnClickListener {
            Log.d("ConfirmAppointmentDialog", "Usuario confirmó - Ejecutando confirmación de cita")
            viewModel.confirmAppointment(args.appointmentId)
        }

        binding.btnNo.setOnClickListener {
            Log.d("ConfirmAppointmentDialog", "Usuario canceló - Cerrando diálogo")
            dismiss()
        }
    }

    private fun formatearFecha(fecha: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(fecha)
            outputFormat.format(date ?: return fecha)
        } catch (e: Exception) {
            fecha
        }
    }

    private fun formatearHora(hora: String): String {
        return try {
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = inputFormat.parse(hora)
            outputFormat.format(time ?: return hora)
        } catch (e: Exception) {
            hora
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

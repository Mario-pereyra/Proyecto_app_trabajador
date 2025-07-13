package com.example.proyectoapptrabajador.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.proyectoapptrabajador.databinding.DialogFinalizeAppointmentBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.viewmodels.AppointmentDialogViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class FinalizeAppointmentDialog : DialogFragment() {

    private var _binding: DialogFinalizeAppointmentBinding? = null
    private val binding get() = _binding!!

    private val args: FinalizeAppointmentDialogArgs by navArgs()

    private val viewModel: AppointmentDialogViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFinalizeAppointmentBinding.inflate(inflater, container, false)
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
            binding.tvClienteNombre.text = "Cliente: ${cita.client.name} ${cita.client.profile.last_name}"
            binding.tvCategoria.text = "Servicio: ${cita.category.name}"
        }

        viewModel.finalizeResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "¡Trabajo finalizado exitosamente! El cliente podrá calificarte ahora.", Toast.LENGTH_LONG).show()
                dismiss()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnFinalizar.isEnabled = !isLoading
            binding.btnFinalizar.text = if (isLoading) "Finalizando..." else "✓ Finalizar"
        }
    }

    private fun setupClickListeners() {
        binding.btnFinalizar.setOnClickListener {
            viewModel.finalizeAppointment(args.appointmentId)
        }

        binding.btnCancelar.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

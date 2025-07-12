package com.example.proyectoapptrabajador.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyectoapptrabajador.data.model.ConcretarCitaRequest
import com.example.proyectoapptrabajador.databinding.FragmentDateTimePickerBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.viewmodels.DateTimePickerViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class DateTimePickerFragment : Fragment() {

    private var _binding: FragmentDateTimePickerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DateTimePickerViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }
    private val args: DateTimePickerFragmentArgs by navArgs()

    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDateTimePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventListeners()
        setupObservers()
    }

    private fun setupEventListeners() {
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }
        binding.btnSelectTime.setOnClickListener {
            showTimePicker()
        }
        binding.btnConcretarCita.setOnClickListener {
            if (selectedDate != null && selectedTime != null) {
                val request = ConcretarCitaRequest(
                    appointmentDate = selectedDate!!,
                    appointmentTime = selectedTime!!,
                    latitude = args.latitude.toString(), // Convertir Float a String
                    longitude = args.longitude.toString() // Convertir Float a String
                )

                // LOG: Información que se va a enviar
                Log.d("DateTimePickerFragment", "=== CONCRETAR CITA ===")
                Log.d("DateTimePickerFragment", "Appointment ID: ${args.appointmentId}")
                Log.d("DateTimePickerFragment", "Fecha seleccionada: ${selectedDate}")
                Log.d("DateTimePickerFragment", "Hora seleccionada: ${selectedTime}")
                Log.d("DateTimePickerFragment", "Latitud: ${args.latitude}")
                Log.d("DateTimePickerFragment", "Longitud: ${args.longitude}")
                Log.d("DateTimePickerFragment", "Request completo: $request")

                viewModel.makeAppointment(args.appointmentId, request)
            } else {
                Toast.makeText(context, "Por favor, seleccione fecha y hora", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.appointmentMadeStatus.observe(viewLifecycleOwner) { isMade ->
            if (isMade) {
                Toast.makeText(context, "Cita concretada exitosamente", Toast.LENGTH_LONG).show()
                findNavController().navigate(DateTimePickerFragmentDirections.actionDateTimePickerFragmentToAppointmentsFragment())
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = dateFormat.format(selectedCalendar.time)
                binding.txtSelectedDate.text = "Fecha: $selectedDate"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minute)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // Cambiar formato sin segundos
                selectedTime = timeFormat.format(selectedCalendar.time)
                binding.txtSelectedTime.text = "Hora: $selectedTime"
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        )
        timePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

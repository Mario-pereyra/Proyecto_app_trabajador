package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentMisCitasBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.adapters.CitaAdapter
import com.example.proyectoapptrabajador.ui.viewmodels.CitasViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class MisCitasFragment : Fragment() {

    private var _binding: FragmentMisCitasBinding? = null
    private val binding get() = _binding!!

    private val citasViewModel: CitasViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    private lateinit var citaAdapter: CitaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisCitasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Cargar las citas al iniciar
        citasViewModel.loadCitas()
    }

    private fun setupClickListeners() {
        // Botón de perfil en el header - navega a HomeTrabajador
        binding.btnPerfil.setOnClickListener {
            findNavController().navigate(R.id.action_misCitasFragment_to_homeTrabajadorFragment)
        }
    }

    private fun setupRecyclerView() {
        citaAdapter = CitaAdapter(
            onCitaClick = { cita ->
                // Navegar al chat de la cita seleccionada
                val action = MisCitasFragmentDirections.actionMisCitasFragmentToChatFragment(cita.id)
                findNavController().navigate(action)
            },
            onConfirmClick = { cita ->
                // Mostrar popup de confirmación
                val action = MisCitasFragmentDirections.actionMisCitasFragmentToConfirmAppointmentDialog(cita.id)
                findNavController().navigate(action)
            },
            onFinalizeClick = { cita ->
                // Mostrar popup de finalización
                val action = MisCitasFragmentDirections.actionMisCitasFragmentToFinalizeAppointmentDialog(cita.id)
                findNavController().navigate(action)
            }
        )

        binding.rvCitas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = citaAdapter
        }
    }

    private fun setupObservers() {
        citasViewModel.citas.observe(viewLifecycleOwner) { citas ->
            citaAdapter.updateCitas(citas)
            binding.tvNoCitas.visibility = if (citas.isEmpty()) View.VISIBLE else View.GONE
        }

        citasViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        citasViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

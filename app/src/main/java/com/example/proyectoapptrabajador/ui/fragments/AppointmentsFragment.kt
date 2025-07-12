package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentAppointmentsBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.adapters.CitaAdapter
import com.example.proyectoapptrabajador.ui.viewmodels.AppointmentsViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class AppointmentsFragment : Fragment() {

    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppointmentsViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    private lateinit var citaAdapter: CitaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Habilitar menú
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar la toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupObservers()
        viewModel.fetchAppointments()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.appointments_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_profile -> {
                // Navegar a la pantalla de editar perfil
                val action = AppointmentsFragmentDirections.actionAppointmentsFragmentToEditProfileFragment()
                findNavController().navigate(action)
                true
            }
            R.id.action_logout -> {
                // Implementar logout
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        // Aquí puedes implementar la lógica de logout
        // Por ejemplo, limpiar el token y navegar al login
        Toast.makeText(requireContext(), "Cerrando sesión...", Toast.LENGTH_SHORT).show()
        // TODO: Implementar navegación al login con clearTask
    }

    private fun setupRecyclerView() {
        citaAdapter = CitaAdapter(
            emptyList(),
            onItemClick = { cita ->
                val action = AppointmentsFragmentDirections.actionAppointmentsFragmentToChatFragment(cita.id)
                findNavController().navigate(action)
            },
            onViewLocationClick = { cita ->
                // Navegar al mapa para mostrar la ubicación de la cita
                val action = AppointmentsFragmentDirections.actionAppointmentsFragmentToMapFragment(
                    cita.id,
                    cita.latitude?.toFloat() ?: 0f,
                    cita.longitude?.toFloat() ?: 0f
                )
                findNavController().navigate(action)
            }
        )
        binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAppointments.adapter = citaAdapter
    }

    private fun setupObservers() {
        viewModel.appointments.observe(viewLifecycleOwner) { appointments ->
            citaAdapter.updateData(appointments)
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

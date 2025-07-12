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
import com.example.proyectoapptrabajador.databinding.FragmentMisCitasBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.adapters.CitaTrabajadorAdapter
import com.example.proyectoapptrabajador.ui.viewmodels.MisCitasViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class MisCitasFragment : Fragment() {

    private var _binding: FragmentMisCitasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MisCitasViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    private lateinit var adapter: CitaTrabajadorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Habilitar menú
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisCitasBinding.inflate(inflater, container, false)
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
                val action = MisCitasFragmentDirections.actionMisCitasFragmentToEditProfileFragment()
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
        adapter = CitaTrabajadorAdapter(
            appointments = emptyList(),
            onItemClick = { cita ->
                val action = MisCitasFragmentDirections.actionMisCitasFragmentToChatFragment(cita.id)
                findNavController().navigate(action)
            }
        )
        binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAppointments.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.appointments.observe(viewLifecycleOwner) { appointments ->
            adapter.updateData(appointments)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            // Mostrar error
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

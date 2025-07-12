package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectoapptrabajador.databinding.FragmentWorkersBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.adapters.TrabajadorAdapter
import com.example.proyectoapptrabajador.ui.viewmodels.WorkersViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class WorkersFragment : Fragment() {

    private var _binding: FragmentWorkersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkersViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    private val args: WorkersFragmentArgs by navArgs()
    private lateinit var trabajadorAdapter: TrabajadorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupObservers()

        // Pedimos los trabajadores de la categoría que recibimos como argumento
        viewModel.fetchWorkers(args.categoryId)
    }

    private fun setupRecyclerView() {
        trabajadorAdapter = TrabajadorAdapter(emptyList()) { trabajador ->
            val action = com.example.proyectoapptrabajador.ui.fragments.WorkersFragmentDirections.actionWorkersFragmentToWorkerDetailFragment(
                trabajador.id,
                args.categoryId
            )
            findNavController().navigate(action)
        }
        binding.rvWorkers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWorkers.adapter = trabajadorAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterWorkers(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupObservers() {
        viewModel.workers.observe(viewLifecycleOwner) { workers ->
            trabajadorAdapter.updateData(workers)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

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
import com.example.proyectoapptrabajador.databinding.FragmentSelectCategoriesBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.adapters.CategoriesSelectionAdapter
import com.example.proyectoapptrabajador.ui.viewmodels.SelectCategoriesViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class SelectCategoriesFragment : Fragment() {

    private var _binding: FragmentSelectCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SelectCategoriesViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    private lateinit var categoriesAdapter: CategoriesSelectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupEventListeners()
    }

    private fun setupRecyclerView() {
        categoriesAdapter = CategoriesSelectionAdapter { categoryId ->
            viewModel.toggleCategory(categoryId)
        }
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = categoriesAdapter
    }

    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoriesAdapter.updateData(categories)
        }

        viewModel.selectedCategories.observe(viewLifecycleOwner) { selectedIds ->
            categoriesAdapter.updateSelection(selectedIds)
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Categorías actualizadas correctamente", Toast.LENGTH_SHORT).show()
                val action = SelectCategoriesFragmentDirections.actionSelectCategoriesFragmentToProfilePictureFragment()
                findNavController().navigate(action)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupEventListeners() {
        binding.btnContinue.setOnClickListener {
            // Por ahora usamos un ID fijo del trabajador
            // En una implementación real, esto debería venir del registro exitoso
            val workerId = 1 // TODO: Obtener del registro real
            viewModel.updateWorkerCategories(workerId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

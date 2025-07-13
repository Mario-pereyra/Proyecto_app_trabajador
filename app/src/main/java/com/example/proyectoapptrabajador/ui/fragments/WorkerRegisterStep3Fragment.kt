package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentWorkerRegisterStep3Binding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.adapters.OcupacionAdapter
import com.example.proyectoapptrabajador.ui.viewmodels.WorkerRegisterViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class WorkerRegisterStep3Fragment : Fragment() {

    private var _binding: FragmentWorkerRegisterStep3Binding? = null
    private val binding get() = _binding!!

    private val registerViewModel: WorkerRegisterViewModel by activityViewModels {
        ViewModelFactory(MainActivity.repository)
    }
    private lateinit var ocupacionAdapter: OcupacionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerRegisterStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        registerViewModel.fetchCategories()

        binding.btnFinalizarRegistro.setOnClickListener {
            registerViewModel.registerWorker(requireActivity().contentResolver)
        }
    }

    private fun setupRecyclerView() {
        ocupacionAdapter = OcupacionAdapter(
            emptyList(),
            isCategorySelected = { categoryId -> registerViewModel.isCategorySelected(categoryId) },
            onCategorySelected = { categoryId, isChecked -> registerViewModel.onCategorySelected(categoryId, isChecked) }
        )
        binding.rvOcupaciones.adapter = ocupacionAdapter
    }

    private fun setupObservers() {
        registerViewModel.categoriesList.observe(viewLifecycleOwner) { categories ->
            ocupacionAdapter.updateData(categories)
        }

        registerViewModel.registrationResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_workerRegisterStep3Fragment_to_misCitasFragment)
            }
        }

        registerViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
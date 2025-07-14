package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.util.Log
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

        binding.btnAgregarOcupacion.setOnClickListener {
            showAddCategoryDialog()
        }

        binding.btnFinalizarRegistro.setOnClickListener {
            // Log del estado final antes de hacer las peticiones
            Log.d("WorkerRegisterStep3", "=== DATOS FINALES ANTES DE PETICIONES ===")
            Log.d("WorkerRegisterStep3", "Name: ${registerViewModel.name.value}")
            Log.d("WorkerRegisterStep3", "LastName: ${registerViewModel.lastName.value}")
            Log.d("WorkerRegisterStep3", "Email: ${registerViewModel.email.value}")
            Log.d("WorkerRegisterStep3", "Password: ${registerViewModel.password.value}")
            Log.d("WorkerRegisterStep3", "ProfilePictureUri: ${registerViewModel.profilePictureUri.value}")
            Log.d("WorkerRegisterStep3", "Selected Categories: ${registerViewModel.getSelectedCategoriesDebug()}")
            Log.d("WorkerRegisterStep3", "==========================================")

            // Asignar categorías con token
            registerViewModel.assignCategories()
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

        registerViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnFinalizarRegistro.isEnabled = !isLoading
            binding.btnFinalizarRegistro.text = if (isLoading) "Asignando categorías..." else "Finalizar Registro"
        }

        registerViewModel.categoriesAssignResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "¡Ocupaciones actualizadas exitosamente!", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_workerRegisterStep3Fragment_to_homeTrabajadorFragment)
            }
        }

        registerViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddCategoryDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val editText = android.widget.EditText(requireContext())
        editText.hint = "Nombre de la ocupación"
        editText.setPadding(50, 30, 50, 30)

        builder.setTitle("Agregar nueva ocupación")
        builder.setView(editText)

        builder.setPositiveButton("Agregar") { _, _ ->
            val categoryName = editText.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                registerViewModel.createCategory(categoryName)
            } else {
                Toast.makeText(context, "Ingresa un nombre para la ocupación", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
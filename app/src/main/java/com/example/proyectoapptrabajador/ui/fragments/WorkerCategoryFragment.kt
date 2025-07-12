package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectoapptrabajador.databinding.FragmentWorkerCategoryBinding
import com.example.proyectoapptrabajador.ui.adapters.CategoriaAdapter
import com.example.proyectoapptrabajador.ui.viewmodels.WorkerRegisterViewModel

class WorkerCategoryFragment : Fragment() {
    private var _binding: FragmentWorkerCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WorkerRegisterViewModel by activityViewModels()
    private lateinit var adapter: CategoriaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CategoriaAdapter(emptyList()) { selectedIds ->
            viewModel.saveSelectedCategories(selectedIds)
        }
        binding.recyclerCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategories.adapter = adapter

        // Obtener categorías desde el ViewModel
        viewModel.getCategories { categorias ->
            adapter.updateData(categorias)
        }

        binding.btnFinishRegister.setOnClickListener {
            viewModel.finalizarRegistro()
        }

        viewModel.registrationStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                // Aquí puedes navegar a la pantalla principal del trabajador
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

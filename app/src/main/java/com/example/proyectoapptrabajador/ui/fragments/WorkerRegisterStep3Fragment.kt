package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapptrabajador.databinding.FragmentWorkerRegisterStep3Binding
import com.example.proyectoapptrabajador.databinding.ItemCategoryCheckboxBinding
import com.example.proyectoapptrabajador.ui.viewmodels.WorkerRegisterViewModel
import com.example.proyectoapptrabajador.data.model.Category

class WorkerRegisterStep3Fragment : Fragment() {
    private var _binding: FragmentWorkerRegisterStep3Binding? = null
    private val binding get() = _binding!!
    private val viewModel: WorkerRegisterViewModel by activityViewModels()
    private lateinit var adapter: CategoryMultiSelectAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkerRegisterStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CategoryMultiSelectAdapter { selectedIds ->
            viewModel.selectedCategories.value = selectedIds
        }
        binding.recyclerCategorias.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategorias.adapter = adapter

        // Observa las categorías desde el ViewModel y actualiza el adapter
        viewModel.categories.observe(viewLifecycleOwner) { categorias ->
            adapter.setCategories(categorias)
        }

        binding.btnFinalizarRegistro.setOnClickListener {
            viewModel.registerWorker()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Adapter para selección múltiple de categorías
class CategoryMultiSelectAdapter(
    private val onSelectionChanged: (List<Int>) -> Unit
) : RecyclerView.Adapter<CategoryMultiSelectAdapter.CategoryViewHolder>() {
    private var categories: List<Category> = emptyList()
    private val selectedIds = mutableSetOf<Int>()

    fun setCategories(list: List<Category>) {
        categories = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryCheckboxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.checkBox.text = category.name
        holder.binding.checkBox.isChecked = selectedIds.contains(category.id)
        holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedIds.add(category.id) else selectedIds.remove(category.id)
            onSelectionChanged(selectedIds.toList())
        }
    }

    override fun getItemCount() = categories.size

    class CategoryViewHolder(val binding: ItemCategoryCheckboxBinding) : RecyclerView.ViewHolder(binding.root)
}

package com.example.proyectoapptrabajador.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapptrabajador.data.model.Category
import com.example.proyectoapptrabajador.databinding.ItemOcupacionSelectableBinding

class OcupacionAdapter(
    private var categories: List<Category>,
    private val isCategorySelected: (Int) -> Boolean,
    private val onCategorySelected: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<OcupacionAdapter.OcupacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OcupacionViewHolder {
        val binding = ItemOcupacionSelectableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OcupacionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OcupacionViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun updateData(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    inner class OcupacionViewHolder(private val binding: ItemOcupacionSelectableBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.tvOcupacionNombre.text = category.name

            // Quitamos listeners anteriores para evitar conflictos
            binding.root.setOnClickListener(null)
            binding.cbOcupacion.setOnCheckedChangeListener(null)

            // Sincronizamos el estado del CheckBox
            binding.cbOcupacion.isChecked = isCategorySelected(category.id)

            // Configuramos el click del CheckBox
            binding.cbOcupacion.setOnCheckedChangeListener { _, isChecked ->
                onCategorySelected(category.id, isChecked)
            }

            // Configuramos el click del contenedor completo
            binding.root.setOnClickListener {
                val newState = !binding.cbOcupacion.isChecked
                binding.cbOcupacion.isChecked = newState
                onCategorySelected(category.id, newState)
            }
        }
    }
}
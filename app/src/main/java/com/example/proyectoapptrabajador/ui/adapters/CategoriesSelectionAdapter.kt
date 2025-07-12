package com.example.proyectoapptrabajador.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.data.model.Categoria

class CategoriesSelectionAdapter(
    private val onCategoryToggle: (Int) -> Unit
) : RecyclerView.Adapter<CategoriesSelectionAdapter.CategoryViewHolder>() {

    private var categories: List<Categoria> = emptyList()
    private var selectedCategories: Set<Int> = emptySet()

    fun updateData(newCategories: List<Categoria>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    fun updateSelection(newSelection: Set<Int>) {
        selectedCategories = newSelection
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_selection, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkCategory)
        private val textName: TextView = itemView.findViewById(R.id.txtCategoryName)

        fun bind(category: Categoria) {
            textName.text = category.name
            checkBox.isChecked = selectedCategories.contains(category.id)

            // Configurar el listener del checkbox
            checkBox.setOnCheckedChangeListener(null) // Limpiar listener anterior
            checkBox.setOnCheckedChangeListener { _, _ ->
                onCategoryToggle(category.id)
            }

            // También permitir hacer clic en toda la fila
            itemView.setOnClickListener {
                onCategoryToggle(category.id)
            }
        }
    }
}

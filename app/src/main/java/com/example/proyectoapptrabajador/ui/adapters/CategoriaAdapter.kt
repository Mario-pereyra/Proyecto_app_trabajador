package com.example.proyectoapptrabajador.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapptrabajador.data.model.Categoria
import com.example.proyectoapptrabajador.databinding.CategoryItemBinding

class CategoriaAdapter(
    private var categories: List<Categoria>,
    private val onItemClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun updateData(newCategories: List<Categoria>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }

    inner class CategoriaViewHolder(private val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoria: Categoria) {
            binding.txtCategoryName.text = categoria.name
            itemView.setOnClickListener { onItemClick(categoria) }
        }
    }
}


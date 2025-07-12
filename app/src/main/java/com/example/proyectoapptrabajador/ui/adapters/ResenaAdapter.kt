package com.example.proyectoapptrabajador.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapptrabajador.data.model.Resena
import com.example.proyectoapptrabajador.databinding.ReviewItemBinding

class ResenaAdapter(
    private var reviews: List<Resena>
) : RecyclerView.Adapter<ResenaAdapter.ResenaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResenaViewHolder {
        val binding = ReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResenaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResenaViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size

    fun updateData(newReviews: List<Resena>) {
        this.reviews = newReviews
        notifyDataSetChanged()
    }

    inner class ResenaViewHolder(private val binding: ReviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(resena: Resena) {
            // Mostramos el nombre completo del usuario que hizo la reseña
            val reviewerName = "${resena.user.name} ${resena.user.lastName}"
            binding.txtReviewAuthor.text = reviewerName

            // Mostramos el comentario o un mensaje por defecto si es nulo
            binding.txtReviewComment.text = resena.comment ?: "Sin comentario adicional"
        }
    }
}

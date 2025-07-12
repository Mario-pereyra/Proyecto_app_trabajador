package com.example.proyectoapptrabajador.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.data.model.Trabajador
import com.example.proyectoapptrabajador.databinding.WorkerItemBinding

class TrabajadorAdapter(
    private var workers: List<Trabajador>,
    private val onItemClick: (Trabajador) -> Unit
) : RecyclerView.Adapter<TrabajadorAdapter.TrabajadorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrabajadorViewHolder {
        val binding = WorkerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrabajadorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrabajadorViewHolder, position: Int) {
        holder.bind(workers[position])
    }

    override fun getItemCount(): Int = workers.size

    fun updateData(newWorkers: List<Trabajador>) {
        this.workers = newWorkers
        notifyDataSetChanged()
    }

    inner class TrabajadorViewHolder(private val binding: WorkerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(trabajador: Trabajador) {
            // Filtro para apellido: si es null, usar "trabajador"
            val displayLastName = trabajador.user.lastName ?: "trabajador"
            val fullName = "${trabajador.user.name} $displayLastName"

            binding.txtWorkerName.text = fullName
            binding.txtWorkerRating.text = "${trabajador.averageRating}% - ${trabajador.reviewsCount} trabajos"

            // Lógica corregida para evitar FileNotFoundException con "null"
            val imageUrl = trabajador.pictureUrl
            Glide.with(itemView.context)
                .load(if (imageUrl == "null") null else imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .into(binding.imgWorker)

            itemView.setOnClickListener { onItemClick(trabajador) }
        }
    }
}

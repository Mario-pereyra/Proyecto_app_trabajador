package com.example.proyectoapptrabajador.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapptrabajador.data.model.Cita
import com.example.proyectoapptrabajador.databinding.ItemCitaBinding

class CitaAdapter(
    private val onCitaClick: (Cita) -> Unit
) : RecyclerView.Adapter<CitaAdapter.CitaViewHolder>() {

    private var citas: List<Cita> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = ItemCitaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(citas[position])
    }

    override fun getItemCount(): Int = citas.size

    fun updateCitas(newCitas: List<Cita>) {
        citas = newCitas
        notifyDataSetChanged()
    }

    inner class CitaViewHolder(private val binding: ItemCitaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cita: Cita) {
            // Nombre del cliente
            binding.tvClienteNombre.text = "${cita.client.name} ${cita.client.profile.last_name}"

            // Categoría de trabajo
            binding.tvCategoria.text = cita.category.name

            // Fecha y hora
            if (cita.appointment_date != null && cita.appointment_time != null) {
                binding.tvFecha.text = formatearFecha(cita.appointment_date)
                binding.tvHora.text = formatearHora(cita.appointment_time)
            } else {
                binding.tvFecha.text = "Fecha no definida"
                binding.tvHora.text = "Hora no definida"
            }

            // Estado de la cita con colores
            val (estadoTexto, colorFondo) = when (cita.status) {
                0 -> "Pendiente" to "#FF9800" // Naranja
                1 -> "Confirmada" to "#2196F3" // Azul
                2 -> "En progreso" to "#4CAF50" // Verde
                3 -> "Finalizada" to "#607D8B" // Gris azulado
                else -> "Estado desconocido" to "#757575" // Gris
            }

            binding.tvEstado.text = estadoTexto
            binding.tvEstado.setBackgroundColor(android.graphics.Color.parseColor(colorFondo))

            binding.root.setOnClickListener {
                onCitaClick(cita)
            }
        }

        private fun formatearFecha(fecha: String): String {
            return try {
                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                val date = inputFormat.parse(fecha)
                outputFormat.format(date ?: return fecha)
            } catch (e: Exception) {
                fecha
            }
        }

        private fun formatearHora(hora: String): String {
            return try {
                val inputFormat = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                val outputFormat = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                val time = inputFormat.parse(hora)
                outputFormat.format(time ?: return hora)
            } catch (e: Exception) {
                hora
            }
        }
    }
}

package com.example.proyectoapptrabajador.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapptrabajador.data.model.Cita
import com.example.proyectoapptrabajador.databinding.ItemCitaBinding

class CitaAdapter(
    private val onCitaClick: (Cita) -> Unit,
    private val onConfirmClick: (Cita) -> Unit,
    private val onFinalizeClick: (Cita) -> Unit,
    private val onViewLocationClick: (Cita) -> Unit // Nuevo callback para ver ubicación
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

            // Estado de la cita con colores y acciones
            when (cita.status) {
                0 -> {
                    binding.tvEstado.text = "Pendiente"
                    binding.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"))
                    // Solo chat para citas pendientes
                    binding.btnConfirmar.visibility = android.view.View.GONE
                    binding.btnFinalizar.visibility = android.view.View.GONE
                    binding.btnVerUbicacion.visibility = android.view.View.GONE
                }
                1 -> {
                    binding.tvEstado.text = "Solicitada"
                    binding.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#2196F3"))
                    // Chat + opción de concretar para citas solicitadas
                    binding.btnConfirmar.visibility = android.view.View.GONE
                    binding.btnFinalizar.visibility = android.view.View.GONE
                    binding.btnVerUbicacion.visibility = android.view.View.GONE
                }
                2 -> {
                    binding.tvEstado.text = "Concretada"
                    binding.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                    // Mostrar botón para ver ubicación de cita concretada
                    binding.btnConfirmar.visibility = android.view.View.GONE
                    binding.btnFinalizar.visibility = android.view.View.GONE

                    // Mostrar botón de ubicación si hay coordenadas
                    if (cita.latitude != null && cita.longitude != null) {
                        binding.btnVerUbicacion.visibility = android.view.View.VISIBLE
                    } else {
                        binding.btnVerUbicacion.visibility = android.view.View.GONE
                    }
                }
                3 -> {
                    binding.tvEstado.text = "Finalizada"
                    binding.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#607D8B"))
                    // Ocultar todos los botones para citas finalizadas
                    binding.btnConfirmar.visibility = android.view.View.GONE
                    binding.btnFinalizar.visibility = android.view.View.GONE
                    binding.btnVerUbicacion.visibility = android.view.View.GONE
                }
                else -> {
                    binding.tvEstado.text = "Estado desconocido"
                    binding.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#757575"))
                    binding.btnConfirmar.visibility = android.view.View.GONE
                    binding.btnFinalizar.visibility = android.view.View.GONE
                    binding.btnVerUbicacion.visibility = android.view.View.GONE
                }
            }

            // Click listeners para los botones de acción
            binding.btnVerUbicacion.setOnClickListener {
                onViewLocationClick(cita)
            }

            binding.btnConfirmar.setOnClickListener {
                onConfirmClick(cita)
            }

            binding.btnFinalizar.setOnClickListener {
                onFinalizeClick(cita)
            }

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

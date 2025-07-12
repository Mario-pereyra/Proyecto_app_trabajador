package com.example.proyectoapptrabajador.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapptrabajador.data.model.Cita
import com.example.proyectoapptrabajador.databinding.AppointmentItemBinding

class CitaAdapter(
    private var appointments: List<Cita>,
    private val onItemClick: (Cita) -> Unit,
    private val onViewLocationClick: (Cita) -> Unit = {} // Nuevo callback para ver ubicación
) : RecyclerView.Adapter<CitaAdapter.CitaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = AppointmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount(): Int = appointments.size

    fun updateData(newAppointments: List<Cita>) {
        this.appointments = newAppointments
        notifyDataSetChanged()
    }

    inner class CitaViewHolder(private val binding: AppointmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cita: Cita) {
            // Mostrar nombre del cliente en lugar del trabajador
            binding.txtWorkerName.text = cita.client?.name ?: "Cliente no asignado"
            binding.txtCategory.text = cita.category.name

            // Configurar el botón "Ver Ubicación"
            // Solo visible si la cita ha sido concretada (tiene coordenadas)
            if (cita.latitude != null && cita.longitude != null) {
                binding.btnViewLocation.visibility = View.VISIBLE
                binding.btnViewLocation.setOnClickListener {
                    onViewLocationClick(cita)
                }
            } else {
                binding.btnViewLocation.visibility = View.GONE
            }

            // Lógica de visualización condicional adaptada para trabajador
            when (cita.status) {
                0 -> { // PENDIENTE
                    binding.txtAppointmentStatus.text = "Estado: Pendiente de concretar"
                    binding.txtAppointmentDateTime.visibility = View.GONE
                }
                1 -> { // CONCRETADA POR CLIENTE
                    binding.txtAppointmentStatus.text = "Estado: Concretada por cliente"
                    binding.txtAppointmentDateTime.visibility = View.VISIBLE
                    binding.txtAppointmentDateTime.text = "Fecha: ${cita.appointmentDate} - Hora: ${cita.appointmentTime}"
                }
                2 -> { // CONFIRMADA POR TRABAJADOR
                    binding.txtAppointmentStatus.text = "Estado: Confirmada - En proceso"
                    binding.txtAppointmentDateTime.visibility = View.VISIBLE
                    binding.txtAppointmentDateTime.text = "Fecha: ${cita.appointmentDate} - Hora: ${cita.appointmentTime}"
                }
                3 -> { // FINALIZADA
                    binding.txtAppointmentStatus.text = "Estado: Trabajo finalizado"
                    binding.txtAppointmentDateTime.visibility = View.VISIBLE
                    binding.txtAppointmentDateTime.text = "Fecha: ${cita.appointmentDate} - Hora: ${cita.appointmentTime}"
                }
                else -> {
                    binding.txtAppointmentStatus.text = "Estado: Desconocido"
                    binding.txtAppointmentDateTime.visibility = View.GONE
                }
            }

            itemView.setOnClickListener { onItemClick(cita) }
        }
    }
}

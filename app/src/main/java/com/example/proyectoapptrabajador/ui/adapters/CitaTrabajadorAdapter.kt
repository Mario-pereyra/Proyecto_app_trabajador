package com.example.proyectoapptrabajador.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapptrabajador.data.model.Cita
import com.example.proyectoapptrabajador.databinding.AppointmentItemBinding

class CitaTrabajadorAdapter(
    private var appointments: List<Cita>,
    private val onItemClick: (Cita) -> Unit,
    private val onViewLocationClick: (Cita) -> Unit = {},
    private val onConfirmClick: (Cita) -> Unit = {},
    private val onFinalizeClick: (Cita) -> Unit = {}
) : RecyclerView.Adapter<CitaTrabajadorAdapter.CitaViewHolder>() {

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
            // Mostrar nombre del cliente (asumiendo que existe en el modelo)
            binding.txtWorkerName.text = cita.client?.name ?: "Cliente"
            binding.txtCategory.text = cita.category.name

            // Configurar el botón "Ver Ubicación"
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
                0 -> {
                    binding.txtAppointmentStatus.text = "Estado: Pendiente de concretar"
                    binding.txtAppointmentDateTime.visibility = View.GONE
                }
                1 -> {
                    binding.txtAppointmentStatus.text = "Estado: Concretada por cliente"
                    binding.txtAppointmentDateTime.visibility = View.VISIBLE
                    binding.txtAppointmentDateTime.text = "Fecha: ${cita.appointmentDate} - Hora: ${cita.appointmentTime}"
                }
                2 -> {
                    binding.txtAppointmentStatus.text = "Estado: Confirmada - En proceso"
                    binding.txtAppointmentDateTime.visibility = View.VISIBLE
                    binding.txtAppointmentDateTime.text = "Fecha: ${cita.appointmentDate} - Hora: ${cita.appointmentTime}"
                }
                3 -> {
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

package com.example.proyectoapptrabajador.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapptrabajador.data.model.MensajeChat
import com.example.proyectoapptrabajador.databinding.ItemMensajeEnviadoBinding
import com.example.proyectoapptrabajador.databinding.ItemMensajeRecibidoBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val isMyMessage: (Int) -> Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    private var mensajes: List<MensajeChat> = emptyList()

    override fun getItemViewType(position: Int): Int {
        val mensaje = mensajes[position]
        return if (isMyMessage(mensaje.sender_id)) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val binding = ItemMensajeEnviadoBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MensajeEnviadoViewHolder(binding)
            }
            VIEW_TYPE_RECEIVED -> {
                val binding = ItemMensajeRecibidoBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MensajeRecibidoViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Tipo de vista desconocido: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensaje = mensajes[position]
        when (holder) {
            is MensajeEnviadoViewHolder -> holder.bind(mensaje)
            is MensajeRecibidoViewHolder -> holder.bind(mensaje)
        }
    }

    override fun getItemCount(): Int = mensajes.size

    fun updateMensajes(newMensajes: List<MensajeChat>) {
        mensajes = newMensajes
        notifyDataSetChanged()
    }

    // ViewHolder para mensajes enviados (por el trabajador)
    inner class MensajeEnviadoViewHolder(private val binding: ItemMensajeEnviadoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mensaje: MensajeChat) {
            binding.tvMensaje.text = mensaje.message
            binding.tvHora.text = formatearHora(mensaje.date_sent)
        }
    }

    // ViewHolder para mensajes recibidos (del cliente)
    inner class MensajeRecibidoViewHolder(private val binding: ItemMensajeRecibidoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mensaje: MensajeChat) {
            binding.tvMensaje.text = mensaje.message
            binding.tvHora.text = formatearHora(mensaje.date_sent)
            binding.tvNombreRemitente.text = mensaje.sender.name
        }
    }

    private fun formatearHora(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            outputFormat.format(date ?: return dateTime)
        } catch (e: Exception) {
            dateTime
        }
    }
}

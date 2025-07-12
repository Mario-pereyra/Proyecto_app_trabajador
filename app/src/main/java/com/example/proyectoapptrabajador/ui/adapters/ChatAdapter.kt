package com.example.proyectoapptrabajador.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapptrabajador.data.model.MensajeChat
import com.example.proyectoapptrabajador.databinding.ItemChatReceivedBinding
import com.example.proyectoapptrabajador.databinding.ItemChatSentBinding

class ChatAdapter(
    private var messages: List<MensajeChat>,
    private val currentUserId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding = ItemChatSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentMessageViewHolder(binding)
        } else {
            val binding = ItemChatReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder.itemViewType == VIEW_TYPE_SENT) {
            (holder as SentMessageViewHolder).bind(message)
        } else {
            (holder as ReceivedMessageViewHolder).bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun updateData(newMessages: List<MensajeChat>) {
        this.messages = newMessages
        notifyDataSetChanged()
    }

    inner class SentMessageViewHolder(private val binding: ItemChatSentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MensajeChat) {
            binding.txtMessage.text = message.message
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemChatReceivedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MensajeChat) {
            binding.txtMessage.text = message.message
        }
    }
}

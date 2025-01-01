package com.example.kelompok2.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kelompok2.R
import com.example.kelompok2.DataModels.ChatMessage

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val chatMessages = mutableListOf<ChatMessage>()

    fun addMessage(message: ChatMessage) {
        chatMessages.add(message)
        notifyItemInserted(chatMessages.size - 1)
    }

    fun setMessages(messages: List<ChatMessage>) {
        chatMessages.clear()
        chatMessages.addAll(messages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == VIEW_TYPE_USER) {
            R.layout.item_chat_user
        } else {
            R.layout.item_chat_other
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        holder.bind(chatMessage)
    }

    override fun getItemCount() = chatMessages.size

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].isUser) {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_OTHER
        }
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.tv_message)

        fun bind(chatMessage: ChatMessage) {
            messageTextView.text = chatMessage.message
        }
    }

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_OTHER = 2
    }
}
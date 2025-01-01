package com.example.kelompok2.Fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kelompok2.R
import com.example.kelompok2.DataModels.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import android.widget.ImageView
import com.bumptech.glide.Glide

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val chatMessages = mutableListOf<ChatMessage>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

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
            R.layout.item_chat_user  // Layout pesan user
        } else {
            R.layout.item_chat_other  // Layout pesan user lain
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
        return if (chatMessages[position].senderId == currentUserId) {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_OTHER
        }
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.tv_message)
        private val imageView: ImageView = itemView.findViewById(R.id.i_chat_image)

        fun bind(chatMessage: ChatMessage) {
            if (chatMessage.imageUrl != null) {
                messageTextView.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                Glide.with(itemView.context).load(chatMessage.imageUrl).into(imageView)
            } else {
                messageTextView.text = chatMessage.message
                messageTextView.visibility = View.VISIBLE
                imageView.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_OTHER = 2
    }
}
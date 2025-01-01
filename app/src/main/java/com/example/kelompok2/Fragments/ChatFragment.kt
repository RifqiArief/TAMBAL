package com.example.kelompok2.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelompok2.Adapters.ChatAdapter
import com.example.kelompok2.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.example.kelompok2.DataModels.ChatMessage

class ChatFragment : Fragment() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        chatRecyclerView = view.findViewById(R.id.rv_chat)
        messageInput = view.findViewById(R.id.et_message)
        sendButton = view.findViewById(R.id.iv_send)

        setupRecyclerView()
        loadChatHistory()

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                messageInput.text.clear()
            } else {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = chatAdapter
    }

    private fun sendMessage(message: String) {
        val chatMessage = ChatMessage(message = message, isUser = true)

        val chatMap = hashMapOf(
            "userId" to (currentUser?.uid ?: "Anonymous"),
            "message" to message,
            "isUser" to true,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("chats")
            .add(chatMap)
            .addOnSuccessListener {
                chatAdapter.addMessage(chatMessage)
                chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadChatHistory() {
        db.collection("chats")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                val chatMessages = mutableListOf<ChatMessage>()
                for (document in result) {
                    val message = document.getString("message") ?: ""
                    val isUser = document.getBoolean("isUser") ?: false
                    chatMessages.add(ChatMessage(message, isUser))
                }
                chatAdapter.setMessages(chatMessages)
                chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load chat history", Toast.LENGTH_SHORT).show()
            }
    }
}
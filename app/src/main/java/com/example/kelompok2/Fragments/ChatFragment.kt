package com.example.kelompok2.Fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelompok2.R
import com.example.kelompok2.DataModels.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

        // Jika receiverId kosong, gunakan user sendiri untuk self-chat
        val receiverId = arguments?.getString("receiverId") ?: currentUser?.uid

        chatRecyclerView = view.findViewById(R.id.rv_chat)
        messageInput = view.findViewById(R.id.et_message)
        sendButton = view.findViewById(R.id.iv_send)

        setupRecyclerView()

        receiverId?.let {
            loadChatHistory(it)
        }

        // Tombol Kirim Pesan
        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                receiverId?.let {
                    sendMessage(message, it)
                    messageInput.text.clear()
                }
            } else {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Back
        view.findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Tombol Enter untuk Kirim Pesan
        messageInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                event?.action == KeyEvent.ACTION_DOWN &&
                event.keyCode == KeyEvent.KEYCODE_ENTER) {

                val message = messageInput.text.toString()
                if (message.isNotEmpty()) {
                    receiverId?.let {
                        sendMessage(message, it)
                        messageInput.text.clear()
                    }
                } else {
                    Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
        return view
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = chatAdapter
    }

    // Menghasilkan chatId unik untuk obrolan antar dua pengguna atau self-chat
    private fun generateChatId(user1: String, user2: String): String {
        return if (user1 == user2) {
            "${user1}_selfChat"
        } else if (user1 < user2) {
            "${user1}_${user2}"
        } else {
            "${user2}_${user1}"
        }
    }

    private fun sendMessage(message: String, receiverId: String) {
        val senderId = currentUser?.uid ?: return
        val chatId = generateChatId(senderId, receiverId)
        val chatMessage = ChatMessage(message, senderId, receiverId)

        db.collection("chats").document(chatId)
            .collection("messages")
            .add(chatMessage)
            .addOnSuccessListener {
                chatAdapter.addMessage(chatMessage)  // Tambahkan langsung ke RecyclerView
                chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                Toast.makeText(requireContext(), "Message sent", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadChatHistory(receiverId: String) {
        val senderId = currentUser?.uid ?: return
        val chatId = generateChatId(senderId, receiverId)

        db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), "Failed to load messages", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                snapshots?.let {
                    val messages = it.toObjects(ChatMessage::class.java)
                    chatAdapter.setMessages(messages)
                }
            }
    }
}
package com.example.kelompok2.Fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelompok2.R
import com.example.kelompok2.DataModels.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import java.util.*
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.File
import androidx.core.content.FileProvider
import java.text.SimpleDateFormat

class ChatFragment : Fragment() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageView
    private lateinit var ivProfilePicture: ImageView
    private lateinit var tvUsername: TextView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 100
    private lateinit var currentPhotoPath: String
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // Ambil receiverId dari arguments
        val receiverId = arguments?.getString("receiverId")

        // Inisialisasi Views
        chatRecyclerView = view.findViewById(R.id.rv_chat)
        messageInput = view.findViewById(R.id.et_message)
        sendButton = view.findViewById(R.id.iv_send)
        ivProfilePicture = view.findViewById(R.id.iv_profile_picture)
        tvUsername = view.findViewById(R.id.tv_username)

        setupRecyclerView()

        receiverId?.let {
            loadChatHistory(it)
            loadReceiverProfile(it)  // Load profil pengguna lain
        }

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

        view.findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            requireActivity().onBackPressed()
        }
        view.findViewById<ImageView>(R.id.ic_camera).setOnClickListener {
            openCamera()
        }


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

    private fun generateChatId(user1: String, user2: String): String {
        return if (user1 < user2) {
            "${user1}_${user2}"
        } else {
            "${user2}_${user1}"
        }
    }
    private fun sendMessage(message: String, receiverId: String, imageUrl: String? = null) {
        val senderId = currentUser?.uid ?: return
        val chatId = generateChatId(senderId, receiverId)

        // Create chat message object
        val chatMessage = ChatMessage(
            message = message,
            senderId = senderId,
            receiverId = receiverId,
            imageUrl = imageUrl,  // Pass image URL if available
            timestamp = System.currentTimeMillis()
        )

        // Store message in Firestore
        db.collection("chats").document(chatId)
            .collection("messages")
            .add(chatMessage)
            .addOnSuccessListener {
                chatAdapter.addMessage(chatMessage)
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

    // Load profil pengguna lain (receiver)
    private fun loadReceiverProfile(receiverId: String) {
        db.collection("Users").document(receiverId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val profileName = document.getString("fullName") ?: "Unknown"
                    val profileImage = document.getString("profileImage") ?: ""

                    tvUsername.text = profileName

                    // Load gambar dengan Glide
                    if (profileImage.isNotEmpty()) {
                        Glide.with(this)
                            .load(profileImage)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(ivProfilePicture)
                    } else {
                        ivProfilePicture.setImageResource(R.drawable.ic_profile_placeholder)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openCamera() {
        // Check if Camera Permission is Granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera()
        } else {
            // Request Camera Permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    // Launch Camera if Permission is Granted
    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: Exception) {
                Toast.makeText(requireContext(), "Failed to create file", Toast.LENGTH_SHORT).show()
                null
            }

            if (photoFile != null) {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    photoFile
                )
                currentPhotoPath = photoFile.absolutePath  // Pastikan path diinisialisasi di sini
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            } else {
                Toast.makeText(requireContext(), "Failed to open camera", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No Camera App Found", Toast.LENGTH_SHORT).show()
        }
    }

    // Menangani hasil foto yang diambil
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (::currentPhotoPath.isInitialized) {
                val file = File(currentPhotoPath)
                if (file.exists()) {
                    val photoUri = Uri.fromFile(file)
                    uploadPhotoToFirebase(photoUri)
                } else {
                    Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Photo path not initialized", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Upload gambar ke Firebase Storage
    private fun uploadPhotoToFirebase(fileUri: Uri) {
        val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance()
            .getReference("chat_images/${UUID.randomUUID()}.jpg")

        storageRef.putFile(fileUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val receiverId = arguments?.getString("receiverId") ?: return@addOnCompleteListener
                    sendMessage("", receiverId, downloadUri.toString())
                } else {
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath  // Inisialisasi path di sini
        }
        Log.d("CameraDebug", "File created at: $currentPhotoPath")
        return file
    }
}
package com.example.kelompok2.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelompok2.Activities.SettingsActivity
import com.example.kelompok2.Adapters.UserAdapter
import com.example.kelompok2.DataModels.UserModel
import com.example.kelompok2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.fragment.app.commit

class HomeFragment : Fragment() {

    private lateinit var ivHomeProfileImage: ImageView
    private lateinit var tvHomeWelcomeText: TextView
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi views
        ivHomeProfileImage = view.findViewById(R.id.homeProfileImage)
        tvHomeWelcomeText = view.findViewById(R.id.homeWelcomeText)
        userRecyclerView = view.findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load user data (profile image and welcome text)
        loadUserProfileImageAndName()

        // Load daftar pengguna dari Firestore
        loadUsers()

        // Klik profil menuju Settings
        ivHomeProfileImage.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    // Fungsi untuk memuat daftar pengguna
    // Fungsi untuk memuat daftar pengguna
    private fun loadUsers() {
        currentUser?.let { user ->
            val userId = user.uid

            // Ambil tipe pengguna yang sedang login
            db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val currentUserType = document.getString("userType") ?: "Standard"

                        // Tentukan tipe yang ingin diambil (berlawanan dari tipe pengguna yang login)
                        val targetUserType = if (currentUserType == "mechanic") "Standard" else "mechanic"

                        // Query Firestore untuk memuat pengguna dengan tipe yang berbeda
                        db.collection("Users")
                            .whereEqualTo("userType", targetUserType)
                            .get()
                            .addOnSuccessListener { documents ->
                                val userList = documents.map { it.toObject(UserModel::class.java) }

                                // Pasang data ke dalam RecyclerView
                                userAdapter = UserAdapter(userList) { user ->
                                    openChatFragment(user)
                                }
                                userRecyclerView.adapter = userAdapter
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to load users.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Failed to get user type.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to load current user.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Buka ChatFragment untuk user yang dipilih
    private fun openChatFragment(user: UserModel) {
        val bundle = Bundle()
        bundle.putString("receiverId", user.userId)
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle

        parentFragmentManager.commit {
            replace(R.id.FragmentContainer, chatFragment)
            addToBackStack(null)
        }
    }

    // Fungsi untuk memuat gambar profil dan nama pengguna
    private fun loadUserProfileImageAndName() {
        currentUser?.let { user ->
            val userId = user.uid
            db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val profileImageUrl = document.getString("profileImage") ?: ""
                        val fullName = document.getString("fullName") ?: "User"

                        // Ambil first name dari full name
                        val firstName = fullName.split(" ").firstOrNull() ?: "User"

                        // Set welcome text
                        tvHomeWelcomeText.text = "Welcome, $firstName 👋"

                        // Load profil dengan Glide
                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(android.R.drawable.ic_menu_camera)
                                .error(R.drawable.icon_circle_user)  // Fallback jika gagal load
                                .into(ivHomeProfileImage)
                        } else {
                            ivHomeProfileImage.setImageResource(R.drawable.icon_circle_user)
                        }
                    } else {
                        Toast.makeText(context, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(context, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.example.kelompok2.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kelompok2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountFragment : Fragment() {

    private lateinit var ivProfileImage: ImageView
    private lateinit var tvFullName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnBack: ImageButton

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Initialize views
        ivProfileImage = view.findViewById(R.id.accountProfileImage)
        tvFullName = view.findViewById(R.id.accountFullName)
        tvEmail = view.findViewById(R.id.accountEmail)
        btnBack = view.findViewById(R.id.accountBackButton)

        // Load user data
        loadUserData()

        // Handle back button click
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack() // Navigate back to previous fragment
        }

        return view
    }

    private fun loadUserData() {
        currentUser?.let { user ->
            val userId = user.uid
            db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fullName = document.getString("fullName") ?: "User"
                        val email = document.getString("email") ?: "No Email"
                        val profileImageUrl = document.getString("profileImage") ?: ""

                        // Set user details
                        tvFullName.text = fullName
                        tvEmail.text = email

                        // Load profile image using Glide
                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.icon_circle_user)
                                .into(ivProfileImage)
                        } else {
                            ivProfileImage.setImageResource(R.drawable.icon_circle_user)
                        }
                    }
                }
        }
    }
}

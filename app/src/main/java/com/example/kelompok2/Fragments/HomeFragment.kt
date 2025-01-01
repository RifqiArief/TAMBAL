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
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import com.example.kelompok2.Activities.SettingsActivity
import com.example.kelompok2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    private lateinit var ivHomeProfileImage: ImageView
    private lateinit var tvHomeWelcomeText: TextView

    // Firebase references
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        ivHomeProfileImage = view.findViewById(R.id.homeProfileImage)
        tvHomeWelcomeText = view.findViewById(R.id.homeWelcomeText)

        // Load the user's profile image and welcome text
        loadUserProfileImageAndName()

        // Make the profile image clickable to redirect to SettingsActivity
        ivHomeProfileImage.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        // Find the chat button
        val chatButton: MaterialButton = view.findViewById(R.id.homeRecentCarBookBtn)

        // Set up the click listener
        chatButton.setOnClickListener {
            // Navigate to ChatFragment
            parentFragmentManager.commit {
                replace(R.id.FragmentContainer, ChatFragment())
                addToBackStack(null)
            }
        }

        return view
    }

    private fun loadUserProfileImageAndName() {
        currentUser?.let { user ->
            val userId = user.uid
            db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val profileImageUrl = document.getString("profileImage") ?: ""
                        val fullName = document.getString("fullName") ?: "User"

                        // Extract the first name from the full name
                        val firstName = fullName.split(" ").firstOrNull() ?: "User"

                        // Set the welcome text
                        tvHomeWelcomeText.text = "Welcome, $firstName 👋"

                        // Load the profile image with Glide
                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(android.R.drawable.ic_menu_camera)
                                .error(R.drawable.icon_circle_user)  // Fallback if loading fails
                                .into(ivHomeProfileImage)
                        } else {
                            // Set default placeholder if no image exists
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
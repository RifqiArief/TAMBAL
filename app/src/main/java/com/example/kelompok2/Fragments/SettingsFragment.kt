package com.example.kelompok2.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kelompok2.Activities.LandingActivity
import com.example.kelompok2.Activities.SettingsActivity
import com.example.kelompok2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsFragment : Fragment() {

    private lateinit var ivProfileImage: ImageView
    private lateinit var tvFullName: TextView
    private lateinit var btnSignOut: Button

    // Firestore and Firebase Auth references
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize views
        ivProfileImage = view.findViewById(R.id.settingsProfileImage)
        tvFullName = view.findViewById(R.id.settingsProfileFullname)
        btnSignOut = view.findViewById(R.id.settingsSignOutBtn)

        // Load user data
        loadUserData()

        // Set click listener for Account button
        view.findViewById<View>(R.id.settingsGeneralAccountBtn).setOnClickListener {
            openAccountFragment()
        }

        // Set click listener for Privacy button
        view.findViewById<View>(R.id.settingsGeneralPrivacyBtn).setOnClickListener {
            openPrivacyFragment()
        }

        // Set click listener for Notifications button
        view.findViewById<View>(R.id.settingsGeneralNotificationsBtn).setOnClickListener {
            openNotificationsFragment()
        }

        // Set click listener for Report Bug button
        view.findViewById<View>(R.id.settingsGeneralBugBtn).setOnClickListener {
            openReportBugFragment()
        }

        // Set click listener for Send Feedback button
        view.findViewById<View>(R.id.settingsGeneralFeedbackBtn).setOnClickListener {
            openFeedbackFragment()
        }

        // Set click listener for Edit Profile button
        view.findViewById<View>(R.id.settingsEditProfileBtn).setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for Sign Out button
        btnSignOut.setOnClickListener {
            signOut()
        }

        return view
    }

    // Function to load user's full name from Firestore
    private fun loadUserData() {
        currentUser?.let { user ->
            val userId = user.uid
            db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fullName = document.getString("fullName") ?: "User"
                        val profileImageUrl = document.getString("profileImage") ?: ""

                        // Set user's full name
                        tvFullName.text = fullName

                        // Set profile image using Glide
                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.icon_circle_user)  // Your default placeholder
                                .into(ivProfileImage)
                        } else {
                            // Set placeholder if no image found
                            ivProfileImage.setImageResource(R.drawable.icon_circle_user)
                        }
                    } else {
                        Toast.makeText(context, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Function to sign out the user
    private fun signOut() {
        auth.signOut()
        Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()

        // Redirect to the LandingActivity or Login Screen
        val intent = Intent(requireContext(), LandingActivity::class.java)
        startActivity(intent)
        requireActivity().finish()  // Close the current activity
    }

    // Function to open AccountFragment
    private fun openAccountFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(this.id, AccountFragment()) // Replace current fragment
        transaction.addToBackStack(null) // Add the transaction to the back stack
        transaction.commit()
    }

    // Function to open PrivacyFragment
    private fun openPrivacyFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(this.id, PrivacyFragment()) // Replace current fragment
        transaction.addToBackStack(null) // Add to back stack
        transaction.commit()
    }

    // Function to open Notifications2Fragment
    private fun openNotificationsFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(this.id, Notifications2Fragment()) // Replace current fragment
        transaction.addToBackStack(null) // Add to back stack
        transaction.commit()
    }

    // Function to open ReportBugFragment
    private fun openReportBugFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(this.id, ReportBugFragment()) // Replace current fragment
        transaction.addToBackStack(null) // Add to back stack
        transaction.commit()
    }

    // Function to open FeedbackFragment
    private fun openFeedbackFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(this.id, FeedbackFragment()) // Replace current fragment
        transaction.addToBackStack(null) // Add to back stack
        transaction.commit()
    }
}

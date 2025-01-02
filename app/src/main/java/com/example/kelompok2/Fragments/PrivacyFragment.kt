package com.example.kelompok2.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kelompok2.R
import com.google.firebase.auth.FirebaseAuth

class PrivacyFragment : Fragment() {

    private lateinit var tvEmail: TextView
    private lateinit var etNewPassword: EditText
    private lateinit var btnChangePassword: Button
    private lateinit var btnBack: ImageButton

    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_privacy, container, false)

        // Initialize views
        tvEmail = view.findViewById(R.id.privacyEmail)
        etNewPassword = view.findViewById(R.id.newPasswordInput)
        btnChangePassword = view.findViewById(R.id.privacyChangePasswordButton)
        btnBack = view.findViewById(R.id.privacyBackButton)

        // Load user data
        loadUserData()

        // Back button
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack() // Navigate back
        }

        // Change password
        btnChangePassword.setOnClickListener {
            val newPassword = etNewPassword.text.toString().trim()
            if (newPassword.length >= 6) {
                changeUserPassword(newPassword)
            } else {
                Toast.makeText(context, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loadUserData() {
        currentUser?.let { user ->
            // Set email
            tvEmail.text = user.email ?: "No Email"
        }
    }

    private fun changeUserPassword(newPassword: String) {
        currentUser?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                    etNewPassword.text.clear() // Clear input field after success
                } else {
                    Toast.makeText(context, "Failed to change password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

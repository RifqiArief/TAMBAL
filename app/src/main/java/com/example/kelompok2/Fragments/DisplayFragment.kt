package com.example.kelompok2.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kelompok2.R
import com.example.kelompok2.databinding.FragmentAccountInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DisplayFragment : Fragment() {

    private var _binding: FragmentAccountInfoBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountInfoBinding.inflate(inflater, container, false)

        // Load account data
        loadAccountData()

        // Handle Back button click
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun loadAccountData() {
        currentUser?.let { user ->
            val userId = user.uid
            db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fullName = document.getString("fullName") ?: "N/A"
                        val email = document.getString("email") ?: "N/A"
                        val password = "******" // Masked for security

                        // Populate the views with account data
                        binding.accountFullNameTextView.text = fullName
                        binding.accountEmailTextView.text = email
                        binding.accountPasswordTextView.text = password
                    }
                }
                .addOnFailureListener {
                    binding.accountFullNameTextView.text = "Error loading data"
                    binding.accountEmailTextView.text = "Error loading data"
                    binding.accountPasswordTextView.text = "Error loading data"
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

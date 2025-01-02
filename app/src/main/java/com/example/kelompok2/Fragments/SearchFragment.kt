package com.example.kelompok2.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelompok2.Activities.LocationPickerActivity
import com.example.kelompok2.Adapters.MechanicAdapter
import com.example.kelompok2.DataModels.CarModel
import com.example.kelompok2.DataModels.ServiceOrderModel
import com.example.kelompok2.databinding.FragmentSearchBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var mechanicAdapter: MechanicAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchMechanicsFromFirestore()

        // Launch location picker on edit button click
        binding.searchHeaderEditBtn.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val intent = Intent(requireContext(), LocationPickerActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
            } else {
                Toast.makeText(context, "Please sign in to change location.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        mechanicAdapter = MechanicAdapter(emptyList())

        // Handle Order Button Click
        mechanicAdapter.onOrderClick = onOrderClick@{ selectedMechanic ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@onOrderClick
            val userName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown User"
            val userPhone = "123-456-789"  // Placeholder or fetch from user profile

            val order = ServiceOrderModel(
                orderId = db.collection("Orders").document().id,
                mechanicId = selectedMechanic.brand,
                userId = userId,
                userName = userName,
                userPhone = userPhone,
                status = "Pending"
            )

            db.collection("Orders").document(order.orderId)
                .set(order)
                .addOnSuccessListener {
                    Toast.makeText(context, "Order Sent Successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to send order: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mechanicAdapter
        }
    }

    private fun fetchMechanicsFromFirestore() {
        db.collection("Users")
            .whereEqualTo("userType", "mechanic")
            .get()
            .addOnSuccessListener { documents ->
                val mechanicsList = documents.mapNotNull { doc ->
                    val fullName = doc.getString("fullName") ?: "Unknown"
                    val lat = doc.get("location.lat") as? Double
                    val lng = doc.get("location.lng") as? Double
                    if (lat != null && lng != null) {
                        CarModel(
                            brand = fullName,
                            model = "Mechanic Service",
                            seats = 0,
                            doors = 0,
                            transmission = "-",
                            rating = 5.0,
                            carType = "Mechanic",
                            image = doc.getString("profileImage") ?: "",
                            servicePrice = 0,
                            location = "$lat, $lng"
                        )
                    } else {
                        println("Mechanic ${doc.id} missing location.")
                        null
                    }
                }
                mechanicAdapter.updateMechanicList(mechanicsList)
            }
            .addOnFailureListener { e ->
                println("Failed to fetch mechanics: ${e.message}")
            }
    }
}

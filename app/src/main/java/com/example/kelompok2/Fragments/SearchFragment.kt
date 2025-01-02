package com.example.kelompok2.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelompok2.Adapters.SearchCarsAdapter
import com.example.kelompok2.DataModels.CarModel
import com.example.kelompok2.databinding.FragmentSearchBinding
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchCarsAdapter: SearchCarsAdapter
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
    }

    private fun setupRecyclerView() {
        searchCarsAdapter = SearchCarsAdapter(emptyList())
        searchCarsAdapter.onItemClick = { selectedMechanic ->
            Toast.makeText(requireContext(), "Navigating to ${selectedMechanic.brand}'s details", Toast.LENGTH_SHORT).show()
        }
        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = searchCarsAdapter
        }
    }



    private fun fetchMechanicsFromFirestore() {
        db.collection("Users")
            .whereEqualTo("userType", "mechanic")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    println("No mechanics found.")
                } else {
                    for (doc in documents) {
                        println("Fetched: ${doc.data}")
                    }
                }

                val mechanicsList = documents.mapNotNull { doc ->
                    val fullName = doc.getString("fullName") ?: "Unknown"
                    val lat = doc.get("location.lat") as? Double
                    val lng = doc.get("location.lng") as? Double
                    if (lat != null && lng != null) {
                        CarModel(
                            brand = fullName,
                            model = "Mechanic Location",
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
                searchCarsAdapter.updateCarList(mechanicsList)
            }
            .addOnFailureListener { e ->
                println("Failed to fetch mechanics: ${e.message}")
            }
    }

}

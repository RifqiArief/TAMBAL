package com.example.kelompok2.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelompok2.Adapters.MechanicOrdersAdapter
import com.example.kelompok2.DataModels.ServiceOrderModel
import com.example.kelompok2.databinding.FragmentMechanicOrdersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MechanicOrdersFragment : Fragment() {

    private lateinit var binding: FragmentMechanicOrdersBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var mechanicOrdersAdapter: MechanicOrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMechanicOrdersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchOrdersForMechanic()
    }

    private fun setupRecyclerView() {
        mechanicOrdersAdapter = MechanicOrdersAdapter(emptyList())
        binding.ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mechanicOrdersAdapter
        }
    }

    private fun fetchOrdersForMechanic() {
        val mechanicId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("Orders")
            .whereEqualTo("mechanicId", mechanicId)
            .get()
            .addOnSuccessListener { documents ->
                val orders = documents.map { it.toObject(ServiceOrderModel::class.java) }
                mechanicOrdersAdapter.updateOrderList(orders)
            }
    }
}

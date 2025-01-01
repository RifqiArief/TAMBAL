package com.example.kelompok2.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.kelompok2.R

class Notifications2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications2, container, false)

        // Tombol back untuk kembali ke fragment sebelumnya
        val btnBack: ImageButton = view.findViewById(R.id.notificationsBackButton)
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack() // Kembali ke fragment sebelumnya
        }

        return view
    }
}

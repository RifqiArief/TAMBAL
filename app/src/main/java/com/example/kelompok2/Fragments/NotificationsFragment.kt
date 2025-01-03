package com.example.kelompok2.Fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kelompok2.R
import com.example.kelompok2.ViewModels.NotificationsViewModel

class NotificationsFragment : Fragment() {

    companion object {
        fun newInstance() = NotificationsFragment()
    }

    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }
}
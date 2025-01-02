package com.example.kelompok2.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kelompok2.R

class FeedbackFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feedback, container, false)

        // Back button
        view.findViewById<ImageButton>(R.id.feedbackBackButton).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Send button
        view.findViewById<Button>(R.id.btnSendFeedback).setOnClickListener {
            val feedback = view.findViewById<EditText>(R.id.etFeedback).text.toString()
            if (feedback.isEmpty()) {
                Toast.makeText(context, "Please write your feedback.", Toast.LENGTH_SHORT).show()
            } else {
                sendFeedback(feedback)
            }
        }

        return view
    }

    private fun sendFeedback(feedback: String) {
        Toast.makeText(context, "Feedback sent: $feedback", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }
}

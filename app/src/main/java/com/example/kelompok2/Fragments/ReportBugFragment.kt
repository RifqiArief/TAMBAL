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

class ReportBugFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report_bug, container, false)

        // Back button
        view.findViewById<ImageButton>(R.id.reportBugBackButton).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Send button
        view.findViewById<Button>(R.id.btnSendBugReport).setOnClickListener {
            val description = view.findViewById<EditText>(R.id.etBugDescription).text.toString()
            if (description.isEmpty()) {
                Toast.makeText(context, "Please describe the issue.", Toast.LENGTH_SHORT).show()
            } else {
                sendBugReport(description)
            }
        }

        return view
    }

    private fun sendBugReport(description: String) {
        Toast.makeText(context, "Bug report sent: $description", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }
}

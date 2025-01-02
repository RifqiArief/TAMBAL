import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.kelompok2.Activities.LocationPickerActivity
import com.example.kelompok2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpgradeMechanicFragment : DialogFragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upgrade_mechanic, container, false)

        val nameEditText: EditText = view.findViewById(R.id.et_name)
        val emailEditText: EditText = view.findViewById(R.id.et_email)
        val mobileEditText: EditText = view.findViewById(R.id.et_mobile)
        val confirmButton: Button = view.findViewById(R.id.btn_confirm)
        val termsCheckBox: CheckBox = view.findViewById(R.id.cb_terms)

        confirmButton.isEnabled = false

        // Enable button only when checkbox is checked
        termsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            confirmButton.isEnabled = isChecked
        }

        confirmButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val mobile = mobileEditText.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && mobile.isNotEmpty()) {
                checkIfUserIsMechanic(name, email, mobile)
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // Check if user is already a mechanic with location
    private fun checkIfUserIsMechanic(name: String, email: String, mobile: String) {
        val userId = currentUser?.uid ?: return

        db.collection("Users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userType = document.getString("userType")
                    val location = document.get("location") as? Map<*, *>

                    if (userType == "mechanic" && location != null) {
                        Toast.makeText(context, "You are already a mechanic with a set location.", Toast.LENGTH_LONG).show()
                        dismiss()
                    } else if (userType == "mechanic" && location == null) {
                        // Redirect to location picker if no location exists
                        redirectToLocationPicker(userId)
                    } else {
                        updateUserRoleToMechanic(name, email, mobile)
                    }
                } else {
                    updateUserRoleToMechanic(name, email, mobile)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error checking profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Update user to mechanic role
    private fun updateUserRoleToMechanic(name: String, email: String, mobile: String) {
        val userId = currentUser?.uid ?: return
        val userMap = mapOf(
            "fullName" to name,
            "email" to email,
            "phoneNumber" to mobile,
            "userType" to "mechanic"
        )

        db.collection("Users").document(userId)
            .update(userMap)
            .addOnSuccessListener {
                Toast.makeText(context, "Profile Updated! You are now a mechanic.", Toast.LENGTH_SHORT).show()
                redirectToLocationPicker(userId)  // After upgrade, set location
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Redirect to Location Picker Activity
    private fun redirectToLocationPicker(userId: String) {
        Log.d("UpgradeMechanic", "Redirecting to Location Picker for user: $userId")
        Toast.makeText(context, "Redirecting to map...", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), LocationPickerActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}

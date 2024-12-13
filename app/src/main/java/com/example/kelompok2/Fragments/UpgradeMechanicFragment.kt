import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_upgrade_mechanic, container, false)

        val nameEditText: EditText = view.findViewById(R.id.et_name)
        val emailEditText: EditText = view.findViewById(R.id.et_email)
        val mobileEditText: EditText = view.findViewById(R.id.et_mobile)
        val confirmButton: Button = view.findViewById(R.id.btn_confirm)
        val termsCheckBox: CheckBox = view.findViewById(R.id.cb_terms)

        // Disable the confirm button initially
        confirmButton.isEnabled = false

        // Add a listener to the CheckBox to enable the confirm button when checked
        termsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            confirmButton.isEnabled = isChecked
        }

        confirmButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val mobile = mobileEditText.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && mobile.isNotEmpty()) {
                updateUserRoleToMechanic(name, email, mobile)
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun updateUserRoleToMechanic(name: String, email: String, mobile: String) {
        val userId = currentUser?.uid ?: return
        val userMap = mapOf(
            "fullName" to name,
            "email" to email,
            "phoneNumber" to mobile,
            "userType" to "mechanic"
        )

        db.collection("Users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Toast.makeText(context, "Profile Updated! You are now a mechanic.", Toast.LENGTH_SHORT).show()
                dismiss() // Close the dialog
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
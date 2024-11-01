package com.example.kelompok2.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kelompok2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class SettingsActivity : AppCompatActivity() {

    private lateinit var ivProfileImage: ImageView
    private lateinit var etFullName: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var btnSaveName: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnGoBack: Button

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ivProfileImage = findViewById(R.id.settingsProfileImage)
        etFullName = findViewById(R.id.et_full_name)
        etNewPassword = findViewById(R.id.et_new_password)
        btnSaveName = findViewById(R.id.btn_save_name)
        btnChangePassword = findViewById(R.id.btn_change_password)
        btnGoBack = findViewById(R.id.btn_go_back)

        loadUserData()

        ivProfileImage.setOnClickListener {
            openImagePicker()
        }

        btnSaveName.setOnClickListener {
            val fullName = etFullName.text.toString()
            if (fullName.isNotEmpty()) {
                saveFullNameToFirestore(fullName)
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        btnChangePassword.setOnClickListener {
            val newPassword = etNewPassword.text.toString()
            if (newPassword.length >= 6) {
                changeUserPassword(newPassword)
            } else {
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoBack.setOnClickListener {
            finish()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri = data.data!!
            ivProfileImage.setImageURI(imageUri)  // Display selected image
            uploadImageToFirebase(imageUri)
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val userId = currentUser?.uid ?: return
        val storageRef = storage.reference.child("profile_images/$userId.jpg")

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        storageRef.putBytes(data)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveImageUrlToFirestore(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val userId = currentUser?.uid ?: return
        db.collection("Users").document(userId)
            .update("profileImage", imageUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile image updated!", Toast.LENGTH_SHORT).show()
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_camera)
                    .into(ivProfileImage)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save image URL: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserData() {
        currentUser?.let { user ->
            val userId = user.uid
            db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fullName = document.getString("fullName") ?: "User"
                        val profileImageUrl = document.getString("profileImage") ?: ""

                        etFullName.setText(fullName)

                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(android.R.drawable.ic_menu_camera)
                                .into(ivProfileImage)
                        }
                    } else {
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun changeUserPassword(newPassword: String) {
        currentUser?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to change password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveFullNameToFirestore(fullName: String) {
        currentUser?.let { user ->
            val userId = user.uid
            db.collection("Users").document(userId)
                .update("fullName", fullName)
                .addOnSuccessListener {
                    Toast.makeText(this, "Name saved successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save name: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

package com.example.kelompok2.Activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kelompok2.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var selectedLatLng: LatLng? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        userId = intent.getStringExtra("userId") ?: run {
            Toast.makeText(this, "User ID is missing.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this) ?: run {
            Toast.makeText(this, "Failed to load map fragment.", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.btnConfirmLocation).setOnClickListener {
            if (selectedLatLng != null) {
                saveUserLocation(selectedLatLng!!.latitude, selectedLatLng!!.longitude)
            } else {
                Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val defaultLocation = LatLng(-6.200000, 106.816666) // Jakarta, for example
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            selectedLatLng = latLng
            Toast.makeText(this, "Location Selected!", Toast.LENGTH_SHORT).show()
        }

        enableLocation()
    }

    private fun enableLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableLocation()
        } else {
            Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserLocation(lat: Double, lng: Double) {
        val locationMap = mapOf("lat" to lat, "lng" to lng)

        db.collection("Users").document(userId)
            .update("location", locationMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Location saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving location", e)
                Toast.makeText(this, "Failed to save location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

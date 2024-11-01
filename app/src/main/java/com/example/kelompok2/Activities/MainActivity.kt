package com.example.kelompok2.Activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kelompok2.Fragments.HomeFragment
import com.example.kelompok2.Fragments.NotificationsFragment
import com.example.kelompok2.Fragments.SearchFragment
import com.example.kelompok2.Fragments.SettingsFragment
import com.example.kelompok2.R
import com.example.kelompok2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)



            replaceFragment(HomeFragment())

            binding.bottomNavigationView.setOnItemSelectedListener {menuItem ->
                when (menuItem.itemId) {
                    R.id.navHome -> {
                        replaceFragment(HomeFragment())
                        true
                    }
                    R.id.navSearch -> {
                        replaceFragment(SearchFragment())
                        true
                    }
                    R.id.navNotifications -> {
                        replaceFragment(NotificationsFragment())
                        true
                    }
                    R.id.navSettings -> {
                        replaceFragment(SettingsFragment())
                        true
                    }
                    else -> false
                }
            }


        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(binding.FragmentContainer.id, fragment).commit()
    }


}

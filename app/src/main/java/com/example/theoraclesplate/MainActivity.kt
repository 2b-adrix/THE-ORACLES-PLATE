package com.example.theoraclesplate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.theoraclesplate.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class  MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
           var NavController:NavController = findNavController(R.id.fragmentContainerView)
           var bottomnav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomnav.setupWithNavController(NavController)
        binding.notificationButton.setOnClickListener {
            val bottomSheetDialog = Notification_Bottom_Fragment()
            bottomSheetDialog.show(supportFragmentManager, "Test")
        }
        }
}
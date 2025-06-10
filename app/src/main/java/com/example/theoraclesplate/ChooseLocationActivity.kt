package com.example.theoraclesplate

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.theoraclesplate.databinding.ActivityChooseLocationBinding

class ChooseLocationActivity : AppCompatActivity() {
    private val binding : ActivityChooseLocationBinding by lazy {
        ActivityChooseLocationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
       val locationList = listOf("Jaipur", "Jharsuguda", "Delhi","Others")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locationList)


      val autoCompleteTextView =  binding.ListOfLocation
          autoCompleteTextView.setAdapter(adapter)
        }
    }

package com.example.theoraclesplate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.theoraclesplate.databinding.ActivityLoginBinding

class login : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.loginbutton.setOnClickListener {
          val intent = Intent(this, MainActivity::class.java)
          startActivity(intent)
        }
        binding.dontHaveButton.setOnClickListener {
          val intent = Intent(this,SigninActivity::class.java)
          startActivity(intent)

        }
        }
    }

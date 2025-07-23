package com.example.theoraclesplate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.theoraclesplate.databinding.ActivitySigninBinding

class SigninActivity : AppCompatActivity() {
    private val binding: ActivitySigninBinding by lazy {
        ActivitySigninBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
       binding.haveaccountbutton.setOnClickListener{
           val intent = Intent(this,login::class.java)
           startActivity(intent)

       }
        binding.CreateAccount.setOnClickListener{
           val intent = Intent(this,login::class.java)
           startActivity(intent)

       }
    }
}
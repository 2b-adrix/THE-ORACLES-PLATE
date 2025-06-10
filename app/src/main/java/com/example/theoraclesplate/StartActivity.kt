package com.example.theoraclesplate

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.theoraclesplate.databinding.ActivityStartBinding
import kotlin.jvm.java

class StartActivity : AppCompatActivity() {
    private val binding: ActivityStartBinding by lazy{
        ActivityStartBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
binding.nextbutton.setOnClickListener {
    val intent = Intent(this, login::class.java)
    startActivity(intent)

}
    }
}
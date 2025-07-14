package com.example.login

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val username = intent.getStringExtra("username") ?: "Guest"
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        tvWelcome.text = "Welcome, $username!"
    }
}

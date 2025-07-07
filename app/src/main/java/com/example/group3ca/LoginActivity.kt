package com.example.group3ca

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.group3ca.databinding.ActivityLoginBinding
import com.example.group3ca.dto.LoginRequestDto
import com.example.group3ca.dto.LoginResponseDto
import com.example.group3ca.service.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPrefs: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initLogin()

    }

    // Function that initialises login functionality
    fun initLogin() {
        binding.apply {
            btnLogin.setOnClickListener() {
                val username = usernameInput.text.toString()
                val password = passwordInput.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Please enter username and password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val request = LoginRequestDto(username, password)

                // Use created ApiClient to call login function that calls corresponding backend login
                ApiClient.instance.login(request).enqueue(object : Callback<LoginResponseDto> {
                    override fun onResponse(
                        call: Call<LoginResponseDto>,
                        response: Response<LoginResponseDto>
                    ) {
                        if (response.isSuccessful) {
                            val user = response.body()
                            Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()

                            // Save username and tier to shared preferences
                            sharedPrefs = getSharedPreferences("UserSession", MODE_PRIVATE)
                            sharedPrefs.edit().putString("UserId", user?.id).apply()
                            sharedPrefs.edit().putString("Tier", user?.tier).apply()

                            // Intent to FetchActivity upon successful login
                            startActivity(Intent(this@LoginActivity, FetchActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponseDto>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })


            }


        }

    }



}
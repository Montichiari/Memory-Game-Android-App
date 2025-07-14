package com.example.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usernameInput = findViewById<EditText>(R.id.etUsername)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val loginBtn = findViewById<Button>(R.id.btnLogin)

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 模拟验证用户名和密码，假设账号admin，密码123456
            if (username == "admin" && password == "123456") {
                // 登录成功，跳转HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
            } else {
                // 登录失败，弹出提示
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


package com.example.group3ca

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ★ 临时：直接跳排行榜
        startActivity(Intent(this, LeaderboardActivity::class.java))
        // 可选：finish() 让 MainActivity 不留在返回栈
    }
}

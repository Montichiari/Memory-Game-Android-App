package com.example.group3ca

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.group3ca.databinding.ActivityLeaderboardBinding
import com.example.group3ca.network.ApiClient
import kotlinx.coroutines.launch

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private val adapter = ScoreAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ① Bind ViewBinding
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ② Set up RecyclerView
        binding.rvScores.layoutManager = LinearLayoutManager(this)
        binding.rvScores.adapter = adapter

        // ③ Load leaderboard data
        loadScores()
    }

    private fun loadScores() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.api.topScores()
                if (response.isSuccessful) {
                    adapter.submitList(response.body())
                } else {
                    showToast("Load failed: ${response.code()}")
                }
            } catch (e: Exception) {
                showToast("Network error: ${e.localizedMessage}")
            }
        }
    }

    private fun showToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

package com.example.group3ca

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.group3ca.LoginActivity
import com.example.group3ca.PlayCardActivity
import com.example.group3ca.adapter.LeaderboardAdapter
import com.example.group3ca.databinding.ActivityFetchBinding
import com.example.group3ca.databinding.ActivityLeaderboardBinding
import com.example.group3ca.dto.LeaderboardEntry
import com.example.group3ca.service.ApiClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var adapter: LeaderboardAdapter
    private lateinit var leaderboardList: List<LeaderboardEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = LeaderboardAdapter(this, leaderboardList)
        binding.listLeaderboard.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.leaderboard)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Display player timing
        var playerTiming = intent?.getIntExtra("Timing", 0) ?: 0

        val hours = playerTiming / 3600
        val minutes = (playerTiming % 3600) / 60
        val seconds = playerTiming % 60

        val timeFormatted = String.format("Time: %02d:%02d:%02d", hours, minutes, seconds)

        binding.textYourTiming.text = "Your timing: ${timeFormatted}"

        // Call for leaderboard list
        ApiClient.instance.getLeaderboard().enqueue(object : Callback<List<LeaderboardEntry>> {
            override fun onResponse(
                call: Call<List<LeaderboardEntry>>,
                response: Response<List<LeaderboardEntry>>
            ) {
                if (response.isSuccessful) {

                    leaderboardList = response.body() ?: emptyList()

                } else {
                    Toast.makeText(this@LeaderboardActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<LeaderboardEntry>>, t: Throwable) {
                Toast.makeText(this@LeaderboardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // init the restart button
        initNewGame()
    }

    fun initNewGame() {
        binding.apply {
            btnANewStart.setOnClickListener() {

                startActivity(Intent(this@LeaderboardActivity, FetchActivity::class.java))
                finish()

            }

        }

    }
}
package com.example.group3ca

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.group3ca.LoginActivity
import com.example.group3ca.adapter.CardAdapter
import com.example.group3ca.dto.GameRequestDto
import com.example.group3ca.dto.LoginRequestDto
import com.example.group3ca.dto.LoginResponseDto
import com.example.group3ca.service.ApiClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PlayCardActivity : AppCompatActivity(), CardAdapter.OnCardClickListener {

    //----------------------------------------------------------
    // For playing cards----------------------------------------
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter

    // For keeping track of matches
    private lateinit var matchCounterText: TextView
    private var totalMatches = 6
    private var currentMatches = 0

    // For the timer
    private lateinit var timerText: TextView
    private var countSeconds = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timerRunnable: Runnable

//    private val drawableImageIds = listOf(
//        R.drawable.image1, R.drawable.image2, R.drawable.image3,
//        R.drawable.image4, R.drawable.image5, R.drawable.image6
//    )

    // For sound effects
    private var matchPlayer: MediaPlayer? = null
    private var noMatchPlayer: MediaPlayer? = null
    private var completePlayer: MediaPlayer? = null
    private var bgmPlayer: MediaPlayer? = null




    // List of images
    private lateinit var cardImages: List<String>

    // Whether a card is faced up or not
    private lateinit var faceUp: MutableList<Boolean>

    // Keep track of the position of the first card
    private var firstCardIndex: Int? = null

    // Shouldn't be allowed to click while it is "busy"
    private var isBusy = false
    //----------------------------------------------------------


    //----------------------------------------------------------
    // For displaying advertisement images----------------------

    lateinit var imageView: ImageView
    private lateinit var sharedPrefs: SharedPreferences

    // List of image URLs for advertisement images
    private val imageUrls = listOf(
        "http://10.0.2.2:5167/images/milo.jpg",
        "http://10.0.2.2:5167/images/coke.jpg",
        "http://10.0.2.2:5167/images/mcdonalds.jpg",
        "http://10.0.2.2:5167/images/donki.jpg"
    )
    private var currentImageIndex = 0 // Index to track which image to display
    private val handler2 = Handler(Looper.getMainLooper()) // Handler to manage repeating tasks
    //-----------------------------------------------------------


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playcard)

        // Background music
        bgmPlayer = MediaPlayer.create(this, R.raw.background)
        bgmPlayer?.isLooping = true  // Loop the music
        // Set volume: (leftVolume, rightVolume), values between 0.0 and 1.0
        bgmPlayer?.setVolume(0.4f, 0.4f)  // softer background music
        bgmPlayer?.start()


        //----------------------------------------------------------
        // For playing cards----------------------------------------
        // Duplicate each image
//        for (id in drawableImageIds) {
//            cardImages.add(id)
//            cardImages.add(id)
//        }
//
//        // Shuffle the list
//        cardImages.shuffle()
        sharedPrefs = getSharedPreferences("UserSession", MODE_PRIVATE)

        val selectedImages = intent.getStringArrayListExtra("selectedImages") ?: arrayListOf()
        cardImages = (selectedImages + selectedImages).shuffled()
        faceUp = MutableList(cardImages.size) { false }

        // Arrange the cards in a grid with 3 columns
        recyclerView = findViewById(R.id.cardsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        // Bind image to each card view and assign the adapter to RecyclerView
        adapter = CardAdapter(cardImages, faceUp, this)
        recyclerView.adapter = adapter

        // For counting the number of matches
        matchCounterText = findViewById(R.id.matchCounterText)

        // For the timer
        timerText = findViewById(R.id.timerText)
        timerRunnable = object : Runnable {
            override fun run() {
                countSeconds++

                val hours = countSeconds / 3600
                val minutes = (countSeconds % 3600) / 60
                val seconds = countSeconds % 60

                val timeFormatted = String.format("Time: %02d:%02d:%02d", hours, minutes, seconds)
                timerText.text = timeFormatted

                handler.postDelayed(this, 1000)  // schedule again after 1 second
            }
        }
        handler.post(timerRunnable)  // start timer
        //----------------------------------------------------------


        //----------------------------------------------------------
        // For displaying advertisement images----------------------
        imageView = findViewById(R.id.imageView)


        // Get the tier from shared preferences to determine whether to play ads or not
        val Tier = sharedPrefs.getString("Tier", "free")

        // Start displaying images with a 30-second interval

        if (Tier == "free") {
            startImageCycle()

            handler2.postDelayed(object : Runnable {
                override fun run() {
                    // Get the next image URL
                    val imageUrl = imageUrls[currentImageIndex]

                    // Use Glide to load and display the image
                    Glide.with(this@PlayCardActivity)
                        .load(imageUrl) // URL of the ad image
                        .into(imageView) // Set image in ImageView

                    imageView.visibility = View.VISIBLE

                    // Move to the next image in the list (loop back to the start if at the end)
                    currentImageIndex = (currentImageIndex + 1) % imageUrls.size

                    // Schedule the next image change
                    handler2.postDelayed(this, 30000) // Change image every 30 seconds
                }
            }, 0) // Start immediately
        }


        //---------------------------------------------------------------

    }

    //---------------------------------------------------------------
    // For playing cards---------------------------------------------
    override fun onCardClicked(position: Int) {

        // Cannot click if the card is faced up already
        //  or while the two cards are faced up ("busy")
        if (isBusy || faceUp[position]) return

        faceUp[position] = true
        adapter.notifyItemChanged(position)


        if (firstCardIndex == null) {
            // If no card has been clicked yet, this is the first card
            firstCardIndex = position
        } else {
            isBusy = true
            // If the first card has been chosen already, this is the second card
            val secondCardIndex = position

            if (cardImages[firstCardIndex!!] == cardImages[secondCardIndex]) {
                // Match!
                matchPlayer = MediaPlayer.create(this, R.raw.match)
                matchPlayer?.start()

                currentMatches += 1
                matchCounterText.text = "Matches: $currentMatches/$totalMatches"

                if (currentMatches == totalMatches) {
                    // All pairs matched
                    // Stop the timer
                    handler.removeCallbacks(timerRunnable)

                    // Post request to leaderboard
                    val UserId = sharedPrefs.getString("UserId", "AAA") ?: "AAA"
                    val Timing = countSeconds

                    val request = GameRequestDto(UserId, Timing)

                    // Use created ApiClient to call createGame function that calls corresponding backend createGame
                    ApiClient.instance.createGame(request).enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@PlayCardActivity, "Game saved!", Toast.LENGTH_SHORT).show()

                            } else {
                                Toast.makeText(this@PlayCardActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(this@PlayCardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })

                    // Intent to LeaderboardActivity upon successful game save
                    startActivity(Intent(this@PlayCardActivity, LeaderboardActivity::class.java))
                    intent.putExtra("Timing", countSeconds)
                    finish()


                    // Game complete
                    bgmPlayer?.stop()
                    bgmPlayer?.release()
                    bgmPlayer = null
                    completePlayer = MediaPlayer.create(this, R.raw.win)
                    completePlayer?.setVolume(0.4f, 0.4f)
                    completePlayer?.start()
                    // Go to leaderboard?
                }

                // Reset firstCardIndex to null and allow other cards to be clicked
                firstCardIndex = null
                isBusy = false

            } else {
                // Not match
                noMatchPlayer = MediaPlayer.create(this, R.raw.no_match)
                noMatchPlayer?.start()
                // Flip back the cards after 1 second
                Handler(Looper.getMainLooper()).postDelayed({
                    faceUp[firstCardIndex!!] = false
                    faceUp[secondCardIndex] = false

                    adapter.notifyItemChanged(firstCardIndex!!)
                    adapter.notifyItemChanged(secondCardIndex)

                    firstCardIndex = null
                    isBusy = false
                }, 1000)
            }
        }
    }
    //---------------------------------------------------------------


    //----------------------------------------------------------
    // For displaying advertisement images----------------------

    private fun startImageCycle() {
        // Load the first image using Glide
        displayNextImage()
    }

    private fun displayNextImage() {
        // Get the URL of the next image
        val imageUrl = imageUrls[currentImageIndex]

        // Use Glide to download and display the image asynchronously
        Glide.with(this)
            .load(imageUrl) // URL of the image
            .into(imageView) // Target ImageView
    }
    //--------------------------------------------------------------


    override fun onDestroy() {
        super.onDestroy()
        matchPlayer?.release()
        noMatchPlayer?.release()
        completePlayer?.release()
        bgmPlayer?.release()
        bgmPlayer = null
    }



}
package com.example.group3ca

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager

class PlayCardActivity : AppCompatActivity(), CardAdapter.OnCardClickListener {

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

    private val drawableImageIds = listOf(
        R.drawable.image1, R.drawable.image2, R.drawable.image3,
        R.drawable.image4, R.drawable.image5, R.drawable.image6
    )

    // List of images
    private val cardImages = mutableListOf<Int>()

    // Whether a card is faced up or not
    private val faceUp = MutableList(12) { false }

    // Keep track of the position of the first card
    private var firstCardIndex: Int? = null

    // Shouldn't be allowed to click while it is "busy"
    private var isBusy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playcard)

        // Duplicate each image
        for (id in drawableImageIds) {
            cardImages.add(id)
            cardImages.add(id)
        }

        // Shuffle the list
        cardImages.shuffle()

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

    }

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
                currentMatches += 1
                matchCounterText.text = "Matches: $currentMatches/$totalMatches"

                if (currentMatches == totalMatches) {
                    // All pairs matched
                    // Stop the timer
                    handler.removeCallbacks(timerRunnable)
                    // Go to leaderboard?
                }

                // Reset firstCardIndex to null and allow other cards to be clicked
                firstCardIndex = null
                isBusy = false

            } else {
                // Not match
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

}
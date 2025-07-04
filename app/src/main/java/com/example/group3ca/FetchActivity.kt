package com.example.group3ca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.group3ca.databinding.ActivityFetchBinding
import org.jsoup.Jsoup

class FetchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFetchBinding
    private var bgThread: Thread? = null
    private lateinit var adapter: FetchAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFetchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = FetchAdapter { selectedList ->
            binding.startGameButton.isEnabled = selectedList.size == 6
        }

        binding.imageRecycler.layoutManager = GridLayoutManager(this, 4)
        binding.imageRecycler.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fetchMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialise
        initFetch()
    }


    // Function that initialises the activity elements
    fun initFetch() {

        binding.apply {

            btnFetch.setOnClickListener() {
                val url = urlInput.text.toString().trim()

                if (url.isNotEmpty()) {
                    startImageDownload(url)
                }

            }

            startGameButton.setOnClickListener() {
                val selectedImages = adapter.getSelectedImages()
                if (selectedImages.size == 6) {
                    val intent = Intent(this@FetchActivity, PlayCardActivity::class.java)
                    intent.putStringArrayListExtra("selectedImages", ArrayList(selectedImages))
                    startActivity(intent)
                }
            }


        }

    }

    // Function to scrape the URL page with Jsoup, then pass to adapter, and update UI
    fun startImageDownload(url: String) {

        bgThread?.interrupt()

        // Variable can be adjusted to change how many images to download
        val maxImages = 20

        bgThread = Thread {
          try {

              // Retrieve the DOM from the URL
              val page = Jsoup.connect(url)
                  .userAgent("Mozilla/5.0")
                  .timeout(10_000)
                  .get()

              // Finds elements on the DOM that have img tags, with src OR data-cfsrc (that stocksnap uses) attribute
              val imageUrls = page.select("img[src], img[data-cfsrc]")
                  .mapNotNull {
                      when {
                          it.hasAttr("src") -> it.attr("abs:src")
                          it.hasAttr("data-cfsrc") -> it.attr("abs:data-cfsrc")
                          else -> null
                      }
                  }
                  .filter { it.isNotBlank() && !it.endsWith(".svg", ignoreCase = true) } // Filter out .svg because we don't want that
                  .take(maxImages)

              runOnUiThread {
                  binding.beginText.visibility = View.GONE

                  binding.progBar.visibility = View.VISIBLE
                  binding.progBar.max = imageUrls.size
                  binding.progBar.progress = 0 // Reset on each fetch

                  binding.progressText.visibility = View.VISIBLE
                  binding.progressText.text = "Downloading 0 of ${imageUrls.size} images..." // Populate the max dynamically
              }

              val downloaded = mutableListOf<String>()
              imageUrls.forEachIndexed { index, imgUrl ->
                  if (Thread.interrupted()) return@Thread

                  downloaded.add(imgUrl)

                  // Updates progress bar
                  runOnUiThread {
                      binding.progBar.progress = index + 1
                      binding.progressText.text =
                          "Downloading ${index + 1} of ${imageUrls.size} images..."
                  }

                  Log.d("Log downloaded items", downloaded.toString())
                  // Intentionally adding delay so that we can see the download progress
                  Thread.sleep(300)

                  runOnUiThread {
                      adapter.setImages(downloaded)
                      binding.startGameButton.visibility = View.VISIBLE
                      binding.progressText.text = "Download complete! Now select 6 images."
                  }
              }

          } catch (e: Exception) {
              runOnUiThread {
                  Toast.makeText(this@FetchActivity, "Something went wrong.", Toast.LENGTH_SHORT).show()

              }

          }
      }
        bgThread?.start()
    }


}
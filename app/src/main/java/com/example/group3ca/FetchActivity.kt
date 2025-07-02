package com.example.group3ca

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.group3ca.databinding.ActivityFetchBinding
import com.example.group3ca.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import java.net.URL

class FetchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFetchBinding
    private var bgThread: Thread? = null
    private lateinit var adapter: ImagesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFetchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ImagesAdapter { selectedList ->
            binding.startGameButton.isEnabled = selectedList.size == 6
        }

        binding.imageRecycler.layoutManager = GridLayoutManager(this, 5)
        binding.imageRecycler.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fetchMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



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

    // Function to download images to file
    fun startImageDownload(url: String) {

        bgThread?.interrupt()

        val maxImages = 20

        bgThread = Thread {
          try {
              val page = Jsoup.connect(url)
                  .userAgent("Mozilla/5.0")
                  .timeout(10_000)
                  .get()

              val imageUrls = page.select("img[src], img[data-cfsrc]")
                  .mapNotNull {
                      when {
                          it.hasAttr("src") -> it.attr("abs:src")
                          it.hasAttr("data-cfsrc") -> it.attr("abs:data-cfsrc")
                          else -> null
                      }
                  }
                  .filter { it.isNotBlank() && !it.endsWith(".svg", ignoreCase = true) }
                  .take(maxImages)

              runOnUiThread {
                  binding.beginText.visibility = View.GONE

                  binding.progBar.visibility = View.VISIBLE
                  binding.progBar.max = imageUrls.size
                  binding.progBar.progress = 0

                  binding.progressText.visibility = View.VISIBLE
                  binding.progressText.text = "Downloading 0 of ${imageUrls.size} images..."
              }

              val downloaded = mutableListOf<String>()
              imageUrls.forEachIndexed { index, imgUrl ->
                  if (Thread.interrupted()) return@Thread

                  // Simulate downloading (or actually download if needed)
                  downloaded.add(imgUrl)

                  runOnUiThread {
                      binding.progBar.progress = index + 1
                      binding.progressText.text =
                          "Downloading ${index + 1} of ${imageUrls.size} images..."
                  }
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

    // Function to download images to file
    fun downloadToFile(url: String, file: File)
    {
        URL(url).openStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    // Function to make a new file
    fun makeFile(fname: String): File
    {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(dir, fname)
    }

    // Function to update the view
//    fun updateImageView(file: File)
//    {
//        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
//        binding.apply{
//            imageView.setImageBitmap(bitmap)
//        }
//    }



}
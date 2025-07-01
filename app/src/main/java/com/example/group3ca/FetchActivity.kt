package com.example.group3ca

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.group3ca.databinding.ActivityFetchBinding
import com.example.group3ca.databinding.ActivityMainBinding
import java.io.File
import java.net.URL

class FetchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFetchBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFetchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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



            }


        }

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
    fun updateImageView(file: File)
    {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        binding.apply{
            imageView.setImageBitmap(bitmap)
        }
    }

}
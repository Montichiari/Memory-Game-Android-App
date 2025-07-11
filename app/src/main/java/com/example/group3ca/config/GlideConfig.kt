package com.example.group3ca.config

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream

// This config file is to configure Glide requests with user-agent Mozilla, to avoid 403.

@GlideModule
class GlideConfig : AppGlideModule() {
    override fun registerComponents(
        context: Context,
        glide: Glide,
        registry: Registry
    ) {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0")
                    .header("Referer", "https://stocksnap.io/") // optional but helpful
                    .build()
                chain.proceed(newRequest)
            }
            .build()

        val factory = OkHttpUrlLoader.Factory(client)
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            factory
        )
    }
}
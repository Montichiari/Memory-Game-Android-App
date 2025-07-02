package com.example.group3ca

import android.graphics.Color
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.widget.Toast


class ImagesAdapter(

    private val maxSelectable: Int = 6,
    private val onSelectionChanged: (List<String>) -> Unit
    ) : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

        private val imageUrls = mutableListOf<String>()
        private val selected = mutableSetOf<String>()

        fun setImages(urls: List<String>) {
            imageUrls.clear()
            imageUrls.addAll(urls)
            selected.clear()
            notifyDataSetChanged()
        }

        fun getSelectedImages(): List<String> = selected.toList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val imageView = ImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    300
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            return ImageViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val url = imageUrls[position]
            val imageView = holder.imageView

            Glide.with(imageView.context)
                .load(url)
                .into(imageView)

            imageView.alpha = if (selected.contains(url)) 0.5f else 1.0f

            val padding = 8
            imageView.setPadding(padding, padding, padding, padding)
            imageView.setBackgroundColor(
                if (selected.contains(url)) Color.RED
                else Color.TRANSPARENT
            )

            imageView.setOnClickListener {
                if (selected.contains(url)) {
                    selected.remove(url)
                } else {
                    if (selected.size >= maxSelectable) {
                        Toast.makeText(imageView.context, "Max $maxSelectable images.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    selected.add(url)
                }
                notifyItemChanged(position)
                onSelectionChanged(selected.toList())
            }
        }

        override fun getItemCount(): Int = imageUrls.size

        class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

}
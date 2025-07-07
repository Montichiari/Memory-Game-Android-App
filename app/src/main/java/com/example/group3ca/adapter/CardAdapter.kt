package com.example.group3ca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.group3ca.R

class CardAdapter(
    private val cardImageUrls: List<String>,
    private val faceUp: List<Boolean>,
    private val clickListener: OnCardClickListener
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    interface OnCardClickListener {
        fun onCardClicked(position: Int)
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.cardImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int = cardImageUrls.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        if (faceUp[position]) {
            // Show the card
            Glide.with(holder.imageView.context)
                .load(cardImageUrls[position])
                .into(holder.imageView)

        } else {
            // Show the back
            holder.imageView.setImageResource(R.drawable.card_back)
        }

        holder.itemView.setOnClickListener {
            clickListener.onCardClicked(position)
        }
    }
}

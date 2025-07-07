package com.example.group3ca.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.group3ca.R
import com.example.group3ca.dto.LeaderboardEntry

class LeaderboardAdapter(

    private val context: Context,
    private val entries: List<LeaderboardEntry>
) : BaseAdapter() {

    override fun getCount(): Int = entries.size

    override fun getItem(position: Int): Any = entries[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_leaderboard, parent, false)

        val rank = view.findViewById<TextView>(R.id.textRank)
        val username = view.findViewById<TextView>(R.id.textUsername)
        val timing = view.findViewById<TextView>(R.id.textTiming)

        val entry = entries[position]

        rank.text = "${position + 1}"
        username.text = entry.username

        var countSeconds = entry.timing

        val hours = countSeconds / 3600
        val minutes = (countSeconds % 3600) / 60
        val seconds = countSeconds % 60

        val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        timing.text = "${timeFormatted}"



        return view
    }
}

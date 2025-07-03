package com.example.group3ca

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.group3ca.databinding.RowScoreBinding
import com.example.group3ca.network.ScoreDto

class ScoreAdapter : ListAdapter<ScoreDto, ScoreAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ScoreDto>() {
            override fun areItemsTheSame(o: ScoreDto, n: ScoreDto) = o === n
            override fun areContentsTheSame(o: ScoreDto, n: ScoreDto) = o == n
        }
    }

    inner class VH(private val binding: RowScoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ScoreDto, pos: Int) {
            binding.tvRank.text = (pos + 1).toString()
            binding.tvName.text = item.username
            binding.tvTime.text = String.format("%.2fs", item.timeMs / 1000.0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RowScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position), position)
}

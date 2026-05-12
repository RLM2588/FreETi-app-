package com.example.freeti.adapters_pack

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.R
import com.example.freeti.data.local.entity.DTasks

class UnassignedTaskAdapter(
    private val onClick: (DTasks) -> Unit
) : ListAdapter<DTasks, UnassignedTaskAdapter.ViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DTasks>() {
            override fun areItemsTheSame(oldItem: DTasks, newItem: DTasks) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: DTasks, newItem: DTasks) =
                oldItem.title == newItem.title && oldItem.colour == newItem.colour
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_unassigned_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorIndicator: View = itemView.findViewById(R.id.color_indicator)
        private val taskTitle: TextView = itemView.findViewById(R.id.task_title)

        fun bind(task: DTasks) {
            taskTitle.text = task.title
            val color = try {
                Color.parseColor("#${task.colour}")
            } catch (e: Exception) {
                Color.LTGRAY
            }
            colorIndicator.setBackgroundColor(color)
            itemView.setOnClickListener { onClick(task) }
        }
    }
}
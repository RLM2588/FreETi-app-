package com.example.freeti

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PollAdapter(
    private var polls: List<Poll>,
    private val onPollClick: (Poll) -> Unit,
    private val onDeleteClick: (Poll) -> Unit
) : RecyclerView.Adapter<PollAdapter.ViewHolder>() {

    fun updateList(newPolls: List<Poll>) {
        polls = newPolls
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_poll, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val poll = polls[position]
        holder.questionTextView.text = poll.question
        holder.itemView.setOnClickListener { onPollClick(poll) }
        holder.deleteButton.setOnClickListener { onDeleteClick(poll) }
    }

    override fun getItemCount() = polls.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionTextView: TextView = itemView.findViewById(R.id.tvPollQuestion)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDeletePoll)
    }
}
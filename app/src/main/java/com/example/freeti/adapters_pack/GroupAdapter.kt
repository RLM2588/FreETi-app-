package com.example.freeti.adapters_pack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.R
import com.example.freeti.data.local.entity.DGroups
import com.google.android.material.card.MaterialCardView

class GroupAdapter(
    private var groups: List<DGroups>,
    private val onItemClick: (DGroups) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount() = groups.size

    fun updateList(newList: List<DGroups>) {
        groups = newList
        notifyDataSetChanged()
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        private val imageGroup: ImageView = itemView.findViewById(R.id.imageGroup)
        private val textGroupName: TextView = itemView.findViewById(R.id.textGroupName)

        fun bind(group: DGroups) {
            textGroupName.text = group.title
            imageGroup.setImageResource(group.imageResId)

            cardView.setOnClickListener {
                onItemClick(group)
            }
        }
    }
}
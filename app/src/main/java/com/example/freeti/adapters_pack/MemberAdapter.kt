package com.example.freeti.adapters_pack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.Member
import com.example.freeti.R

class MemberAdapter(
    private var members: List<Member>,
    private val onRoleClick: (Member) -> Unit,
    private val onDeleteClick: (Member) -> Unit
): RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount() = members.size

    fun updateData(newMembers: List<Member>) {
        this.members = newMembers
        notifyDataSetChanged() // В идеале здесь лучше использовать DiffUtil
    }

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textUserName: TextView = itemView.findViewById(R.id.textUserName)
        private val textUserRole: TextView = itemView.findViewById(R.id.textUserRole)
        private val buttonPromote: Button = itemView.findViewById(R.id.buttonPromote)
        private val buttonKick: Button = itemView.findViewById(R.id.buttonKick)
        private val member_faceInput: TextView = itemView.findViewById(R.id.member_faceInput)

        fun bind(member: Member) {
            textUserName.text = member.username
            textUserRole.text = member.role
            member_faceInput.text = member.avatar

            buttonPromote.setOnClickListener {
                onRoleClick(member)
            }

            buttonKick.setOnClickListener {
                onDeleteClick(member)
            }
        }
    }
}
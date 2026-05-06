package com.example.freeti.adapters_pack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.Member
import com.example.freeti.R

class MemberAdapter(private val members: List<Member>,
                    private val onRoleClick: (Member) -> Unit,
                    private val onDeleteClick: (Member) -> Unit):
    RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position], position)
    }

    override fun getItemCount() = members.size

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textUserName: TextView = itemView.findViewById(R.id.textUserName)
        private val textUserRole: TextView = itemView.findViewById(R.id.textUserRole)
        private val buttonPromote: Button = itemView.findViewById(R.id.buttonPromote)
        private val buttonKick: Button = itemView.findViewById(R.id.buttonKick)
        private val member_faceInput: TextView = itemView.findViewById(R.id.member_faceInput)

        fun bind(member: Member, position: Int) {
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

//package com.example.freeti.adapters_pack
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.freeti.R
//import com.example.freeti.data.local.entity.DUsers
//
//class UserAdapter(
//    private var users: List<DUsers>,
//    private val onItemClick: (DUsers) -> Unit
//) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
//
//    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val nicknameTextView: TextView = itemView.findViewById(R.id.textViewNickname)
//        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
//        val avatar: TextView = itemView.findViewById(R.id.user_faceInput)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_user, parent, false)
//        return UserViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
//        val user = users[position]
//        holder.nicknameTextView.text = user.username
//        holder.nameTextView.text = user.login
//        holder.avatar.text = user.avatar
//        holder.itemView.setOnClickListener { onItemClick(user) }
//    }
//
//    override fun getItemCount() = users.size
//
//    // Метод для обновления списка из серча
//    fun updateList(newList: List<DUsers>) {
//        users = newList
//        notifyDataSetChanged()
//    }
//}
package com.example.freeti.adapters_pack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.R
import com.example.freeti.data.local.entity.DUsers

class UserAdapter(
    private var users: List<DUsers>,
    private val onItemClick: (DUsers) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nicknameTextView: TextView = itemView.findViewById(R.id.textViewNickname)
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val avatar: TextView = itemView.findViewById(R.id.user_faceInput)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.nicknameTextView.text = user.username
        holder.nameTextView.text = user.login
        holder.avatar.text = user.avatar
        var textAvatar = holder.avatar.text
        if (textAvatar.length > 1 && textAvatar[0] == '_') {
            holder.avatar.rotation = 0f
            textAvatar = textAvatar.substring(1, textAvatar.length)
        } else holder.avatar.rotation = 90f
        holder.avatar.text = textAvatar
        holder.itemView.setOnClickListener { onItemClick(user) }
    }

    override fun getItemCount() = users.size

    // Метод для обновления списка из серча
    fun updateList(newList: List<DUsers>) {
        users = newList
        notifyDataSetChanged()
    }
}
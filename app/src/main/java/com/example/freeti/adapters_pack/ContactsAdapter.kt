package com.example.freeti.adapters_pack

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.R
import com.example.freeti.data_base.UserWithContactStatus

class ContactsAdapter(
    private val context: Context,
    private var items: List<UserWithContactStatus>,
    private val onItemClick: (UserWithContactStatus) -> Unit,
    private val onItemLongClick: ((UserWithContactStatus) -> Unit)? = null
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nickname: TextView = view.findViewById(R.id.textViewNickname)
        val login: TextView = view.findViewById(R.id.textViewName)
        val avatar: TextView = view.findViewById(R.id.user_faceInput)
        val rootLayout: View = view.findViewById(R.id.back_user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nickname.text = item.username
        holder.login.text = item.login

        var textAvatar = item.avatar
        if (textAvatar.length > 1 && textAvatar[0] == '_') {
            holder.avatar.rotation = 0f
            textAvatar = textAvatar.substring(1, textAvatar.length)
        } else holder.avatar.rotation = 90f
        holder.avatar.text = textAvatar.trim()

        if (item.isFriend) {
            holder.rootLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.for_privacy_private))
            val colorBg = ContextCompat.getColor(context, R.color.bg)
            holder.nickname.setTextColor(colorBg)
            holder.login.setTextColor(colorBg)
        } else {
            holder.rootLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.bg))
            val colorText = ContextCompat.getColor(context, R.color.for_text)
            holder.nickname.setTextColor(colorText)
            holder.login.setTextColor(colorText)
        }
        holder.itemView.setOnClickListener { onItemClick(item) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(item)
            true
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<UserWithContactStatus>) {
        items = newItems
        notifyDataSetChanged()
    }
}
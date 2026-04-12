package com.example.freeti

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MembersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerViewMembers)
        recyclerView.layoutManager = LinearLayoutManager(this)

    }


    inner class MemberAdapter(private val members: List<Member>) :
        RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_member, parent, false)
            return MemberViewHolder(view)
        }

        override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
            holder.bind(members[position])
        }

        override fun getItemCount() = members.size

        inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textUserName: TextView = itemView.findViewById(R.id.textUserName)
            private val textUserRole: TextView = itemView.findViewById(R.id.textUserRole)

            fun bind(member: Member) {
                textUserName.text = member.name
                textUserRole.text = if (member.role == "admin") "Администратор" else "Участник"
            }
        }
    }
}
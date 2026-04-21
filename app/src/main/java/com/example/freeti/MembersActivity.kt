package com.example.freeti

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MembersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var memberAdapter: MemberAdapter
    private val membersList = mutableListOf<Member>()

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


    inner class MemberAdapter(private val members: MutableList<Member>) :
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

            fun bind(member: Member, position: Int) {
                textUserName.text = member.name
                textUserRole.text = if (member.role == "admin") "Администратор" else "Участник"


                buttonPromote.setOnClickListener {
                    if (member.role == "admin") {
                        Toast.makeText(itemView.context, "${member.name} уже администратор", Toast.LENGTH_SHORT).show()
                    } else {
                        member.role = "admin"
                        textUserRole.text = "Администратор"
                        Toast.makeText(itemView.context, "${member.name} повышен до администратора", Toast.LENGTH_SHORT).show()
                    }
                }


                buttonKick.setOnClickListener {
                    members.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(itemView.context, "${member.name} удалён из группы", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
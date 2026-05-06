package com.example.freeti

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.adapters_pack.MemberAdapter
import com.example.freeti.adapters_pack.TasksNoTimeAdapter
import com.example.freeti.repository.MembersRepository
import kotlinx.coroutines.launch

class MembersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var membersList: List<Member>
    private val membersRepository: MembersRepository by lazy {
        (application as MyApp).appContainer.membersRepository
    }

    private lateinit var groupId: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_members)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerViewMembers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        groupId = intent.getStringExtra("group_id").toString()
        if (groupId == "") finish()

        updateList()

        val memberAdapter = MemberAdapter(
            members = membersList,
            onRoleClick = { user ->
                lifecycleScope.launch {
                    if(membersRepository.roleMember(groupId, user.id)) {

                    }
                }
                updateList()
            },
            onDeleteClick = { user ->
                updateList()
            }
        )
        recyclerView.adapter = memberAdapter
    }

    fun updateList(){
        lifecycleScope.launch {
            membersRepository.getMembers(groupId)
            membersList = (application as MyApp).appContainer.groupMemberDao.getGroupMembersMemb(groupId)
        }
    }
}
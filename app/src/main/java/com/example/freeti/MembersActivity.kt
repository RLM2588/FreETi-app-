package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.adapters_pack.MemberAdapter
import com.example.freeti.repository.MembersRepository
import kotlinx.coroutines.launch

class MembersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var groupId: String
    private var currentUserId: Int = 0

    private val membersRepository: MembersRepository by lazy {
        (application as MyApp).appContainer.membersRepository
    }
    private val groupMemberDao by lazy {
        (application as MyApp).appContainer.groupMemberDao
    }

    private lateinit var buttonLeaveGroup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        groupId = intent.getStringExtra("group_id") ?: ""
        if (groupId.isEmpty()) {
            finish()
            return
        }

        currentUserId = (application as MyApp).appContainer.tokenManager.getUserId()

        val toolbar = findViewById<Toolbar>(R.id.toolbar_members)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerViewMembers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        memberAdapter = MemberAdapter(
            members = emptyList(),
            onRoleClick = { user ->
                lifecycleScope.launch {
                    val success = membersRepository.roleMember(groupId, user.id)
                    if (success) {
                        loadMembers()
                    } else {
                        showToast("Ошибка изменения роли. Проверьте права.")
                    }
                }
            },
            onDeleteClick = { user ->
                lifecycleScope.launch {
                    val success = membersRepository.deleteMember(groupId, user.id)
                    if (success) {
                        loadMembers()
                    } else {
                        showToast("Ошибка удаления пользователя. Проверьте права.")
                    }
                }
            }
        )
        recyclerView.adapter = memberAdapter

        buttonLeaveGroup = findViewById(R.id.buttonLeaveGroup)
        val buttonDeleteGroup = findViewById<Button>(R.id.buttonDeleteGroup)
        val buttonAddMember = findViewById<Button>(R.id.buttonAddMember)

        buttonLeaveGroup.setOnClickListener {
            lifecycleScope.launch {
                val success = membersRepository.leaveGroup(groupId)
                if (success) {
                    showToast("Вы покинули группу")
                    finish()
                } else {
                    showToast("Не удалось покинуть группу")
                }
            }
        }

        buttonDeleteGroup.setOnClickListener {
            lifecycleScope.launch {
                val success = membersRepository.deleteGroup(groupId)
                if (success) {
                    showToast("Группа удалена")
                    finish()
                } else {
                    showToast("Ошибка удаления. Возможно, у вас нет прав владельца.")
                }
            }
        }

        buttonAddMember.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            intent.putExtra("group_id", groupId)
            startActivity(intent)
        }

        loadMembers()
    }

    override fun onResume() {
        super.onResume()

        loadMembers()
    }

    private fun loadMembers() {
        lifecycleScope.launch {
            val cachedMembers = groupMemberDao.getGroupMembersMemb(groupId)
            memberAdapter.updateData(cachedMembers)
            updateUIAfterDataLoad(cachedMembers)

            val isSuccess = membersRepository.getMembers(groupId)

            if (isSuccess) {
                val freshMembers = groupMemberDao.getGroupMembersMemb(groupId)
                memberAdapter.updateData(freshMembers)
                updateUIAfterDataLoad(freshMembers)
            } else {
                showToast("Не удалось обновить список с сервера")
            }
        }
    }

    private fun updateUIAfterDataLoad(membersList: List<Member>) {
        lifecycleScope.launch {
            val count = groupMemberDao.getCountGroupMembers(groupId)
            supportActionBar?.subtitle = "Участников: $count"
        }

        val currentUserMember = membersList.find { it.id == currentUserId }
        val buttonDeleteGroup = findViewById<Button>(R.id.buttonDeleteGroup)

        if (currentUserMember?.role?.uppercase() == "OWNER") {
            buttonDeleteGroup.visibility = View.VISIBLE
            buttonLeaveGroup.visibility = View.GONE
        } else {
            buttonDeleteGroup.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MembersActivity, message, Toast.LENGTH_SHORT).show()
    }
}
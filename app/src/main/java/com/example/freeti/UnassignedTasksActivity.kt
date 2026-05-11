package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.adapters_pack.UnassignedTaskAdapter
import com.example.freeti.view_model.UnassignedTasksViewModel
import com.example.freeti.view_model.UnassignedTasksViewModelFactory
import kotlinx.coroutines.launch

class UnassignedTasksActivity : AppCompatActivity() {

    private lateinit var viewModel: UnassignedTasksViewModel
    private lateinit var adapter: UnassignedTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unassigned_tasks)

        // Получаем зависимости
        val app = application as MyApp
        val taskDao = app.appContainer.myTasksDao
        val syncManager = app.appContainer.taskSyncManager
        val apiService = app.appContainer.apiService

        // ViewModel
        viewModel = ViewModelProvider(
            this,
            UnassignedTasksViewModelFactory(taskDao, syncManager, apiService)
        ).get(UnassignedTasksViewModel::class.java)

        // RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recycler_unassigned)
        adapter = UnassignedTaskAdapter { task ->
            val intent = Intent(this, NewTaskActivity::class.java)
            intent.putExtra("task_id", task.id)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Наблюдение за данными
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { tasks ->
                    adapter.submitList(tasks)
                }
            }
        }

        // Кнопка синхронизации
        val btnSync: ImageButton = findViewById(R.id.btn_sync_unassigned)
        btnSync.setOnClickListener {
            viewModel.syncWithServer()
        }
    }
}
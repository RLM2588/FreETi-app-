package com.example.freeti.adapters_pack

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.data.local.entity.DTasks
import com.example.freeti.databinding.ItemTaskNoTimeBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class TasksNoTimeAdapter(
    private val onDoneClick: (DTasks) -> Unit,
    private val onLongClick: (DTasks) -> Unit,
    private val onEditClick: (DTasks) -> Unit
) : ListAdapter<DTasks, TasksNoTimeAdapter.ViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DTasks>() {
            override fun areItemsTheSame(oldItem: DTasks, newItem: DTasks): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DTasks, newItem: DTasks): Boolean {
                // Сравниваем всё, что влияет на отображение
                return oldItem.title == newItem.title &&
                        oldItem.colour == newItem.colour &&
                        oldItem.status == newItem.status
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaskNoTimeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemTaskNoTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: DTasks) {
            binding.taskTitle.text = task.title
            val color = try {
                Color.parseColor("#${task.colour}")
            } catch (e: IllegalArgumentException) {
                Color.LTGRAY
            }
            binding.colorIndicator.setBackgroundColor(color)
            // НАЧАЛО ШАМАНСТВА
            val isDone = task.status == "DONE"
            if (isDone) {

                binding.taskTitle.paintFlags =
                    binding.taskTitle.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG

                binding.taskContentContainer.alpha = 0.4f
            }
            else {

                binding.taskTitle.paintFlags = binding.taskTitle.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.taskContentContainer.alpha = 1.0f
            }
            //КОНЕЦ ШАМАНСТВА

            // Чекбокс
            binding.doneCheckbox.setOnCheckedChangeListener(null)
            binding.doneCheckbox.isChecked = task.status == "DONE"
            binding.doneCheckbox.setOnCheckedChangeListener { _, _ ->
                onDoneClick(task)
            }

            // Короткое касание по карточке → редактирование
            binding.root.setOnClickListener {
                onEditClick(task)
            }

            // Долгое нажатие → перенос на завтра
            binding.root.setOnLongClickListener {
                onLongClick(task)
                true
            }
        }
    }
}
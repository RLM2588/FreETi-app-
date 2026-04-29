package com.example.freeti.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.freeti.R
import com.example.freeti.data.local.entity.DTasks
import java.util.concurrent.TimeUnit
import kotlin.math.max

class TaskTimelineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var cellDurationMinutes: Int = 30
    var timeColumnWidth: Float = 160f
    var rowHeight: Float = 80f
    var columnWidth: Float = 360f   // фиксированная ширина одной колонки

    private val timePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.for_text)
        textSize = 36f
        textAlign = Paint.Align.RIGHT
    }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.for_text)
        strokeWidth = 1.5f
    }
    private val taskRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 32f
        isFakeBoldText = true
    }
    private val timePaintSm = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 26f
    }

    private var tasks: List<DTasks> = emptyList()
    private var columns: List<List<DTasks>> = emptyList()
    private var dayStartMillis: Long = 0L
    private var dayEndMillis: Long = 3600_000L * 24
    private var totalRows: Int = 0

    interface OnTaskClickListener {
        fun onTaskClick(task: DTasks)
    }
    var onTaskClickListener: OnTaskClickListener? = null

    fun setTasks(newTasks: List<DTasks>) {
        tasks = newTasks
        if (tasks.isNotEmpty()) {
            dayStartMillis = tasks.minOf { it.start }
            dayEndMillis = tasks.maxOf { it.time_end }
        } else {
            val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            dayStartMillis = cal.timeInMillis
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
            dayEndMillis = cal.timeInMillis
        }
        dayEndMillis += 3600_000L
        columns = distributeTasks(tasks)
        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(dayEndMillis - dayStartMillis)
        totalRows = (totalMinutes / cellDurationMinutes).toInt()
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val colCount = maxOf(2, columns.size)
        val desiredWidth = timeColumnWidth + colCount * columnWidth
        val desiredHeight = rowHeight * totalRows
        val width = resolveSize(max(desiredWidth.toInt(),widthMeasureSpec), widthMeasureSpec)
        val height = resolveSize(desiredHeight.toInt(), heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (totalRows == 0) return

        val width = width.toFloat()
        val height = height.toFloat()
        //val colCount = maxOf(1, columns.size)

        // Горизонтальные линии сетки + время слева
        for (row in 0..totalRows) {
            val y = row * rowHeight
            canvas.drawLine(timeColumnWidth, y, width, y, gridPaint)
            if (row < totalRows) {
                val slotMillis = dayStartMillis + TimeUnit.MINUTES.toMillis((row * cellDurationMinutes).toLong())
                canvas.drawText(formatTime(slotMillis), timeColumnWidth - 16f, y + rowHeight - 12f, timePaint)
            }
        }
        // Вертикальная линия между временем и задачами
        canvas.drawLine(timeColumnWidth, 0f, timeColumnWidth, height, gridPaint)

        // Рисуем задачи
        for (colIndex in columns.indices) {
            val xStart = timeColumnWidth + colIndex * columnWidth
            for (task in columns[colIndex]) {
                val startSlot = getSlotIndex(task.start)
                val endSlot = getSlotIndex(task.time_end)

                val taskTop = startSlot * rowHeight + 3 * rowHeight / 8
                val taskBottom = endSlot * rowHeight + rowHeight - 3 * rowHeight / 8

                val padding = 12f
                val padding2 = 6f
                val rect1 = RectF(
                    xStart + padding2, taskTop + padding2,
                    xStart + columnWidth - padding2, taskBottom - padding2
                )

                taskRectPaint.color = Color.BLACK //ContextCompat.getColor(context, R.color.for_text)
                canvas.drawRoundRect(rect1, 12f, 12f, taskRectPaint)
                val rect = RectF(
                    xStart + padding, taskTop + padding,
                    xStart + columnWidth - padding, taskBottom - padding
                )

                val color = parseColor(task.colour)
                taskRectPaint.color = color
                canvas.drawRoundRect(rect, 12f, 12f, taskRectPaint)

                // Название задачи
                val titleText = task.title
                val maxTextWidth = columnWidth - 2 * padding - 8f
                titlePaint.textSize = minOf(36f, maxTextWidth / titleText.length.coerceAtLeast(1) * 2.0f)
                canvas.drawText(titleText, rect.left + 4f, rect.top + titlePaint.textSize + 4f, titlePaint)

                // Время задачи – теперь чуть выше, чтобы не слипалось с соседней
                val timeText = "${formatTime(task.start)}–${formatTime(task.time_end)}"
                timePaintSm.textSize = minOf(28f, maxTextWidth / timeText.length.coerceAtLeast(1) * 1.8f)
                canvas.drawText(timeText, rect.left + 4f, rect.bottom - 8f, timePaintSm)
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        cal.timeInMillis = millis
        return String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
    }

    private fun getSlotIndex(timestamp: Long): Int {
        val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(timestamp - dayStartMillis)
        return (diffMinutes / cellDurationMinutes).toInt()
    }

    private fun parseColor(hex: String): Int {
        return try {
            Color.parseColor("#$hex")
        } catch (e: IllegalArgumentException) {
            Color.LTGRAY
        }
    }

    private fun distributeTasks(tasks: List<DTasks>): List<List<DTasks>> {
        val sorted = tasks.sortedBy { it.start }
        val columns = mutableListOf<MutableList<DTasks>>()
        for (task in sorted) {
            var placed = false
            for (column in columns) {
                if (column.none { tasksOverlap(it, task) }) {
                    column.add(task)
                    placed = true
                    break
                }
            }
            if (!placed) {
                columns.add(mutableListOf(task))
            }
        }
        return columns
    }

    private fun tasksOverlap(a: DTasks, b: DTasks): Boolean {
        return a.start < b.time_end && b.start < a.time_end
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            //val colCount = maxOf(1, columns.size)
            for (colIndex in columns.indices) {
                val xStart = timeColumnWidth + colIndex * columnWidth
                if (x < xStart || x > xStart + columnWidth) continue
                for (task in columns[colIndex]) {
                    val startSlot = getSlotIndex(task.start)
                    val endSlot = getSlotIndex(task.time_end)
                    val taskTop = startSlot * rowHeight
                    val taskBottom = endSlot * rowHeight + rowHeight
                    if (y >= taskTop && y <= taskBottom) {
                        onTaskClickListener?.onTaskClick(task)
                        return true
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }
}
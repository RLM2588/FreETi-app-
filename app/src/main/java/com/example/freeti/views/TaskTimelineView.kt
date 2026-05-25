package com.example.freeti.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
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

    private var downX = 0f
    private var downY = 0f
    private var downTime = 0L
    private var isLongPressPossible = false
    private val longPressThreshold = 400L // миллисекунды
    private val touchSlop = 10f

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
        color = Color.BLACK
        textSize = 32f
    }

    private var tasks: List<DTasks> = emptyList()
    private var columns: List<List<DTasks>> = emptyList()
    private var dayStartMillis: Long = 0L
    private var dayEndMillis: Long = 0L
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
            val cal = java.util.Calendar.getInstance()
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            dayStartMillis = cal.timeInMillis
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
            dayEndMillis = cal.timeInMillis
        }
        dayStartMillis -= 3600_000L
        dayStartMillis = startToNear(dayStartMillis, cellDurationMinutes * 60 * 1000L)
        dayEndMillis += 2 * 3600_000L
        columns = distributeTasks(tasks)
        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(dayEndMillis - dayStartMillis)
        totalRows = (totalMinutes / cellDurationMinutes).toInt()
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val colCount = maxOf(1, columns.size)
        val desiredWidth = timeColumnWidth + colCount * columnWidth
        val desiredHeight = rowHeight * totalRows
        val width = resolveSize(max(widthMeasureSpec, desiredWidth.toInt()), widthMeasureSpec)
        val height = resolveSize(max(heightMeasureSpec, desiredHeight.toInt()), heightMeasureSpec)
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
                canvas.drawText(ViewsFunction.formatTime(slotMillis), timeColumnWidth - 16f, y - 2f, timePaint)
            }
        }
        // Вертикальная линия между временем и задачами
        canvas.drawLine(timeColumnWidth, 0f, timeColumnWidth, height, gridPaint)

        // Рисуем задачи
        for (colIndex in columns.indices) {
            val xStart = timeColumnWidth + colIndex * columnWidth
            val column = columns[colIndex]
            for (inx in 0..<(column).size) {
                val task = column[inx]

                val startSlot = getSlotIndexSt(task.start)
                val endSlot = getSlotIndexFn(task.time_end)

                var taskTop = startSlot * rowHeight
                var taskBottom = endSlot * rowHeight + rowHeight

                if (!(inx == 0 || getSlotIndexSt(column[inx].start) != getSlotIndexFn(column[inx - 1].time_end))) {
                    taskTop += 3 * rowHeight / 8
                }
                if (!(inx == column.size - 1 || getSlotIndexSt(column[inx+1].start) != getSlotIndexFn(column[inx].time_end))) {
                    taskBottom -= 3 * rowHeight / 8
                }

                val padding2 = 6f
                val rect2 = RectF(
                    xStart + padding2, taskTop + padding2,
                    xStart + columnWidth - padding2, taskBottom - padding2
                )

                taskRectPaint.color = Color.BLACK
                canvas.drawRoundRect(rect2, ViewsFunction.CORNER_RADIUS, ViewsFunction.CORNER_RADIUS, taskRectPaint)

                val padding = 12f
                val rect = RectF(
                    xStart + padding, taskTop + padding,
                    xStart + columnWidth - padding, taskBottom - padding
                )

                val color = parseColor(task.colour)
                taskRectPaint.color = color
                canvas.drawRoundRect(rect, ViewsFunction.CORNER_RADIUS, ViewsFunction.CORNER_RADIUS, taskRectPaint)

                // Название задачи
                var hightDraw = rect.top + titlePaint.textSize + ViewsFunction.TEXT_MARGIN
                var spaceIndex = ViewsFunction.searchSpace(task.title)
                var titleText = task.title.substring(0, spaceIndex).trim()
                val maxTextWidth = columnWidth - 2 * padding - 8f
                titlePaint.color = ViewsFunction.getAdaptiveTextColor(color)
                titlePaint.textSize = minOf(titlePaint.textSize, maxTextWidth / titleText.length.coerceAtLeast(1) * 2.0f)
                canvas.drawText(titleText, rect.left + 4f, hightDraw, titlePaint)
                hightDraw += titlePaint.textSize + ViewsFunction.TEXT_MARGIN

                if(startSlot != endSlot) {
                    // Время задачи – теперь чуть выше, чтобы не слипалось с соседней
                    val timeText = "${ViewsFunction.formatTime(task.start)}–${ViewsFunction.formatTime(task.time_end)} |${task.importance}"
                    timePaintSm.textSize =
                        minOf(28f, maxTextWidth / timeText.length.coerceAtLeast(1) * 1.8f)
                    canvas.drawText(timeText, rect.left + 4f, rect.bottom - 8f, timePaintSm)
                    val posYmax = rect.bottom - 8f - timePaintSm.textSize

                    DrawRect(canvas, xStart, taskBottom, 4f, 40f, Color.BLACK)
                    DrawRect(canvas, xStart, taskBottom, 0f, 40f, if(task.status == "DONE") Color.GREEN else Color.RED)

                    if(!task.is_synced) {
                        DrawRect(canvas, xStart, taskBottom, 4f, 40f, Color.BLACK, 30f)
                        DrawRect(canvas, xStart, taskBottom, 0f, 40f, Color.BLUE, 30f)
                    }

                    if (hightDraw < posYmax && task.title.length > ViewsFunction.CHUNK_SIZE) {
                        titleText = task.title.substring(spaceIndex, ViewsFunction.searchSpace(task.title, spaceIndex)).trim()
                        titlePaint.textSize = minOf(titlePaint.textSize, maxTextWidth / titleText.length.coerceAtLeast(1) * 2.0f)
                        canvas.drawText(titleText, rect.left + 4f, hightDraw, titlePaint)
                        hightDraw += titlePaint.textSize + ViewsFunction.TEXT_MARGIN
                    }

                    if ((hightDraw + ViewsFunction.SEPARATOR_HEIGHT) < posYmax && task.body.isNotEmpty()) {
                        val rect3 = RectF(
                            xStart + padding, hightDraw - titlePaint.textSize,
                            xStart + columnWidth - padding, hightDraw + ViewsFunction.SEPARATOR_HEIGHT  - titlePaint.textSize
                        )
                        taskRectPaint.color = titlePaint.color
                        canvas.drawRoundRect(rect3, 0f, 0f, taskRectPaint)

                        hightDraw += ViewsFunction.SEPARATOR_HEIGHT
                        spaceIndex = ViewsFunction.searchSpace(task.body)
                        titleText = task.body.substring(0, spaceIndex).trim()
                        titlePaint.textSize = minOf(titlePaint.textSize, maxTextWidth / titleText.length.coerceAtLeast(1) * 2.0f)
                        canvas.drawText(titleText, rect.left + 4f, hightDraw, titlePaint)
                        hightDraw += titlePaint.textSize + ViewsFunction.TEXT_MARGIN
                    }

                    if (hightDraw < posYmax && task.body.length > ViewsFunction.CHUNK_SIZE + 1) {
                        titleText = task.body.substring(spaceIndex, ViewsFunction.searchSpace(task.body, spaceIndex)).trim()
                        titlePaint.textSize = minOf(titlePaint.textSize, maxTextWidth / titleText.length.coerceAtLeast(1) * 2.0f)
                        canvas.drawText(titleText, rect.left + 4f, hightDraw, titlePaint)
                    }
                }
            }
        }
    }

    private fun DrawRect(canvas: Canvas, xStart: Float, taskBottom: Float, plus: Float, padding: Float, color: Int, move: Float = 0f) {
        val rect = RectF(
            xStart + columnWidth - padding - move - plus, taskBottom - padding - plus,
            xStart + columnWidth - padding/2 - move + plus, taskBottom - padding/2 + plus
        )

        taskRectPaint.color = color
        canvas.drawRoundRect(rect, ViewsFunction.CORNER_RADIUS, ViewsFunction.CORNER_RADIUS, taskRectPaint)
    }

    private fun getSlotIndexSt(timestamp: Long): Int {
        val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(timestamp - dayStartMillis) + 1
        return ((diffMinutes)/ cellDurationMinutes).toInt() // Теоретически защитит от бага
    }

    private fun getSlotIndexFn(timestamp: Long): Int {
        val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(timestamp - dayStartMillis) - 1
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

    fun startToNear(timeMS: Long, razbMS: Long): Long {
        return (timeMS + razbMS / 2) / razbMS * razbMS
    }

    private fun tasksOverlap(a: DTasks, b: DTasks): Boolean {
        return a.start < b.time_end && b.start < a.time_end
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                downTime = System.currentTimeMillis()
                isLongPressPossible = true
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isLongPressPossible) {
                    val dx = Math.abs(event.x - downX)
                    val dy = Math.abs(event.y - downY)
                    if (dx > touchSlop || dy > touchSlop) {
                        isLongPressPossible = false
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (isLongPressPossible) {
                    val elapsed = System.currentTimeMillis() - downTime
                    if (elapsed >= longPressThreshold) {
                        val x = downX
                        val y = downY

                        for (colIndex in columns.indices) {
                            val xStart = timeColumnWidth + colIndex * columnWidth
                            if (x < xStart || x > xStart + columnWidth) continue
                            for (task in columns[colIndex]) {
                                val startSlot = getSlotIndexSt(task.start)
                                val endSlot = getSlotIndexFn(task.time_end)
                                val taskTop = startSlot * rowHeight
                                val taskBottom = endSlot * rowHeight + rowHeight
                                if (y >= taskTop && y <= taskBottom) {
                                    onTaskClickListener?.onTaskClick(task)
                                    break
                                }
                            }
                        }
                    }
                }
                isLongPressPossible = false
                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }
}
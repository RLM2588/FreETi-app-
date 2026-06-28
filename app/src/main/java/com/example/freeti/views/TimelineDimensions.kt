package com.example.freeti.views

import android.content.Context

object TimelineDimensions {
    var density = 1f

    fun init(context: Context) {
        density = context.resources.displayMetrics.density / 2.3f
    }

    // Размеры в dp
    const val TIME_COLUMN_WIDTH_DP = 120f
    const val ROW_HEIGHT_DP = 60f
    const val COLUMN_WIDTH_DP = 320f // тестить еще нужно
    const val PADDING_MAIN_DP = 10f
    const val PADDING_SMALL_DP = 6f
    const val TEXT_MARGIN_DP = 4f
    const val SEPARATOR_HEIGHT_DP = 2f
    const val CORNER_RADIUS_DP = 8f
    const val TIME_TEXT_SIZE_SP = 14f
    const val TITLE_TEXT_SIZE_SP = 16f

    // Конвертированные значения (px) – кешируем для производительности
    val timeColumnWidthPx: Float get() = TIME_COLUMN_WIDTH_DP * density
    val rowHeightPx: Float get() = ROW_HEIGHT_DP * density
    val columnWidthPx: Float get() = COLUMN_WIDTH_DP * density
    val paddingMainPx: Float get() = PADDING_MAIN_DP * density
    val paddingSmallPx: Float get() = PADDING_SMALL_DP * density
    val textMarginPx: Float get() = TEXT_MARGIN_DP * density
    val separatorHeightPx: Float get() = SEPARATOR_HEIGHT_DP * density
    val cornerRadiusPx: Float get() = CORNER_RADIUS_DP * density
    fun timeTextSizePx(): Float = TIME_TEXT_SIZE_SP * density
    fun titleTextSizePx(): Float = TITLE_TEXT_SIZE_SP * density
}
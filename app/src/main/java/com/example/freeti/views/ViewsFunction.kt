package com.example.freeti.views

import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import kotlin.math.min

class ViewsFunction {
    companion object {
        fun searchSpace(text: String, start: Int = 0): Int {
            val endpos = start + CHUNK_SIZE
            if (text.length < endpos + 1) return text.length
            var spacePos = 0
            val lengthString = min(text.length, endpos)
            if (text[endpos] == ' ') return endpos

            for (i in start..lengthString) {
                if (text[lengthString - i + start] == ' ') {
                    spacePos = lengthString - i + start
                    break
                }
            }
            return if(spacePos != 0  && spacePos - start > CHUNK_SIZE / 4) spacePos else lengthString
        }

        @ColorInt
        fun getAdaptiveTextColor(@ColorInt backgroundColor: Int): Int {
            val r = Color.red(backgroundColor)
            val g = Color.green(backgroundColor)
            val b = Color.blue(backgroundColor)
            val brightness = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0
            return if (brightness < 0.5f) Color.WHITE else Color.BLACK
        }

        fun formatTime(millis: Long): String {
            val cal = java.util.Calendar.getInstance()   // локальный
            cal.timeInMillis = millis
            return String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
        }

        const val CHUNK_SIZE = 18
        const val MAX_TITLE_CHUNKS = 2
        const val MAX_BODY_CHUNKS = 2
        const val PADDING_MAIN = 12f
        const val PADDING_SMALL = 6f
        const val TEXT_MARGIN = 4f
        const val SEPARATOR_HEIGHT = 4f
        const val TIME_TEXT_MAX_SIZE = 28f
        const val CORNER_RADIUS = 14f
    }
}

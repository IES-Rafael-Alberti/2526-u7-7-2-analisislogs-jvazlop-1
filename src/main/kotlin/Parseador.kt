package org.iesra

import java.time.LocalDateTime

class Parseador {

    private val regex = Regex(
        """\[(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})] (INFO|WARNING|ERROR) (.+)"""
    )

    fun parse(line: String): EntradaLog? {
        val match = regex.find(line) ?: return null

        return try {
            val (dateStr, levelStr, message) = match.destructured

            EntradaLog(
                timestamp = LocalDateTime.parse(dateStr.replace(" ", "T")),
                level = LogLevel.valueOf(levelStr),
                message = message
            )
        } catch (e: Exception) {
            null
        }
    }
}
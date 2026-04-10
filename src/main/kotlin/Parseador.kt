package org.iesra

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Parseador {

    private val formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private val regex = Regex(
        """\[(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})] (INFO|WARNING|ERROR) (.+)"""
    )

    fun parse(line: String): EntradaLog? {
        val resultado = regex.find(line) ?: return null

        return try {
            val (fechaStr, levelStr, mensaje) = resultado.destructured

            EntradaLog(
                timestamp = LocalDateTime.parse(fechaStr, formateador),
                level = LogLevel.valueOf(levelStr),
                mensaje = mensaje
            )
        } catch (e: Exception) {
            null
        }
    }
}
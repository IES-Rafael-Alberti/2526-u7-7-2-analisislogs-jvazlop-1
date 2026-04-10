package org.iesra

import java.time.LocalDateTime

data class EntradaLog(
    val timestamp: LocalDateTime,
    val level: LogLevel,
    val mensaje: String
)
package org.iesra

import java.io.File

fun main() {

    val parseador = Parseador()
    val lineas = File("sample_app.log").readLines()

    val logsValidos = mutableListOf<EntradaLog>()
    var invalidas = 0

    for (linea in lineas) {
        val log = parseador.parse(linea)

        if (log != null) {
            logsValidos.add(log)
        } else {
            invalidas++
        }
    }

    println("Total líneas: ${lineas.size}")
    println("Líneas procesadas: ${lineas.size}")
    println("Líneas válidas: ${logsValidos.size}")
    println("Líneas inválidas: $invalidas")

    val info = logsValidos.count { it.level == LogLevel.INFO }
    val warning = logsValidos.count { it.level == LogLevel.WARNING }
    val error = logsValidos.count { it.level == LogLevel.ERROR }

    println("INFO: $info")
    println("WARNINGS: $warning")
    println("ERRORS: $error")

    val primera = logsValidos.minByOrNull { it.timestamp }?.timestamp
    val ultima = logsValidos.maxByOrNull { it.timestamp }?.timestamp

    println("Primera fecha: $primera")
    println("Última fecha: $ultima")
}
package org.iesra

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LogTool : CliktCommand() {

    private val output: String? by option("-o", "--output")

    private val ignoreInvalid: Boolean by option("--ignore-invalid").flag(default = false)

    private val input: String by option("-i", "--input").required()

    private val stdout: Boolean by option("-p", "--stdout").flag(default = false)

    private val stats: Boolean by option("-s", "--stats").flag(default = false)

    private val report: Boolean by option("-r", "--report").flag(default = false)

    private val level: String? by option("-l", "--level")

    private val from: String? by option("--from")

    private val to: String? by option("--to")

    override fun run() {

        val file = File(input)

        if (!file.exists()) {
            echo("El fichero no existe")
            return
        }

        if (!stdout && output == null) {
            echo("Debes indicar --stdout o --output")
            return
        }

        if (stats && report) {
            echo("No puedes usar --stats y --report a la vez")
            return
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val fromDate = try {
            from?.let { LocalDateTime.parse(it, formatter) }
        } catch (e: Exception) {
            echo("Formato de --from incorrecto")
            return
        }

        val toDate = try {
            to?.let { LocalDateTime.parse(it, formatter) }
        } catch (e: Exception) {
            echo("Formato de --to incorrecto")
            return
        }

        val levelFilter = try {
            level?.split(",")?.map { LogLevel.valueOf(it.trim().uppercase()) }
        } catch (e: Exception) {
            echo("Nivel invalido")
            return
        }

        val parseador = Parseador()
        val lineas = file.readLines()

        val logsValidos = mutableListOf<EntradaLog>()
        var invalidas = 0

        for (linea in lineas) {
            val log = parseador.parse(linea)
            if (log != null) {
                logsValidos.add(log)
            } else {
                if (!ignoreInvalid) {
                    echo("Error: Línea mal formada encontrada. Use --ignore-invalid para omitirla.")
                    return
                }
                invalidas++
            }
        }

        val logsFiltrados = logsValidos.filter { log ->
            val cumpleLevel = levelFilter?.let { log.level in it } ?: true
            val cumpleFrom = fromDate?.let { log.timestamp >= it } ?: true
            val cumpleTo = toDate?.let { log.timestamp <= it } ?: true
            cumpleLevel && cumpleFrom && cumpleTo
        }

        val info = logsFiltrados.count { it.level == LogLevel.INFO }
        val warning = logsFiltrados.count { it.level == LogLevel.WARNING }
        val error = logsFiltrados.count { it.level == LogLevel.ERROR }

        val primera = logsFiltrados.minByOrNull { it.timestamp }?.timestamp
        val ultima = logsFiltrados.maxByOrNull { it.timestamp }?.timestamp

        val statsTexto = """
ESTADISTICAS DE LOGS
====================
Fichero: $input

Total lineas: ${lineas.size}
Lineas validas: ${logsFiltrados.size}
Lineas invalidas: $invalidas

INFO: $info
WARNINGS: $warning
ERRORS: $error

Primera fecha: ${primera?.format(formatter) ?: ""}
Ultima fecha: ${ultima?.format(formatter) ?: ""}""".trimIndent()

        val logsTexto = logsFiltrados.joinToString("\n") {
            "[${it.timestamp.format(formatter)}] ${it.level} ${it.mensaje}"
        }

        val reportTexto = """
INFORME DE LOGS
===============

$statsTexto

Entradas:
$logsTexto""".trimIndent()

        val finalStats = stats
        val finalReport = report || (!stats && !report)

        val salida = if (finalStats) statsTexto else reportTexto

        try {
            output?.let {
                File(it).writeText(salida)
            }
        } catch (e: Exception) {
            echo("Error: No se pudo escribir en el fichero de salida.")
            return
        }

        if (stdout) {
            echo(salida)
        }
    }
}
package org.iesra

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import java.io.File

class LogTool : CliktCommand() {

    private val input: String by option("-i", "--input").required()
    private val stdout: Boolean by option("-p", "--stdout").flag(default = false)

    override fun run() {

        val parseador = Parseador()
        val file = File(input)

        if (!file.exists()) {
            echo("El fichero no existe")
            return
        }

        val lineas = file.readLines()

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

        val info = logsValidos.count { it.level == LogLevel.INFO }
        val warning = logsValidos.count { it.level == LogLevel.WARNING }
        val error = logsValidos.count { it.level == LogLevel.ERROR }

        val primera = logsValidos.minByOrNull { it.timestamp }?.timestamp
        val ultima = logsValidos.maxByOrNull { it.timestamp }?.timestamp

        val resultado = """
            Total líneas: ${lineas.size}
            Líneas válidas: ${logsValidos.size}
            Líneas inválidas: $invalidas

            INFO: $info
            WARNINGS: $warning
            ERRORS: $error

            Primera fecha: $primera
            Última fecha: $ultima
        """.trimIndent()

        if (stdout) {
            echo(resultado)
        }
    }
}
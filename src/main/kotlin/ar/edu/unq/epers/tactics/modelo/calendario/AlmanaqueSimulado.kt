package ar.edu.unq.epers.tactics.modelo.calendario

import java.time.LocalDateTime

class AlmanaqueSimulado(private var fecha: LocalDateTime): Almanaque {

    override fun ahora(): LocalDateTime = fecha

    override fun haceUnaSemana(): LocalDateTime = fecha.minusDays(7)

    fun cambiarFechaActual(nuevaFecha: LocalDateTime) {
        fecha = nuevaFecha
    }
}

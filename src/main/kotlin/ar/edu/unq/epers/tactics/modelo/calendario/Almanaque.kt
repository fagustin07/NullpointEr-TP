package ar.edu.unq.epers.tactics.modelo.calendario

import java.time.LocalDateTime

interface Almanaque {

    fun ahora(): LocalDateTime

    fun cambiarFechaActual(nuevaFecha: LocalDateTime)

    fun haceUnaSemana(): LocalDateTime
}

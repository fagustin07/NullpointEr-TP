package ar.edu.unq.epers.tactics.modelo.calendario

import java.time.LocalDateTime

interface ProveedorDeFechas {

    fun ahora(): LocalDateTime

    fun cambiarFechaActual(nuevaFecha: LocalDateTime)

    fun haceUnaSemana(): LocalDateTime
}

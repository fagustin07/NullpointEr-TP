package ar.edu.unq.epers.tactics.modelo.calendario

import java.time.LocalDate

interface ProveedorDeFechas {

    fun ahora(): LocalDate

    fun cambiarFechaActual(nuevaFecha: LocalDate)

    fun haceUnaSemana(): LocalDate
}

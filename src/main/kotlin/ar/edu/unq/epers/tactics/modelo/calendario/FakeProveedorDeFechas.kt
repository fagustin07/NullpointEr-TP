package ar.edu.unq.epers.tactics.modelo.calendario

import java.time.LocalDateTime

class FakeProveedorDeFechas(private var fecha: LocalDateTime): ProveedorDeFechas {

    override fun ahora(): LocalDateTime = fecha

    override fun cambiarFechaActual(nuevaFecha: LocalDateTime) {
        fecha = nuevaFecha
    }

    override fun haceUnaSemana(): LocalDateTime = fecha.minusDays(7)
}

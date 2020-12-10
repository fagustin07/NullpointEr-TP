package ar.edu.unq.epers.tactics.modelo.calendario

import java.time.LocalDateTime

class ProveedorDeFechasReal : ProveedorDeFechas {
    override fun ahora(): LocalDateTime = LocalDateTime.now()

    override fun cambiarFechaActual(nuevaFecha: LocalDateTime) {}

    override fun haceUnaSemana(): LocalDateTime = LocalDateTime.now().minusDays(7)

}

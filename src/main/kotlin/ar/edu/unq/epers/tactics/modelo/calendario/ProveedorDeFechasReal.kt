package ar.edu.unq.epers.tactics.modelo.calendario

import java.time.LocalDate

class ProveedorDeFechasReal : ProveedorDeFechas {
    override fun ahora() = LocalDate.now()

    override fun cambiarFechaActual(nuevaFecha: LocalDate) { }

    override fun haceUnaSemana() = LocalDate.now().minusDays(7)

}

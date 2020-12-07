package ar.edu.unq.epers.tactics.modelo.calendario

import java.time.LocalDate

class FakeProveedorDeFechas(private var fecha: LocalDate): ProveedorDeFechas {

    override fun ahora() = fecha

    override fun cambiarFechaActual(nuevaFecha: LocalDate) {
        fecha = nuevaFecha
    }

    override fun haceUnaSemana() = fecha.minusDays(7)
}

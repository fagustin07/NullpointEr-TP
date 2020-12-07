package ar.edu.unq.epers.tactics.modelo.calendario

import java.time.LocalDate

class FakeProveedorDeFechas(private var fecha: LocalDate): ProveedorDeFechas {

    override fun ahora() = fecha

    override fun cambiarFecha(nuevaFecha: LocalDate) {
        fecha = nuevaFecha
    }

}

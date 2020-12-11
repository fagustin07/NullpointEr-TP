package ar.edu.unq.epers.tactics.modelo.calendario

import java.time.LocalDateTime

class AlmanaqueSimulado(private var fechaDeHoy: LocalDateTime): Almanaque {

    override fun fechaDeHoy(): LocalDateTime = fechaDeHoy

    override fun haceUnaSemana(): LocalDateTime = fechaDeHoy.minusDays(7)

    fun simularElPasoDeDias(cantidadDeDiasAAvanzar: Long) {
        fechaDeHoy = fechaDeHoy.plusDays(cantidadDeDiasAAvanzar)
    }
}

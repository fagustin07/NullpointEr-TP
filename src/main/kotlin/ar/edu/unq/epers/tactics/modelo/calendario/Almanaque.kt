package ar.edu.unq.epers.tactics.modelo.calendario

import java.time.LocalDateTime

interface Almanaque {

    fun fechaDeHoy(): LocalDateTime

    fun haceUnaSemana(): LocalDateTime
}

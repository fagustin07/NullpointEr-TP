package ar.edu.unq.epers.tactics.modelo.calendario

import java.time.LocalDateTime

class AlmanaqueReal : Almanaque {

    override fun ahora(): LocalDateTime = LocalDateTime.now()

    override fun haceUnaSemana(): LocalDateTime = LocalDateTime.now().minusDays(7)

}

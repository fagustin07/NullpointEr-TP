package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HabilidadNulaTest {

    @Test
    fun `no altera el estado del aventurero`() {
        val aventurero = Aventurero("Pepe")
        val manaInicial = aventurero.mana()

        HabilidadNula.para(aventurero, aventurero).resolversePara(aventurero)

        assertEquals(manaInicial, aventurero.mana())
        assertEquals(0.0, aventurero.dañoRecibido())
    }
}
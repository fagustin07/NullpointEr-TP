package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class MeditacionTest {

    @Test
    fun `cuando un aventurero medita regenera mana igual a su nivel`() {
        val party = Party("Los Solitarios","URL")
        val aventurero = Aventurero("Pepe")
        val manaInicial = aventurero.mana()

        val meditacion = Meditacion.para(aventurero, aventurero)
        meditacion.resolversePara(aventurero)

        assertThat(aventurero.mana()).isEqualTo(manaInicial + aventurero.nivel())
    }

    @Test
    fun `la habilidad meditar debe tener el mismo emisor y receptor`() {
        val party = Party("Los Solitarios","URL")
        val aventureroEmisor = Aventurero("Pepe")
        val aventureroReceptor = Aventurero("Jorge")

        assertThatThrownBy { Meditacion.para(aventureroEmisor, aventureroReceptor) }
                .hasMessage(Meditacion.MENSAJE_AVENTUREROS_DISTINTOS)

    }
}
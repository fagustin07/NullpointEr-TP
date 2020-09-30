package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CuracionTest {

    private lateinit var aventureroCurado: Aventurero
    private lateinit var aventureroCurador: Aventurero
    private lateinit var party: Party

    @BeforeEach
    internal fun setUp() {
        party = Party("Los Fabulosos")
        aventureroCurador =Aventurero(party, "Raul", 1, 1, 10, 1)
        aventureroCurado = Aventurero(party, "Sergio")
    }

    @Test
    fun `cuando un aventurero es curado, su vida aumenta una cantidad igual al poder magico del curador`() {
        val vidaAntesDeCurar = aventureroCurado.vida()
        val poderMagicoAntesDeCurar = aventureroCurador.poderMagico()

        val curacion = Curacion.para(aventureroCurador, aventureroCurado)
        curacion.resolverse()

        assertThat(aventureroCurado.vida()).isEqualTo(vidaAntesDeCurar + poderMagicoAntesDeCurar)
    }

    @Test
    fun `cuando un aventurero cura a otro, pierde 5 puntos de mana`() {
        val manaAntesDeCurar = aventureroCurador.mana()

        val curacion = Curacion.para(aventureroCurador, aventureroCurado)
        curacion.resolverse()

        assertThat(aventureroCurador.mana()).isEqualTo(manaAntesDeCurar - 5)
    }
}
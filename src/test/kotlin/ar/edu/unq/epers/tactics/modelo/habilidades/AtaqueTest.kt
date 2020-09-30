package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AtaqueTest {

    private lateinit var aventureroReceptor: Aventurero
    private lateinit var aventureroEmisor: Aventurero
    private lateinit var party: Party

    @BeforeEach
    internal fun setUp() {
        party = Party("Party")
        aventureroEmisor = Aventurero(party, "Pepe", 10, 0, 0, 0)
        aventureroReceptor = Aventurero(party, "Jorge", 25, 0, 0, 20)
    }

    @Test
    fun `un ataque con tirada mayor a la armadura mas la mitad de la velocidad del receptor es exitoso`() {
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        val dadoDe20Falso = DadoDe20(10)
        val ataque = Ataque.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataque.resolverse()

        Assertions.assertThat(aventureroReceptor.vida()).isLessThan(vidaAntesDelAtaque)
    }

    @Test
    fun `un ataque con tirada menor a la armadura mas la mitad de la velocidad del receptor falla`() {
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        val dadoDe20Falso = DadoDe20(1)
        val ataque = Ataque.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataque.resolverse()

        Assertions.assertThat(aventureroReceptor.vida()).isEqualTo(vidaAntesDelAtaque)
    }

    @Test
    fun `un ataque exitoso le resta al receptor una cantidad de vida igual al daño fisico del emisor`() {
        val dadoDe20Falso = DadoDe20(20)
        val ataque = Ataque.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        ataque.resolverse()

        Assertions.assertThat(aventureroReceptor.vida()).isEqualTo(vidaAntesDelAtaque - aventureroEmisor.dañoFisico())
    }

}
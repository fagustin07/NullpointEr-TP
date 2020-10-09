package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.dado.DadoSimulado
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AtaqueTest {

    private lateinit var aventureroReceptor: Aventurero
    private lateinit var aventureroEmisor: Aventurero
    private lateinit var party: Party

    @BeforeEach
    internal fun setUp() {
        party = Party("Party","URL")
        aventureroEmisor = Aventurero("Pepe","", 10)
        aventureroReceptor = Aventurero("Jorge","", 25, constitucion = 20)
    }

    @Test
    fun `un ataque con tirada mayor a la armadura mas la mitad de la velocidad del receptor es exitoso`() {
        val vidaAntesDelAtaque = aventureroReceptor.vidaActual()

        val dadoDe20Falso = DadoSimulado(10)
        val ataque = Ataque.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataque.resolversePara(aventureroReceptor)

        Assertions.assertThat(aventureroReceptor.vidaActual()).isLessThan(vidaAntesDelAtaque)
    }

    @Test
    fun `un ataque con tirada menor a la armadura mas la mitad de la velocidad del receptor falla`() {
        val vidaAntesDelAtaque = aventureroReceptor.vidaActual()

        val dadoDe20Falso = DadoSimulado(1)
        val ataque = Ataque.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataque.resolversePara(aventureroReceptor)

        Assertions.assertThat(aventureroReceptor.vidaActual()).isEqualTo(vidaAntesDelAtaque)
    }

    @Test
    fun `un ataque exitoso le resta al receptor una cantidad de vida igual al daño fisico del emisor`() {
        val dadoDe20Falso = DadoSimulado(20)
        val ataque = Ataque.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        val vidaAntesDelAtaque = aventureroReceptor.vidaActual()

        ataque.resolversePara(aventureroReceptor)

        Assertions.assertThat(aventureroReceptor.vidaActual()).isEqualTo(vidaAntesDelAtaque - aventureroEmisor.dañoFisico())
    }

}
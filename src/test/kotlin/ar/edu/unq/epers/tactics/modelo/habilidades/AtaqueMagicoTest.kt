package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.dado.DadoSimulado
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AtaqueMagicoTest {

    private lateinit var aventureroEmisor: Aventurero
    private lateinit var aventureroReceptor: Aventurero
    private lateinit var party: Party

    @BeforeEach
    internal fun setUp() {
        party = Party("Los Increibles", "URL")
        aventureroReceptor = Aventurero("Belen", "", 1, 6, 50, 10)
        aventureroEmisor = Aventurero("Coco", inteligencia = 5)
    }

    @Test
    fun `un ataque magico con tirada mayor a la la mitad de la velocidad del receptor es exitoso`() {
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        val dadoDe20Falso = DadoSimulado(20)
        val ataqueMagico = AtaqueMagico.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataqueMagico.resolversePara(aventureroReceptor)

        Assertions.assertThat(aventureroReceptor.vida()).isLessThan(vidaAntesDelAtaque)
    }

    @Test
    fun `un ataque con tirada menor a la mitad de la velocidad del receptor falla`() {
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        val dadoDe20Falso = DadoSimulado(1)
        val ataqueMagico = AtaqueMagico.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataqueMagico.resolversePara(aventureroReceptor)

        Assertions.assertThat(aventureroReceptor.vida()).isEqualTo(vidaAntesDelAtaque)
    }

    @Test
    fun `un ataque exitoso le resta al receptor una cantidad de vida igual al poder magico del emisor`() {
        val dadoDe20Falso = DadoSimulado(20)
        val poderMagicoDeAtaque = aventureroEmisor.poderMagico()
        val vidaAntesDeAtaque = aventureroReceptor.vida()
        val ataqueMagico = AtaqueMagico.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)

        ataqueMagico.resolversePara(aventureroReceptor)

        Assertions.assertThat(aventureroReceptor.vida()).isEqualTo(vidaAntesDeAtaque - poderMagicoDeAtaque)
    }
}
package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AtaqueMagicoTest {

    private lateinit var aventureroEmisor: Aventurero
    private lateinit var aventureroReceptor: Aventurero
    private lateinit var party: Party

    @BeforeEach
    internal fun setUp() {
        party = Party("Los Increibles")
        aventureroReceptor = Aventurero(party, "Belen", 0, 6, 0, 0)
        aventureroEmisor = Aventurero(party, "Coco")
    }

    @Test
    fun `un ataque magico con tirada mayor a la la mitad de la velocidad del receptor es exitoso`() {
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        val dadoDe20Falso = DadoDe20(20)
        val ataqueMagico = AtaqueMagico.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataqueMagico.resolverse()

        Assertions.assertThat(aventureroReceptor.vida()).isLessThan(vidaAntesDelAtaque)
    }

    @Test
    fun `un ataque con tirada menor a la mitad de la velocidad del receptor falla`(){
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        val dadoDe20Falso = DadoDe20(1)
        val ataqueMagico = AtaqueMagico.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataqueMagico.resolverse()

        Assertions.assertThat(aventureroReceptor.vida()).isEqualTo(vidaAntesDelAtaque)
    }

    @Test
    fun `un ataque exitoso le resta al receptor una cantidad de vida igual al poder magico del emisor`() {
        val poderMagicoDelEmisor = aventureroEmisor.poderMagico()
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        val dadoDe20Falso = DadoDe20(20)
        val ataqueMagico = AtaqueMagico.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataqueMagico.resolverse()

        Assertions.assertThat(aventureroReceptor.vida()).isEqualTo(vidaAntesDelAtaque - poderMagicoDelEmisor)
    }
}
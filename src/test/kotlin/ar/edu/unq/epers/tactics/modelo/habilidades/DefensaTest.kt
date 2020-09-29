package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DefensaTest{

    private lateinit var aventureroDefendido: Aventurero
    private lateinit var aventureroDefensor: Aventurero
    private lateinit var party: Party

    @BeforeEach
    internal fun setUp() {
        party = Party("Party")
        aventureroDefensor = Aventurero(party, "Pepe", 10, 0, 0, 0)
        aventureroDefendido = Aventurero(party, "Jorge", 21, 0, 0, 20)
    }

    @Test
    fun `cuando un aventurero defiende sufre la mitad de daño que recibe al que esta defendiendo`(){
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoDe20(20)
        val vidaAntesDelAtaque = aventureroDefensor.vida()

        defensa.resolverse()
        val ataque = Ataque.para(aventureroDefensor, aventureroDefendido, dadoDe20Falso)
        ataque.resolverse()

        val vidaEsperadaDeDefensor = vidaAntesDelAtaque - (aventureroDefensor.dañoFisico() / 2)
        assertThat(aventureroDefensor.vida()).isEqualTo(vidaEsperadaDeDefensor)
    }

    @Test
    fun `cuando un aventurero es defendido, no sufre daño`(){
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoDe20(20)
        val vidaDelDefendidoAntesDelAtaque = aventureroDefendido.vida()

        defensa.resolverse()
        val ataque = Ataque.para(aventureroDefensor, aventureroDefendido, dadoDe20Falso)
        ataque.resolverse()

        assertThat(aventureroDefendido.vida()).isEqualTo(vidaDelDefendidoAntesDelAtaque)
    }

    @Test
    fun `luego de ser defendido por tres turnos, el aventurero se queda sin defensor y sufre todo el daño`(){
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoDe20(20)
        val vidaDelDefendidoAntesDelAtaque = aventureroDefendido.vida()

        defensa.resolverse()
        val ataque = Ataque.para(aventureroDefensor, aventureroDefendido, dadoDe20Falso)
        repeat(3) { ataque.resolverse() }

        ataque.resolverse()

        val vidaEsperadaDeDefendido = vidaDelDefendidoAntesDelAtaque - aventureroDefensor.dañoFisico()
        assertThat(aventureroDefendido.vida()).isEqualTo(vidaEsperadaDeDefendido)

    }

    @Test
    fun `si el aventurero defensor de otro muere, el defendido pierde a su defensor y recibe todo el daño del ataque`() {
        val party = Party("Party")
        val aventureroAtacante = Aventurero(party, "Destructor", 80, 0, 0, 0)
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoDe20(20)

        val ataque = Ataque.para(aventureroAtacante, aventureroDefendido, dadoDe20Falso)
        defensa.resolverse()
        ataque.resolverse()

        ataque.resolverse()

        assertThat(aventureroDefendido.vida()).isEqualTo(0)

    }
}


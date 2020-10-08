package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.dado.DadoSimulado
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.RuntimeException

internal class DefensaTest{

    private lateinit var aventureroDefendido: Aventurero
    private lateinit var aventureroDefensor: Aventurero
    private lateinit var party: Party

    @BeforeEach
    internal fun setUp() {
        party = Party("Party","URL")
        aventureroDefensor = Aventurero("Pepe","", 10)
        aventureroDefendido = Aventurero("Jorge","", 21, constitucion = 20)
        party.agregarUnAventurero(aventureroDefensor)
        party.agregarUnAventurero(aventureroDefendido)
    }

    @Test
    fun `cuando un aventurero defiende sufre la mitad de daño que recibe al que esta defendiendo`(){
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoSimulado(20)
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
        val dadoDe20Falso = DadoSimulado(20)
        val vidaDelDefendidoAntesDelAtaque = aventureroDefendido.vida()

        defensa.resolverse()
        val ataque = Ataque.para(aventureroDefensor, aventureroDefendido, dadoDe20Falso)
        ataque.resolverse()

        assertThat(aventureroDefendido.vida()).isEqualTo(vidaDelDefendidoAntesDelAtaque)
    }

    @Test
    fun `luego de ser defendido por tres turnos, el aventurero se queda sin defensor y sufre todo el daño`(){
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoSimulado(20)
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
        val aventureroAtacante = Aventurero("Destructor","", 80)
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoSimulado(20)

        val ataque = Ataque.para(aventureroAtacante, aventureroDefendido, dadoDe20Falso)
        defensa.resolverse()
        ataque.resolverse()

        ataque.resolverse()

        assertThat(aventureroDefendido.vida()).isEqualTo(0)
    }

    @Test
    fun `un aventurero solo puede defender a un compañero a la vez`() {
        val aventureroAtacante = Aventurero("Destructor", "imagen", 30)
        val aventureroCompañero = Aventurero("Jorge", "imagen", 21, constitucion = 20)
        val dadoDe20Falso = DadoSimulado(20)
        val vidaAntesDelAtaque = aventureroCompañero.vida()
        val ataque = Ataque.para(aventureroAtacante, aventureroDefendido, dadoDe20Falso)
        val ataque2 = Ataque.para(aventureroAtacante, aventureroCompañero, dadoDe20Falso)
        val vidaAntesDeAtaqueAventureroDefendido = aventureroDefendido.vida()
        party.agregarUnAventurero(aventureroCompañero)

        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val defensa2 = Defensa.para(aventureroDefensor, aventureroCompañero)

        defensa.resolverse()
        defensa2.resolverse()
        ataque.resolverse()

        ataque2.resolverse()

        val vidaDespuesDeAtaque = vidaAntesDeAtaqueAventureroDefendido - aventureroAtacante.dañoFisico()
        assertThat(aventureroDefendido.vida()).isEqualTo(vidaDespuesDeAtaque)
        assertThat(aventureroCompañero.vida()).isEqualTo(vidaAntesDelAtaque)
    }

    @Test
    fun `un aventurero no puede defender a un enemigo`(){
        val otroAventurero = Aventurero("semantic-sugar")
        Party("Leones", "lyon.jpg").agregarUnAventurero(otroAventurero)

        val exception = assertThrows<RuntimeException>{ Defensa.para(aventureroDefensor, otroAventurero)}
        assertThat(exception.message).isEqualTo("${aventureroDefensor.nombre()} no puede defender a un enemigo!")
    }

    @Test
    fun `un aventurero no puede defenderse a si mismo`(){
        val exception = assertThrows<RuntimeException>{ Defensa.para(aventureroDefensor, aventureroDefensor)}
        assertThat(exception.message).isEqualTo("${aventureroDefensor.nombre()} no puede defenderse a si mismo!")
    }
}


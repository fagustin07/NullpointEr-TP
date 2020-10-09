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
        val vidaAntesDelAtaque = aventureroDefensor.vidaActual()

        defensa.resolversePara(aventureroDefendido)
        val ataque = Ataque.para(aventureroDefensor, aventureroDefendido, dadoDe20Falso)
        ataque.resolversePara(aventureroDefendido)

        val vidaEsperadaDeDefensor = vidaAntesDelAtaque - (aventureroDefensor.dañoFisico() / 2)
        assertThat(aventureroDefensor.vidaActual()).isEqualTo(vidaEsperadaDeDefensor)
    }

    @Test
    fun `cuando un aventurero es defendido, no sufre daño`(){
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoSimulado(20)
        val vidaDelDefendidoAntesDelAtaque = aventureroDefendido.vidaActual()

        defensa.resolversePara(aventureroDefendido)
        val ataque = Ataque.para(aventureroDefensor, aventureroDefendido, dadoDe20Falso)
        ataque.resolversePara(aventureroDefendido)

        assertThat(aventureroDefendido.vidaActual()).isEqualTo(vidaDelDefendidoAntesDelAtaque)
    }

    @Test
    fun `luego de ser defendido por tres turnos, el aventurero se queda sin defensor y sufre todo el daño`(){
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoSimulado(20)
        val vidaDelDefendidoAntesDelAtaque = aventureroDefendido.vidaActual()

        defensa.resolversePara(aventureroDefendido)
        val ataque = Ataque.para(aventureroDefensor, aventureroDefendido, dadoDe20Falso)
        repeat(3) { ataque.resolversePara(aventureroDefendido) }

        ataque.resolversePara(aventureroDefendido)

        val vidaEsperadaDeDefendido = vidaDelDefendidoAntesDelAtaque - aventureroDefensor.dañoFisico()
        assertThat(aventureroDefendido.vidaActual()).isEqualTo(vidaEsperadaDeDefendido)

    }

    @Test
    fun `si el aventurero defensor de otro muere, el defendido pierde a su defensor y recibe todo el daño del ataque`() {
        val aventureroAtacante = Aventurero("Destructor","", 80)
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoSimulado(20)

        val ataque = Ataque.para(aventureroAtacante, aventureroDefendido, dadoDe20Falso)
        defensa.resolversePara(aventureroDefendido)
        ataque.resolversePara(aventureroDefendido)

        ataque.resolversePara(aventureroDefendido)

        assertThat(aventureroDefendido.vidaActual()).isEqualTo(0)
    }

    @Test
    fun `cuando un aventurero que estaba defendiendo a un aliado quiere defender a otro, el primero se queda sin defensor y recibe todo el daño`() {
        val dadoDe20Falso = DadoSimulado(20)

        val defensor = Aventurero("Defensor")
        val aventureroSinDefensor = Aventurero("Sin Defensor")
        val otroAliado = Aventurero("Defendido")
        val enemigo = Aventurero("Enemigo")

        party.agregarUnAventurero(defensor)
        party.agregarUnAventurero(aventureroSinDefensor)
        party.agregarUnAventurero(otroAliado)

        Defensa.para(defensor, aventureroSinDefensor).resolversePara(aventureroSinDefensor)
        Defensa.para(defensor, otroAliado).resolversePara(otroAliado)

        val vidaEsperadaDespuesDelAtaque = aventureroSinDefensor.vidaActual() - enemigo.dañoFisico()
        Ataque.para(enemigo, aventureroSinDefensor, dadoDe20Falso).resolversePara(aventureroSinDefensor)

        assertThat(aventureroSinDefensor.vidaActual()).isEqualTo(vidaEsperadaDespuesDelAtaque)
    }

    @Test
    fun `cuando un aventurero que estaba defendiendo a un aliado quiere defender a otro, el nuevo defendido no recibe daño`() {
        val dadoDe20Falso = DadoSimulado(20)

        val defensor = Aventurero("Defensor")
        val aventureroSinDefensor = Aventurero("Sin Defensor")
        val defendido = Aventurero("Defendido")
        val enemigo = Aventurero("Enemigo")

        party.agregarUnAventurero(defensor)
        party.agregarUnAventurero(aventureroSinDefensor)
        party.agregarUnAventurero(defendido)

        Defensa.para(defensor, aventureroSinDefensor).resolversePara(aventureroSinDefensor)
        Defensa.para(defensor, defendido).resolversePara(defendido)

        val vidaDelDefendidoAntesDelAtaque = defendido.vidaActual()
        Ataque.para(enemigo, aventureroSinDefensor, dadoDe20Falso).resolversePara(aventureroSinDefensor)

        assertThat(defendido.vidaActual()).isEqualTo(vidaDelDefendidoAntesDelAtaque)
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


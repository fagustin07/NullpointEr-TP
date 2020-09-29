package ar.edu.unq.epers.tactics.modelo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class HabilidadTest{

    @Test
    fun `un ataque con tirada mayor a la armadura mas la mitad de la velocidad del receptor es exitoso`() {
        val party = Party("Party")
        val aventureroEmisor = Aventurero(party, "Pepe", 10, 0, 0, 0, 10, 10)
        val aventureroReceptor = Aventurero(party, "Jorge", 25, 0, 0, 10, 0, 10)
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        val dadoDe20Falso = DadoDe20(10)
        val ataque = Ataque.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataque.resolverse()

        assertThat(aventureroReceptor.vida()).isLessThan(vidaAntesDelAtaque)
    }

    @Test
    fun `un ataque con tirada menor a la armadura mas la mitad de la velocidad del receptor falla`() {
        val party = Party("Party")
        val aventureroEmisor = Aventurero(party, "Pepe", 10, 0, 0, 0, 10, 10)
        val aventureroReceptor = Aventurero(party, "Jorge", 25, 0, 0, 70, 0, 10)
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        val dadoDe20Falso = DadoDe20(1)
        val ataque = Ataque.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataque.resolverse()

        assertThat(aventureroReceptor.vida()).isEqualTo(vidaAntesDelAtaque)
    }

    @Test
    fun `un ataque exitoso le resta al receptor una cantidad de vida igual al daño fisico del emisor`(){
        val party = Party("Party")
        val aventureroEmisor = Aventurero(party, "Pepe", 10, 0, 0, 0, 10, 10)
        val aventureroReceptor = Aventurero(party, "Jorge", 21, 0, 0, 20, 0, 10)
        val dadoDe20Falso = DadoDe20(20)
        val ataque = Ataque.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        ataque.resolverse()

        assertThat(aventureroReceptor.vida()).isEqualTo(vidaAntesDelAtaque - aventureroEmisor.danioFisico())
    }

    @Test
    fun `cuando un aventurero defiende sufre la mitad de daño`(){
        val party = Party("Party")
        val aventureroDefensor = Aventurero(party, "Pepe", 10, 0, 0, 0, 10, 10)
        val aventureroDefendido = Aventurero(party, "Jorge", 21, 0, 0, 20, 0, 10)
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoDe20(20)
        val vidaAntesDelAtaque = aventureroDefensor.vida()

        defensa.resolverse()
        val ataque = Ataque.para(aventureroDefendido, aventureroDefensor, dadoDe20Falso)
        ataque.resolverse()

        assertThat(aventureroDefensor.vida()).isEqualTo(vidaAntesDelAtaque - aventureroDefendido.danioFisico() / 2)
    }

    @Test
    fun `cuando un aventurero es defendido sufre la mitad de daño`(){
        val party = Party("Party")
        val aventureroDefensor = Aventurero(party, "Pepe", 10, 0, 0, 0, 10, 10)
        val aventureroDefendido = Aventurero(party, "Jorge", 21, 0, 0, 20, 0, 10)
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoDe20(20)
        val vidaAntesDelAtaque = aventureroDefendido.vida()

        defensa.resolverse()
        val ataque = Ataque.para(aventureroDefensor, aventureroDefendido, dadoDe20Falso)
        ataque.resolverse()

        assertThat(aventureroDefendido.vida()).isEqualTo(vidaAntesDelAtaque - aventureroDefensor.danioFisico() / 2)
    }
}


package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class HabilidadTest{

    @Test
    fun `un ataque con tirada mayor a la armadura mas la mitad de la velocidad del receptor es exitoso`() {
        val party = Party("Party")
        val aventureroEmisor = Aventurero(party, "Pepe", 10, 0, 0, 0)
        val aventureroReceptor = Aventurero(party, "Jorge", 25, 0, 0, 10)
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        val dadoDe20Falso = DadoDe20(10)
        val ataque = Ataque.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataque.resolverse()

        assertThat(aventureroReceptor.vida()).isLessThan(vidaAntesDelAtaque)
    }

    @Test
    fun `un ataque con tirada menor a la armadura mas la mitad de la velocidad del receptor falla`() {
        val party = Party("Party")
        val aventureroEmisor = Aventurero(party, "Pepe", 10, 0, 0, 0)
        val aventureroReceptor = Aventurero(party, "Jorge", 25, 0, 0, 70)
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        val dadoDe20Falso = DadoDe20(1)
        val ataque = Ataque.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        ataque.resolverse()

        assertThat(aventureroReceptor.vida()).isEqualTo(vidaAntesDelAtaque)
    }

    @Test
    fun `un ataque exitoso le resta al receptor una cantidad de vida igual al daño fisico del emisor`(){
        val party = Party("Party")
        val aventureroEmisor = Aventurero(party, "Pepe", 10, 0, 0, 0)
        val aventureroReceptor = Aventurero(party, "Jorge", 21, 0, 0, 20)
        val dadoDe20Falso = DadoDe20(20)
        val ataque = Ataque.para(aventureroEmisor, aventureroReceptor, dadoDe20Falso)
        val vidaAntesDelAtaque = aventureroReceptor.vida()

        ataque.resolverse()

        assertThat(aventureroReceptor.vida()).isEqualTo(vidaAntesDelAtaque - aventureroEmisor.dañoFisico())
    }

    @Test
    fun `cuando un aventurero defiende sufre la mitad de daño que recibe al que esta defendiendo`(){
        val party = Party("Party")
        val aventureroDefensor = Aventurero(party, "Pepe", 10, 0, 0, 0)
        val aventureroDefendido = Aventurero(party, "Jorge", 21, 0, 0, 20)
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
        val party = Party("Party")
        val aventureroDefensor = Aventurero(party, "Pepe", 10, 0, 0, 0)
        val aventureroDefendido = Aventurero(party, "Jorge", 21, 0, 0, 20)
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
        val party = Party("Party")
        val aventureroDefensor = Aventurero(party, "Pepe", 5, 0, 0, 0)
        val aventureroDefendido = Aventurero(party, "Jorge", 21, 0, 0, 20)
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
        val aventureroDefensor = Aventurero(party, "Pepe", 5, 0, 0, 0)
        val aventureroAtacante = Aventurero(party, "Destructor", 80, 0, 0, 0)
        val aventureroDefendido = Aventurero(party, "Jorge", 21, 0, 0, 20)
        val defensa = Defensa.para(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoDe20(20)

        val ataque = Ataque.para(aventureroAtacante, aventureroDefendido, dadoDe20Falso)
        defensa.resolverse()
        ataque.resolverse()

        ataque.resolverse()

        assertThat(aventureroDefendido.vida()).isEqualTo(0)

    }
}


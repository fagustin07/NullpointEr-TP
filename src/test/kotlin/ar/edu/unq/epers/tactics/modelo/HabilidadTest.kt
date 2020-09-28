package ar.edu.unq.epers.tactics.modelo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class HabilidadTest{

    @Test
    fun `un ataque con tirada mayor a la armadura mas la mitad de la velocidad del receptor es exitoso`() {
        val party = Party("Party")
        val aventureroEmisor = Aventurero(party, "Pepe", 10, 0, 0, 0, 10, 10)
        val aventureroReceptor = Aventurero(party, "Jorge", 25, 0, 0, 10, 0, 10)

        assertThat(aventureroReceptor.vida()).isEqualTo(50)

        val dadoDe20Falso = DadoDe20(10)
        val ataque = Ataque(aventureroEmisor.danio_fisico(), aventureroEmisor.precision_fisica(), aventureroReceptor, dadoDe20Falso)
        ataque.resolverParaReceptor(aventureroReceptor)

        assertThat(aventureroReceptor.vida()).isLessThan(50)
    }

    @Test
    fun `un ataque con tirada menor a la armadura mas la mitad de la velocidad del receptor falla`() {
        val party = Party("Party")
        val aventureroEmisor = Aventurero(party, "Pepe", 10, 0, 0, 0, 10, 10)
        val aventureroReceptor = Aventurero(party, "Jorge", 25, 0, 0, 70, 0, 10)

        assertThat(aventureroReceptor.vida()).isEqualTo(170)

        val dadoDe20Falso = DadoDe20(1)
        val ataque = Ataque(aventureroEmisor.danio_fisico(), aventureroEmisor.precision_fisica(), aventureroReceptor, dadoDe20Falso)
        ataque.resolverParaReceptor(aventureroReceptor)

        assertThat(aventureroReceptor.vida()).isEqualTo(170)
    }

    @Test
    fun `un ataque exitoso le resta al receptor una cantidad de vida igual al daño fisico del emisor`(){
        val party = Party("Party")
        val aventureroEmisor = Aventurero(party, "Pepe", 10, 0, 0, 0, 10, 10)
        val aventureroReceptor = Aventurero(party, "Jorge", 21, 0, 0, 20, 0, 10)
        val dadoDe20Falso = DadoDe20(20)
        val ataque = Ataque(aventureroEmisor.danio_fisico(), aventureroEmisor.precision_fisica(), aventureroReceptor, dadoDe20Falso)

        ataque.resolverParaReceptor(aventureroReceptor)

        assertThat(aventureroReceptor.vida()).isEqualTo(55)
    }

    @Test
    fun `cuando un aventurero defiende sufre la mitad de daño`(){
        val party = Party("Party")
        val aventureroDefensor = Aventurero(party, "Pepe", 10, 0, 0, 0, 10, 10)
        val aventureroDefendido = Aventurero(party, "Jorge", 21, 0, 0, 20, 0, 10)
        val defensa = Defensa(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoDe20(20)

        defensa.resolverParaReceptor(aventureroDefendido)
        val ataque = Ataque(aventureroDefendido.danio_fisico(), aventureroDefendido.precision_fisica(), aventureroDefensor, dadoDe20Falso)
        ataque.resolverParaReceptor(aventureroDefensor)

        assertThat(aventureroDefensor.vida()).isEqualTo(4)
    }

    @Test
    fun `cuando un aventurero es defendido sufre la mitad de daño`(){
        val party = Party("Party")
        val aventureroDefensor = Aventurero(party, "Pepe", 10, 0, 0, 0, 10, 10)
        val aventureroDefendido = Aventurero(party, "Jorge", 21, 0, 0, 20, 0, 10)
        val defensa = Defensa(aventureroDefensor, aventureroDefendido)
        val dadoDe20Falso = DadoDe20(20)

        defensa.resolverParaReceptor(aventureroDefendido)
        val ataque = Ataque(aventureroDefensor.danio_fisico(), aventureroDefensor.precision_fisica(), aventureroDefendido, dadoDe20Falso)
        ataque.resolverParaReceptor(aventureroDefendido)

        assertThat(aventureroDefendido.vida()).isEqualTo(61)
    }
}


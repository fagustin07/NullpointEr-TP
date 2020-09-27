package ar.edu.unq.epers.tactics.modelo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows


class AventureroTest() {
    lateinit var cacho: Aventurero
    lateinit var bigTeam: Party

    @BeforeEach
    fun setUp(){
        bigTeam = Party("Big Team")
        cacho = Aventurero(bigTeam,"Cacho",45,10,20,17)

    }

    @Test
    fun unAventureroInicialmenteEsNivelUno(){
        assertEquals(1,cacho.nivel())
    }

    @Test
    fun unAventureroTieneAtributos(){
        assertEquals(45, cacho.fuerza())
        assertEquals(10, cacho.destreza())
        assertEquals(20, cacho.inteligencia())
        assertEquals(17, cacho.constitucion())
    }


    @Test
    fun unAventureroSabeCuantaVidaTiene(){
        assertEquals(84, cacho.vida())
    }

    @Test
    fun unAventureroSabeCuantosPuntosDeArmaduraTiene(){
        assertEquals(18,cacho.armadura())
    }

    @Test
    fun unAventureroSabeCuantoManaTiene(){
        assertEquals(21,cacho.mana())
    }

    @Test
    fun unAventureroSabeCuantosPuntosDeDañoFisicoTiene(){
        assertEquals(51,cacho.danio_fisico())
    }

    @Test
    fun unAventureroSabeCuantosPuntosDePoderMagicoTiene(){
        assertEquals(21,cacho.poder_magico())
    }

    @Test
    fun unAventureroSabeCuantosPuntosDePrecisionFisica(){
        assertEquals(56,cacho.precision_fisica())
    }

    @Test
    fun unAventureroSabeCuantosPuntosDeVelocidadTiene(){
        assertEquals(11,cacho.velocidad())
    }


}
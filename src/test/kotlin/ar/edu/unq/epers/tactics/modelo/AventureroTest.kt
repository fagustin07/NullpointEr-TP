package ar.edu.unq.epers.tactics.modelo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class AventureroTest() {
    lateinit var cacho: Aventurero

    @BeforeEach
    fun setUp() {
        cacho = Aventurero("Cacho", 45, 10, 20, 17)

    }

    @Test
    fun unAventureroInicialmenteEsNivelUno() {
        assertEquals(1, cacho.nivel())
    }

    @Test
    fun unAventureroTieneAtributos() {
        assertEquals(45, cacho.fuerza())
        assertEquals(10, cacho.destreza())
        assertEquals(20, cacho.inteligencia())
        assertEquals(17, cacho.constitucion())
    }


    @Test
    fun unAventureroSabeCuantaVidaTiene() = assertEquals(84, cacho.vida())

    @Test
    fun unAventureroSabeCuantaArmaduraTiene() = assertEquals(18, cacho.armadura())

    @Test
    fun unAventureroSabeCuantoManaTiene() = assertEquals(21, cacho.mana())

    @Test
    fun unAventureroSabeCuantoDañoFisicoTiene() = assertEquals(51, cacho.dañoFisico())

    @Test
    fun unAventureroSabeCuantoPoderMagicoTiene() = assertEquals(22, cacho.poderMagico())

    @Test
    fun unAventureroSabeCuantaPrecisionFisica() = assertEquals(56, cacho.precisionFisica())

    @Test
    fun unAventureroSabeCuantaVelocidadTiene() = assertEquals(11, cacho.velocidad())

}
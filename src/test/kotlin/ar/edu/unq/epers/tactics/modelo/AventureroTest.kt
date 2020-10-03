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
    fun inicialmenteEsNivelUno() {
        assertEquals(1, cacho.nivel())
    }

    @Test
    fun tieneAtributos() {
        assertEquals(45, cacho.fuerza())
        assertEquals(10, cacho.destreza())
        assertEquals(20, cacho.inteligencia())
        assertEquals(17, cacho.constitucion())
    }


    @Test
    fun sabeCuantaVidaTiene() = assertEquals(84, cacho.vida())

    @Test
    fun sabeCuantaArmaduraTiene() = assertEquals(18, cacho.armadura())

    @Test
    fun sabeCuantoManaTiene() = assertEquals(21, cacho.mana())

    @Test
    fun sabeCuantoDañoFisicoTiene() = assertEquals(51, cacho.dañoFisico())

    @Test
    fun sabeCuantoPoderMagicoTiene() = assertEquals(22, cacho.poderMagico())

    @Test
    fun sabeCuantaPrecisionFisica() = assertEquals(56, cacho.precisionFisica())

    @Test
    fun sabeCuantaVelocidadTiene() = assertEquals(11, cacho.velocidad())


}
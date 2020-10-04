package ar.edu.unq.epers.tactics.modelo

import org.junit.jupiter.api.Assertions.*
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

    @Test
    fun inicialmenteNoTieneNingunAliado() {
        assertTrue(cacho.aliados().isEmpty())
    }

    @Test
    fun cuandoLaPartyALaQuePerteneceTieneOtrosAventurerosSonSusAliados() {
        val party = Party("Party", "")
        val aliado = Aventurero("Aliado")

        party.agregarUnAventurero(cacho)
        party.agregarUnAventurero(aliado)

        assertEquals(1, cacho.aliados().size)
        assertTrue(cacho.aliados().contains(aliado))
    }

    @Test
    fun sabeSiEsAliadoDeOtroAventurero() {
        val party = Party("Party", "")
        val aliado = Aventurero("Aliado")
        val noAliado = Aventurero("No aliado")

        party.agregarUnAventurero(cacho)
        party.agregarUnAventurero(aliado)

        assertTrue(cacho.esAliadoDe(aliado))
        assertFalse(cacho.esAliadoDe(noAliado))
    }

    @Test
    fun noEsAliadoDeSiMismo() {
        val party = Party("Party", "")
        party.agregarUnAventurero(cacho)
        assertFalse(cacho.esAliadoDe(cacho))
    }

    @Test
    fun noEsEnemigoDeSiMismo() {
        val party = Party("Party", "")
        party.agregarUnAventurero(cacho)
        assertFalse(cacho.esEnemigoDe(cacho))
    }

    @Test
    fun esEnemigoDeAventurerosQueNoSeanAliados() {
        val party = Party("Party", "")
        val aliado = Aventurero("Aliado")
        val noAliado = Aventurero("No aliado")

        party.agregarUnAventurero(cacho)
        party.agregarUnAventurero(aliado)

        assertFalse(cacho.esEnemigoDe(aliado))
        assertTrue(cacho.esEnemigoDe(noAliado))
    }


}
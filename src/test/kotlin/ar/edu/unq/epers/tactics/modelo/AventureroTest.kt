package ar.edu.unq.epers.tactics.modelo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class AventureroTest {
    lateinit var cacho: Aventurero

    @BeforeEach
    fun setUp() {
        cacho = Aventurero("Cacho","", 45.0, 10.0, 20.0, 17.0)

    }

    @Test
    fun inicialmenteEsNivelUno() {
        assertEquals(1, cacho.nivel())
    }

    @Test
    fun tieneAtributos() {
        assertEquals(45.0, cacho.fuerza())
        assertEquals(10.0, cacho.destreza())
        assertEquals(20.0, cacho.inteligencia())
        assertEquals(17.0, cacho.constitucion())
    }

    @Test
    fun sePuedeActualizarEnBaseALosAtributosDeOtroAventurero() {
        val otroAventurero = Aventurero("Otro aventurero", "xxx", 1.0, 2.0, 3.0, 4.0)
        cacho.actualizarse(otroAventurero)

        assertEquals(otroAventurero.fuerza(), cacho.fuerza())
        assertEquals(otroAventurero.destreza(), cacho.destreza())
        assertEquals(otroAventurero.inteligencia(), cacho.inteligencia())
        assertEquals(otroAventurero.constitucion(), cacho.constitucion())
    }


    @Test
    fun sabeCuantaVidaTiene() = assertEquals(84.0, cacho.vidaActual())

    @Test
    fun sabeCuantaArmaduraTiene() = assertEquals(18.0, cacho.armadura())

    @Test
    fun sabeCuantoManaTiene() = assertEquals(21.0, cacho.mana())

    @Test
    fun sabeCuantoDañoFisicoTiene() = assertEquals(51.0, cacho.dañoFisico())

    @Test
    fun sabeCuantoPoderMagicoTiene() = assertEquals(22.0, cacho.poderMagico())

    @Test
    fun sabeCuantaPrecisionFisica() = assertEquals(56.0, cacho.precisionFisica())

    @Test
    fun sabeCuantaVelocidadTiene() = assertEquals(11.0, cacho.velocidad())

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

    @Test
    fun unAventureroNoPuedeTenerAtributosMayoresACienPuntos(){
        val exception = assertThrows<RuntimeException> { Aventurero("juan","url",978.0) }
        assertEquals("La fuerza no puede exceder los 100 puntos!", exception.message)
    }

    @Test
    fun unAventureroNoPuedeTenerAtributosMenoresAUnPuntos(){
        val exception = assertThrows<RuntimeException> { Aventurero("pepino",
                "url",1.0,12.0,1.0, 0.0) }
        assertEquals("La constitucion no puede ser menor a 1 punto!", exception.message)
    }

    @Test
    fun `un aventurero puede reestablecer su vida y mana`() {
        val vidaInicial = cacho.vidaActual()
        val manaInicial = cacho.mana()
        cacho.recibirAtaqueFisicoSiDebe(10.0, 5000.0)
        cacho.consumirMana()

        cacho.reestablecerse()

        assertThat(cacho.vidaActual()).isEqualTo(vidaInicial)
        assertThat(cacho.mana()).isEqualTo(manaInicial)
    }

    @Test
    fun `un aventurero deja de defender cuando se reestablece`() {
        val otroAventurero = Aventurero("Pepe")
        cacho.defenderA(otroAventurero)

        cacho.reestablecerse()

        assertFalse(cacho.estaDefendiendo())
    }

    @Test
    fun `un aventurero deja de estar defendido cuando se reestablece`() {
        val otroAventurero = Aventurero("Pepe")
        cacho.defenderA(otroAventurero)

        otroAventurero.reestablecerse()

        assertFalse(otroAventurero.estaSiendoDefendiendo())
    }

    @Test
    fun `un aventurero tiene como daño recibido maximo la misma cantidad de vida`(){
        val aventurero = Aventurero("Juan")
        val vidaAntesDeAtaque = aventurero.vidaActual()
        aventurero.recibirAtaqueFisicoSiDebe(1000.0,23232.0)

        assertThat(aventurero.dañoRecibido()).isEqualTo(vidaAntesDeAtaque)
    }
}
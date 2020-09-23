package ar.edu.unq.epers.tactics.modelo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PartyTest {
    private lateinit var losBulls: Party
    private  lateinit var pepe : Aventurero

    @BeforeEach
    fun setUp() {
        losBulls = Party("Los Bulls.")
        pepe = Aventurero(losBulls,50,"Pepe")
    }

    @Test
    fun unaPartyComienzaSinAventureros() = assertEquals(0, losBulls.numeroDeAventureros())

    @Test
    fun sePuedenAgregarAventurerosAUnaParty() {
        losBulls.agregarUnAventurero(pepe)

        assertEquals(1, losBulls.numeroDeAventureros())
        assertEquals(1, losBulls.aventureros.size)
    }

    @Test
    fun noSePuedenAgregarAventurerosAUnaPartyCompleta(){
        repeat(5){
            losBulls.agregarUnAventurero(pepe)
        }

        val exception = assertThrows<RuntimeException> { losBulls.agregarUnAventurero(pepe) }
        assertEquals(exception.message, "La party ${losBulls.nombre} está completa.")

        assertEquals(5, losBulls.numeroDeAventureros())
    }

    @Test
    fun unaPartyNoAgregaAventurerosQueNoPertenezcanAElla(){
        val redHawks = Party("Champions of Red Hawk.")
        val otaku24 = Aventurero(losBulls, 75, "OTK Garfield")

        val exception = assertThrows<RuntimeException> { redHawks.agregarUnAventurero(otaku24) }
        assertEquals(exception.message, "${otaku24.nombre} no pertenece a ${redHawks.nombre}.")
        assertEquals(0, redHawks.numeroDeAventureros())
    }
}
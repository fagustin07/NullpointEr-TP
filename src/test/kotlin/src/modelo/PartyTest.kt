package src.modelo

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PartyTest {
    private lateinit var losBulls: Party

    @BeforeEach
    fun setUp() {
        losBulls = Party("Los Bulls.")
    }

    @Test
    fun unaPartyComienzaSinAventureros() = assertEquals(0, losBulls.numeroDeAventureros)

    @Test
    fun sePuedenAgregarAventurerosAUnaParty() {
        val pepe = Aventurero(losBulls,50,"Pepe")
        losBulls.agregarUnAventurero(pepe)

        assertEquals(1, losBulls.numeroDeAventureros)
    }
}
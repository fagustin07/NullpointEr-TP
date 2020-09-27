package ar.edu.unq.epers.tactics.modelo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PartyTest {
    private lateinit var party: Party
    private lateinit var pepe: Aventurero

    @BeforeEach
    fun setUp() {
        party = Party("Los Bulls")
        pepe = Aventurero(party,  "Pepe")
    }

    @Test
    fun unaPartyComienzaSinAventureros() = assertEquals(0, party.numeroDeAventureros())

    @Test
    fun sePuedenAgregarAventurerosAUnaParty() {
        party.agregarUnAventurero(pepe)

        assertEquals(1, party.numeroDeAventureros())
        assertEquals(1, party.aventureros.size)
    }

    @Test
    fun noSePuedenAgregarAventurerosAUnaPartyCompleta() {
        val cincoAventureros = (1..5).map { Aventurero(party,  "Aventurero${it}") }

        cincoAventureros.forEach { aventurero -> party.agregarUnAventurero(aventurero) }

        val exception = assertThrows<RuntimeException> { party.agregarUnAventurero(pepe) }
        assertEquals(exception.message, "La party ${party.nombre} está completa.")

        assertEquals(5, party.numeroDeAventureros())
        assertTrue(party.aventureros.containsAll(cincoAventureros))
    }

    @Test
    fun unaPartyNoAgregaAventurerosQueNoPertenezcanAElla() {
        val otraParty = Party("Champions of Red Hawk.")
        val aventureroDeOtraParty = Aventurero(otraParty,  "OTK Garfield")

        val exception = assertThrows<RuntimeException> { party.agregarUnAventurero(aventureroDeOtraParty) }
        assertEquals(exception.message, "${aventureroDeOtraParty.nombre} no pertenece a ${party.nombre}.")
        assertEquals(0, party.numeroDeAventureros())
    }


    @Test
    fun noPuedeAgregarseAUnaPartyUnAventureroQueYaFueAgregado() {
        val party = Party("Nombre de party")
        val aventurero = Aventurero(party, "Nombre de aventurero")

        party.agregarUnAventurero(aventurero)

        val exception = assertThrows<RuntimeException> { party.agregarUnAventurero(aventurero) }
        assertEquals(exception.message, "${aventurero.nombre} ya forma parte de la party ${party.nombre}.")

        assertEquals(1, party.numeroDeAventureros())
        assertTrue(party.aventureros.contains(aventurero))
    }

    @Test
    fun unaPartyDebeTenerUnNombre() {
        val exception = assertThrows<RuntimeException> { Party("") }
        assertEquals(exception.message, "Una party debe tener un nombre")
    }
}
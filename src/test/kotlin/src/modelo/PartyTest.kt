package src.modelo

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import org.junit.jupiter.api.Assertions
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
        val shiro = Aventurero(losBulls, 5500, "Shiro99")
        losBulls.agregarA(shiro)

        assertEquals(1, losBulls.numeroDeAventureros)
    }

    @Test
    fun noSePuedeAgregarUnAventureroQueNoPertenezcaALaParty() {
        val losPanas = Party("Los Panas")
        val asura = Aventurero(losPanas, 5500, "Asuraaa")

        Assertions.assertThrows(
            RuntimeException::class.java,
            { losBulls.agregarA(asura) },
            "El aventurero no pertenece a la party seleccionada."
        )
    }
}
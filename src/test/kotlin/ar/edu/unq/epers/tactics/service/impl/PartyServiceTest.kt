package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import helpers.DataServiceHelper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PartyServiceTest {

    private val dao: PartyDAO = HibernatePartyDAO()
    private lateinit var partyService: PartyServiceImpl

    @BeforeEach
    fun setUp() {
        partyService = PartyServiceImpl(dao)
    }

    @Test
    fun inicialmenteNoHayNingunaPartyRegistrada() {
        assertTrue(partyService.recuperarTodas().isEmpty())
    }

    @Test
    fun seCreaExitosamenteUnaParty() {
        val party = Party("UnNombre", "URL")

        val partyId = partyService.crear(party).id()!!

        val todasLasParties = partyService.recuperarTodas()

        assertEquals(1, todasLasParties.size)
        assertEquals(partyId, todasLasParties[0].id())
        assertEqualParty(party, todasLasParties[0])
    }

    @Test
    fun sePuedeRecuperarUnaPartyConSuId() {
        val partyOriginal = Party("nombre de party", "URL")
        val partyId = partyService.crear(partyOriginal).id()!!

        val partyRecuperada = partyService.recuperar(partyId)

        assertEquals(partyId, partyRecuperada.id())
        assertEqualParty(partyOriginal, partyRecuperada)
    }

    @Test
    fun noSePuedeRecuperarUnaPartySiNoExisteNingunaPartyConElIdProvisto() {
        val idInvalido = 0L
        val exception = assertThrows<RuntimeException> { partyService.recuperar(idInvalido) }
        assertEquals("No existe Party con id ${idInvalido}", exception.message)
    }

    @Test
    fun alAgregarUnNuevoAventureroAUnaParty_aumentaLaCantidadDeAventurerosDeLaMisma() {
        val party = Party("Nombre de party", "URL")
        val aventurero = Aventurero("Pepe")
        val partyId = partyService.crear(party).id()!!

        partyService.agregarAventureroAParty(partyId, aventurero)
        val partyRecuperada = partyService.recuperar(partyId)

        assertEquals(1, partyRecuperada.numeroDeAventureros())
        assertNotEquals(party.numeroDeAventureros(), partyRecuperada.numeroDeAventureros())
    }

    @Test
    fun noSePuedeActualizarUnaPartyQueNoFuePersistida() {
        val party = Party("Nombre de party", "URL")

        val exception = assertThrows<RuntimeException> { partyService.actualizar(party) }
        assertEquals("No se puede actualizar una party que no fue persistida", exception.message)
    }

    @Test
    fun noSePuedeAgregarUnAventureroAUnaPartyQueNoFueCreada() {
        val aventurero = Aventurero("Pepe")
        val idNoRegistrado: Long = 45

        val exception = assertThrows<RuntimeException> { partyService.agregarAventureroAParty(idNoRegistrado, aventurero) }
        assertEquals("No existe Party con id ${idNoRegistrado}", exception.message)
    }

    @AfterEach
    fun tearDown() {
        DataServiceHelper(partyService).eliminarTodo()
    }

    private fun assertEqualParty(expectedParty: Party, obtainedParty: Party) {
        assertEquals(expectedParty.nombre(), obtainedParty.nombre())
        assertEquals(expectedParty.numeroDeAventureros(), obtainedParty.numeroDeAventureros())
    }

}

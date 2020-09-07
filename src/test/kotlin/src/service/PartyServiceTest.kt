package src.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.jdbc.IPartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.jdbc.JDBCPartyDAO
import ar.edu.unq.epers.tactics.service.PersistentPartyService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PartyServiceTest {

    private lateinit var partyService: PersistentPartyService
    private lateinit var dao: IPartyDAO

    @BeforeEach
    fun setUp() {
        dao = createDAO()
        partyService = PersistentPartyService(dao)
    }

    @Test
    fun inicialmenteNoHayNingunaPartyRegistrada() {
        assertTrue(partyService.recuperarTodas().isEmpty())
    }

    @Test
    fun seCreaExitosamenteUnaParty() {
        val party = Party("UnNombre")

        val partyId = partyService.crear(party)

        val todasLasParties = partyService.recuperarTodas()

        assertEquals(1, todasLasParties.size)
        assertEquals(partyId, todasLasParties[0].id)
        assertEqualParty(party, todasLasParties[0])
    }

    @Test
    fun sePuedeRecuperarUnaPartyConSuId() {
        val partyOriginal = Party("nombre de party")
        val partyId = partyService.crear(partyOriginal)

        val partyRecuperada = partyService.recuperar(partyId)

        assertEquals(partyId, partyRecuperada.id)
        assertEqualParty(partyOriginal, partyRecuperada)
    }

    @Test
    fun noSePuedeRecuperarUnaPartySiNoExisteNingunaPartyConElIdProvisto() {
        assertThrows(Exception::class.java, { partyService.recuperar(0) }, "No hay ninguna party con el id provisto")
    }

    @Test
    fun alAgregarUnNuevoAventureroAUnaPartyEstaCambiaSusValores() {
        val party = Party("Nombre de party")
        val aventurero = Aventurero(party, 50, "Pepe")
        val partyId = partyService.crear(party)

        partyService.agregarAventureroAParty(partyId, aventurero)
        val partyRecuperada = partyService.recuperar(partyId)

        assertEquals(1, partyRecuperada.numeroDeAventureros)
        assertNotEquals(party.numeroDeAventureros, partyRecuperada.numeroDeAventureros)


    }


    @AfterEach
    fun tearDown() {
        (dao as JDBCPartyDAO).eliminarTablaDeParty()
    }

    private fun assertEqualParty(expectedParty: Party, obtainedParty: Party) {
        assertEquals(expectedParty.nombre, obtainedParty.nombre)
        assertEquals(expectedParty.numeroDeAventureros, obtainedParty.numeroDeAventureros)
    }

    private fun createDAO() = JDBCPartyDAO()
}

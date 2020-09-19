package src.persistencia.dao.jdbc

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.jdbc.JDBCPartyDAO
import helpers.DataServiceHelper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JDBCPartyDAOTest {
    private val adminPartyDAO: JDBCPartyDAO = JDBCPartyDAO()
    lateinit var bigTeam: Party

    @BeforeEach
    fun crearModelo() {
        bigTeam = Party("Null PointEr Team")
    }

    @Test
    fun alCrearUnaPartyYLuegoRecuperarlaSeObtienenObjetosSimilares() {

        val idParty = adminPartyDAO.crear(bigTeam).id!!

        val recoveryBigTeam = adminPartyDAO.recuperar(idParty)

        assertEquals(bigTeam.nombre, recoveryBigTeam.nombre)
        assertEquals(idParty, recoveryBigTeam.id)
        assertEquals(0, recoveryBigTeam.numeroDeAventureros)
        assertNotEquals(bigTeam, recoveryBigTeam)
    }

    @Test
    fun cuandoSeRecuperanTodasLasPartySeLasObtieneOrdenadasPorNombreEnFormaAscendente() {
        val betaParty = Party("Beta")
        val alphaParty = Party("Alpha")

        adminPartyDAO.crear(betaParty)
        adminPartyDAO.crear(alphaParty)

        val partiesObtenidas = adminPartyDAO.recuperarTodas()

        assertEquals(2, partiesObtenidas.size)
        assertEquals(alphaParty.nombre, partiesObtenidas[0].nombre)
        assertEquals(betaParty.nombre, partiesObtenidas[1].nombre)
    }

    @Test
    fun cuandoSeActualizaUnaParty_luegoSeLaRecuperaConLaInformacionActualizada() {
        val party = Party("Beta")
        val partyId = adminPartyDAO.crear(party).id!!

        party.numeroDeAventureros = 4
        adminPartyDAO.actualizar(party)
        val partyActualizada = adminPartyDAO.recuperar(partyId)

        assertEquals(4, partyActualizada.numeroDeAventureros)
    }


    @AfterEach
    fun eliminarDatos() {
        DataServiceHelper().eliminarTodo()
    }
}

package src

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.jdbc.JDBCPartyDAO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*

class JDBCPartyDAOTest {
    private val adminPartyDAO: JDBCPartyDAO = JDBCPartyDAO()
    lateinit var bigTeam : Party

    @BeforeEach
    fun crearModelo() {
        bigTeam = Party("Null PointEr Team")
    }

    @Test
    fun alCrearUnaPartySeObtieneSuId() {
        val partyID = adminPartyDAO.crear(bigTeam)

        assertEquals(1, partyID)
    }
  
    @Test
    fun alCrearUnaPartyYLuegoRecuperarlaSeObtienenObjetosSimilares() {

        val idParty = adminPartyDAO.crear(bigTeam)

        val partyRecov = adminPartyDAO.recuperar(idParty)

        assertEquals(bigTeam.nombre, partyRecov.nombre)
        assertEquals(idParty, partyRecov.id)
        assertEquals(0, partyRecov.numeroDeAventureros)

    }

    @Test
    fun cuandoSeRecuperanTodasLasPartidasSeLasObtieneOrdenadasPorNombreEnFormaAscendente_CaseInsensitive_Asdasdasdasdasd() {
        val alphaParty = Party("Alpha")
        val gammaParty = Party("Gamma")
        val betaParty = Party("beta")
        adminPartyDAO.crear(alphaParty)
        adminPartyDAO.crear(gammaParty)
        adminPartyDAO.crear(betaParty)

        val partiesObtenidas = adminPartyDAO.recuperarTodas()

        assertEquals(3, partiesObtenidas.size)
        assertEquals(alphaParty.nombre, partiesObtenidas[0].nombre)
        assertEquals(betaParty.nombre, partiesObtenidas[1].nombre)
        assertEquals(gammaParty.nombre, partiesObtenidas[2].nombre)
    }

    @AfterEach
    fun eliminarDatos(){
        // creo esta funcion hasta tener el dataService
        adminPartyDAO.eliminarTablaDeParty()
    }
}

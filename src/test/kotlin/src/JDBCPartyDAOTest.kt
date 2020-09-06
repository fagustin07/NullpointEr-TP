package src

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.jdbc.JDBCPartyDAO
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class JDBCPartyDAOTest {
    private val adminPartyDAO: JDBCPartyDAO = JDBCPartyDAO()
    lateinit var bigTeam : Party

    @Before
    fun crearModelo() {
        bigTeam = Party("Null PointEr Team")
    }

    @Test
    fun alCrearUnaPartySeObtieneSuId() {
        val partyID = adminPartyDAO.crear(bigTeam)

        Assert.assertEquals(1, partyID)
    }

    @Test
    fun alCrearUnaPartyYLuegoRecuperarlaSeObtienenObjetosSimilares() {

        val idParty = adminPartyDAO.crear(bigTeam)

        val partyRecov = adminPartyDAO.recuperar(idParty)

        Assert.assertEquals(bigTeam.nombre, partyRecov.nombre)
        Assert.assertEquals(idParty, partyRecov.id)
        Assert.assertEquals(0, partyRecov.numeroDeAventureros)

    }

    @After
    fun eliminarDatos(){
        // creo esta funcion hasta tener el dataService
        adminPartyDAO.eliminarTablaDeParty()
    }
}

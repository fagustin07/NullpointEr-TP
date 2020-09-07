package src.DataServiceTest

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.jdbc.IPartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.jdbc.JDBCPartyDAO
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.PersistentPartyService
import helpers.DataService
import helpers.DataServiceHelper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class DataServiceTest {

    lateinit var partyService: PartyService
    lateinit var dao: IPartyDAO
    lateinit var dataService : DataService

    @BeforeEach
    fun setUp() {
        dao = JDBCPartyDAO()
        partyService = PersistentPartyService(dao)
        dataService = DataServiceHelper()
    }

    @Test
    fun elSetDeDatosCreadoQuedaVacioLuegoDeEliminarTodoConElDataService(){
        val team1 = Party("Team 1")
        val team2 = Party("Team 2")

        partyService.crear(team1)
        partyService.crear(team2)

        // No esta vacia
        assertTrue(partyService.recuperarTodas().isNotEmpty())

        //Vacio los datos persistidos
        dataService.eliminarTodo()

        //Esta vacia
        assertTrue(partyService.recuperarTodas().isEmpty())
        assertThrows(Exception::class.java,  { partyService.recuperar(0) },
                "No hay ninguna party con el id dado")

    }

}
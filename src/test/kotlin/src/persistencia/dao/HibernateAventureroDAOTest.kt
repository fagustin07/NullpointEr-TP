package src.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class HibernateAventureroDAOTest {
    private val aventureroDAO = HibernateAventureroDAO()
    private var partyDAO = HibernatePartyDAO()
    lateinit var pepito : Aventurero
    lateinit var bigTeam : Party

    @BeforeEach
    fun setUp(){
        bigTeam = Party("Big Team")
        pepito = Aventurero(bigTeam,50,"Pepito")
    }

    @Test
    fun alRecuperarUnAventureroSeObtienenObjetosSimilares(){
        HibernateTransactionRunner.runTrx {
            val pepitoId = aventureroDAO.crear(pepito).id!!
            bigTeam.agregarUnAventurero(pepito)
            partyDAO.crear(bigTeam)
            val recovPepito = aventureroDAO.recuperar(pepitoId)

            assertEquals(pepito.nombre, recovPepito.nombre)
            assertEquals(pepito.vida, recovPepito.vida)
            assertEquals(pepitoId, recovPepito.id)
            assertEquals(pepito.party, recovPepito.party)
        }

    }

    @Test
    fun seActualizaLaVidaDeUnAventureroYLuegoSeLoRecuperaActualizado(){
        HibernateTransactionRunner.runTrx {
            val pepitoId = aventureroDAO.crear(pepito).id!!
            bigTeam.agregarUnAventurero(pepito)
            partyDAO.crear(bigTeam)

            pepito.vida = 27

            val recovPepito = aventureroDAO.actualizar(pepito)

            assertEquals(pepito.nombre, recovPepito.nombre)
            assertEquals(pepito.vida, recovPepito.vida)
            assertEquals(pepitoId, recovPepito.id)
            assertEquals(pepito.party, recovPepito.party)


        }
    }

    @Test
    fun alEliminarUnAventureroPersistidoYLuegoRecuperarloNoExiste(){
        HibernateTransactionRunner.runTrx {
            val pepitoId = aventureroDAO.crear(pepito).id!!
            aventureroDAO.eliminar(pepito)

            assertEquals(null, aventureroDAO.recuperar(pepitoId))
        }
    }


    @AfterEach
    fun eliminarDatos() {
        HibernateTransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }
}
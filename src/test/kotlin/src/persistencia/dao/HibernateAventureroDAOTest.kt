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
import org.assertj.core.api.Assertions.*


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
            val pepitoId = generateModel()
            val recoveryPepito = aventureroDAO.recuperar(pepitoId)

            assertThat(pepito).isEqualToComparingFieldByField(recoveryPepito)
        }

    }

    @Test
    fun seActualizaLaVidaDeUnAventureroYLuegoSeLoRecuperaActualizado(){
        HibernateTransactionRunner.runTrx {
            val pepitoId = generateModel()

            pepito.vida = 27
            aventureroDAO.actualizar(pepito)
            val recoveryPepito = aventureroDAO.recuperar(pepitoId)

            assertThat(pepito).isEqualToComparingFieldByField(recoveryPepito)
        }
    }

    @Test
    fun alEliminarUnAventureroPersistidoYLuegoRecuperarloNoExiste(){
        HibernateTransactionRunner.runTrx {
            val pepitoId = aventureroDAO.crear(pepito).id!!
            aventureroDAO.eliminar(pepito)

            assertThrows( Exception::class.java)
            {
               aventureroDAO.recuperar(pepitoId)
            }
        }
    }

    fun generateModel() : Long {
        val pepitoId = aventureroDAO.crear(pepito).id!!
        bigTeam.agregarUnAventurero(pepito)
        partyDAO.crear(bigTeam)
        return pepitoId
    }

    @AfterEach
    fun eliminarDatos() {
        HibernateTransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }
}
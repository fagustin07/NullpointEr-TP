package src.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HibernatePartyDAOTest {
    private val partyDAO = HibernatePartyDAO()
    lateinit var bigTeam: Party


    @BeforeEach
    fun crearModelo() {
        bigTeam = Party("Null PointEr Team")
    }

    @Test
    fun alCrearUnaPartyYLuegoRecuperarlaSeObtienenObjetosEquivalentes() {
        HibernateTransactionRunner.runTrx {
            val idParty = partyDAO.crear(bigTeam).id!!

            val recoveryBigTeam = partyDAO.recuperar(idParty)

            assertEquals(bigTeam.nombre, recoveryBigTeam.nombre)
            assertEquals(idParty, recoveryBigTeam.id)
            assertEquals(0, recoveryBigTeam.numeroDeAventureros)
        }
    }

    @Test
    fun cuandoSeRecuperanTodasLasPartySeLasObtieneOrdenadasPorNombreEnFormaAscendente() {
        HibernateTransactionRunner.runTrx {
            val betaParty = Party("Beta")
            val alphaParty = Party("Alpha")

            partyDAO.crear(betaParty)
            partyDAO.crear(alphaParty)

            val partiesObtenidas = partyDAO.recuperarTodas()

            assertEquals(2, partiesObtenidas.size)
            assertEquals(alphaParty.nombre, partiesObtenidas[0].nombre)
            assertEquals(betaParty.nombre, partiesObtenidas[1].nombre)
        }
    }

    @Test
    fun cuandoSeActualizaUnaParty_luegoSeLaRecuperaConLaInformacionActualizada() {
        HibernateTransactionRunner.runTrx {
            val party = Party("Beta")
            val partyId = partyDAO.crear(party).id!!

            party.numeroDeAventureros = 4
            partyDAO.actualizar(party)
            val partyActualizada = partyDAO.recuperar(partyId)

            assertEquals(4, partyActualizada.numeroDeAventureros)
        }
    }


    @AfterEach
    fun eliminarDatos() {
        HibernateTransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }
}

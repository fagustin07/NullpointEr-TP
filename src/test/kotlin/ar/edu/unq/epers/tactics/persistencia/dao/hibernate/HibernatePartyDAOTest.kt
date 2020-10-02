package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
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
        bigTeam = Party("Null PointEr Team", "URL")
    }

    @Test
    fun alCrearUnaPartyYLuegoRecuperarlaSeObtienenObjetosEquivalentes() {
        HibernateTransactionRunner.runTrx {
            val idParty = partyDAO.crear(bigTeam).id()!!

            val recoveryBigTeam = partyDAO.recuperar(idParty)

            assertEquals(bigTeam.nombre(), recoveryBigTeam.nombre())
            assertEquals(idParty, recoveryBigTeam.id())
            assertEquals(0, recoveryBigTeam.numeroDeAventureros())
        }
    }

    @Test
    fun cuandoSeRecuperanTodasLasPartySeLasObtieneOrdenadasPorNombreEnFormaAscendente() {
        HibernateTransactionRunner.runTrx {
            val betaParty = Party("Beta", "URL")
            val alphaParty = Party("Alpha", "URL")

            partyDAO.crear(betaParty)
            partyDAO.crear(alphaParty)

            val partiesObtenidas = partyDAO.recuperarTodas()

            assertEquals(2, partiesObtenidas.size)
            assertEquals(alphaParty.nombre(), partiesObtenidas[0].nombre())
            assertEquals(betaParty.nombre(), partiesObtenidas[1].nombre())
        }
    }

    @Test
    fun cuandoSeActualizaUnaParty_luegoSeLaRecuperaConLaInformacionActualizada() {
        HibernateTransactionRunner.runTrx {
            val party = Party("Beta", "URL")
            val partyId = partyDAO.crear(party).id()!!
            val wos = Aventurero("wos", party = party)

            party.agregarUnAventurero(wos)

            partyDAO.actualizar(party)
            val partyActualizada = partyDAO.recuperar(partyId)

            assertEquals(1, partyActualizada.numeroDeAventureros())
        }
    }


    @AfterEach
    fun eliminarDatos() { partyDAO.eliminarTodo() }
}

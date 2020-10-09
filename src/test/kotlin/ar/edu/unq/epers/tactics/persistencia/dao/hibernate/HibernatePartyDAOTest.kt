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
        var idParty: Long? = null
        lateinit var partyRecuperada:Party
        HibernateTransactionRunner.runTrx {
            idParty = partyDAO.crear(bigTeam).id()!!
            partyRecuperada = partyDAO.recuperar(idParty!!)

        }
        assertEquals(bigTeam.nombre(), partyRecuperada.nombre())
        assertEquals(idParty, partyRecuperada.id())
        assertEquals(0, partyRecuperada.numeroDeAventureros())
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
        var partyId:Long? = null
        HibernateTransactionRunner.runTrx {
            val party = Party("Beta", "URL")
            partyId = partyDAO.crear(party).id()!!
            val wos = Aventurero("wos")

            party.agregarUnAventurero(wos)

            partyDAO.actualizar(party)
        }

        HibernateTransactionRunner.runTrx{
            val partyActualizada = partyDAO.recuperar(partyId!!)

            assertEquals(1, partyActualizada.numeroDeAventureros())
        }
    }


    @AfterEach
    fun eliminarDatos() { partyDAO.eliminarTodo() }
}

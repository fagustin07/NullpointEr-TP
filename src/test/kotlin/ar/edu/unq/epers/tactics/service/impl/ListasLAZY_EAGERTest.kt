package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.modelo.enums.*
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

//clase creada porque nos estaba fallando la recuperacion de una party o de un aventurero
// por como estabamos definiendo la forma de persistir las listas con las que estaban relacionados.
class ListasLAZY_EAGERTest {

    val partyDAO = HibernatePartyDAO()
    val aventureroDAO = HibernateAventureroDAO()
    lateinit var betaParty: Party
    lateinit var alphaParty: Party
    lateinit var aventurero: Aventurero

    @BeforeEach
    fun setUp() {
        betaParty = Party("Beta", "URL")
        alphaParty = Party("Alpha", "URL")
        aventurero = Aventurero("Mordedor")
        aventurero.agregarTactica(
            Tactica(
                1, TipoDeReceptor.ENEMIGO,
                TipoDeEstadistica.VIDA,
                Criterio.MAYOR_QUE,
                0.0,
                Accion.ATAQUE_MAGICO
            )
        )
        HibernateTransactionRunner.runTrx {
            partyDAO.crear(betaParty)
            partyDAO.crear(alphaParty)
        }
        HibernateTransactionRunner.runTrx {
            betaParty.agregarUnAventurero(aventurero)
            partyDAO.actualizar(betaParty)
        }


    }

    @Test
    fun TEST_CASO1_RECUPERAR_TODAS_PARTIES() {
        lateinit var partiesObtenidas: MutableList<Party>

        HibernateTransactionRunner.runTrx {
            partiesObtenidas = partyDAO.recuperarTodas()
        }

        assertThat(partiesObtenidas.size).isEqualTo(2)
    }

    @Test
    fun TEST_CASO2_RECUPERA_UNA_PARTY() {
        lateinit var partyRecuperada: Party

        HibernateTransactionRunner.runTrx {
            partyRecuperada = partyDAO.recuperar(betaParty.id()!!)
        }

        assertThat(partyRecuperada.aventureros().size).isEqualTo(1)
    }

    @Test
    fun TEST_CASO3_RECUPERAR_UN_AVENTURERO() {
        lateinit var aventureroRecuperado: Aventurero

        HibernateTransactionRunner.runTrx {
            aventureroRecuperado = aventureroDAO.recuperar(aventurero.id()!!)
        }

        assertThat(aventureroRecuperado.tacticas().size).isEqualTo(1)
    }

    @AfterEach
    fun tearDown() {
        partyDAO.eliminarTodo()
    }
}
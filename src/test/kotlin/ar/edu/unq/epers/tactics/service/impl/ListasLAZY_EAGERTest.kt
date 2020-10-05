package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.dto.Criterio
import ar.edu.unq.epers.tactics.service.dto.TipoDeEstadistica
import ar.edu.unq.epers.tactics.service.dto.TipoDeReceptor
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ListasLAZY_EAGERTest {
//    //TODO: Hacer que estos tests anden,
//    // posible solucion, listas en LAZY excepto cuando se recupera una entidad en especifico
//
//    val partyDAO = HibernatePartyDAO()
//    val aventureroDAO = HibernateAventureroDAO()
//    lateinit var betaParty: Party
//    lateinit var alphaParty: Party
//    lateinit var aventurero: Aventurero
//
//    @BeforeEach
//    fun setUp() {
//        betaParty = Party("Beta", "URL")
//        alphaParty = Party("Alpha", "URL")
//        aventurero = Aventurero("Mordedor")
//        aventurero.agregarTactica(
//            Tactica(
//                1, TipoDeReceptor.ENEMIGO,
//                TipoDeEstadistica.VIDA,
//                Criterio.MAYOR_QUE,
//                0,
//                Accion.ATAQUE_MAGICO
//            )
//        )
//        HibernateTransactionRunner.runTrx {
//            partyDAO.crear(betaParty)
//            partyDAO.crear(alphaParty)
//        }
//        HibernateTransactionRunner.runTrx {
//            betaParty.agregarUnAventurero(aventurero)
//            partyDAO.actualizar(betaParty)
//        }
//
//
//    }
//
//    @Test
//    fun TEST_CASO1_RECUPERAR_TODAS_PARTIES() {
//        lateinit var partiesObtenidas: MutableList<Party>
//
//        HibernateTransactionRunner.runTrx {
//            //Si 'tacticas' y 'aventureros' son EAGER, se rompen todos los tests
//            // cannot simultaneously fetch multiple bags: [ar.edu.unq.epers.tactics.modelo.Party.aventureros, ar.edu.unq.epers.tactics.modelo.Aventurero.tacticas]
//            partiesObtenidas = partyDAO.recuperarTodas()
//        }
//
//        assertThat(partiesObtenidas.size).isEqualTo(2)
//    }
//
//    @Test
//    fun TEST_CASO2_RECUPERA_UNA_PARTY() {
//        lateinit var partyRecuperada: Party
//
//        HibernateTransactionRunner.runTrx {
//            //Si 'aventureros es LAZY, cuando recuperas una party no podes acceder a sus aventureros
//            partyRecuperada = partyDAO.recuperar(betaParty.id()!!)
//        }
//
//        assertThat(partyRecuperada.aventureros().size).isEqualTo(1)
//    }
//
//    @Test
//    fun TEST_CASO3_RECUPERAR_UN_AVENTURERO() {
//        lateinit var aventureroRecuperado: Aventurero
//
//        HibernateTransactionRunner.runTrx {
//            //Si 'tacticas' es LAZY, cuando recuperas un aventurero no podes acceder a sus tacticas
//            aventureroRecuperado = aventureroDAO.recuperar(aventurero.id()!!)
//        }
//
//        assertThat(aventureroRecuperado.tacticas().size).isEqualTo(1)
//    }
//
//    @AfterEach
//    fun tearDown() {
//        partyDAO.eliminarTodo()
//    }
}
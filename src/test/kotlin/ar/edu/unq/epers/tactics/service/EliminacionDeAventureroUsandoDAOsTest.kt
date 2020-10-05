package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PersistentPartyService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

class EliminacionDeAventureroUsandoDAOsTest {
    lateinit private var party: Party
    lateinit private var aventurero: Aventurero

    lateinit var aventureroDao: AventureroDAO
    lateinit var partyDao: PartyDAO

    @BeforeEach
    fun setUp() {
        aventureroDao = HibernateAventureroDAO()
        partyDao = HibernatePartyDAO()

        party = Party("Party", "")
        aventurero = Aventurero("Aventurero")
    }

    @Test
    fun `cuando se le agrega un aventurero a una party y se la actualiza, el aventurero es persistido`() {
        HibernateTransactionRunner.runTrx {
            partyDao.crear(party)
            party.agregarUnAventurero(aventurero)
            partyDao.actualizar(party)
        }

        HibernateTransactionRunner.runTrx {
            val partyRecuperada = partyDao.recuperar(party.id()!!)
            val aventureroRecuperado = aventureroDao.recuperar(aventurero.id()!!)

            assertEquals(1, partyRecuperada.numeroDeAventureros())
            assertThat(aventureroRecuperado).usingRecursiveComparison().isEqualTo(aventurero)
        }
    }

    @Test
    fun `cuando se remueve un aventurero de una party y se la actualiza, el aventurero deja de estar persistido`() {
        HibernateTransactionRunner.runTrx {
            partyDao.crear(party)
        }

        HibernateTransactionRunner.runTrx {
            party.agregarUnAventurero(aventurero)
            partyDao.actualizar(party)
        }

        HibernateTransactionRunner.runTrx {
            party.removerA(aventurero)
            partyDao.actualizar(party)
        }

        HibernateTransactionRunner.runTrx {
            val partyRecuperada = partyDao.recuperar(party.id()!!)

            assertEquals(0, partyRecuperada.numeroDeAventureros())
            val exception = assertThrows<RuntimeException> { aventureroDao.recuperar(aventurero.id()!!) }
            assertEquals("No existe una entidad con ese id", exception.message)
        }
    }

    @AfterEach
    fun tearDown() {
        partyDao.eliminarTodo()
    }

}
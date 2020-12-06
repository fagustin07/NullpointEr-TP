package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBInventarioPartyDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AventureroServiceTest {

    private lateinit var aventureroDao: AventureroDAO
    private lateinit var partyDao: PartyDAO

    private lateinit var aventureroService: AventureroServiceImpl
    private lateinit var partyService: PartyServiceImpl

    private lateinit var party: Party
    private lateinit var aventurero: Aventurero

    @BeforeEach
    fun setUp() {
        aventureroDao = HibernateAventureroDAO()
        partyDao = HibernatePartyDAO()

        aventureroService = AventureroServiceImpl(aventureroDao, partyDao)
        partyService = PartyServiceImpl(partyDao, OrientDBInventarioPartyDAO())

        party = Party("Party", "")
        aventurero = Aventurero("Aventurero")
    }

    @Test
    fun seRecuperaUnAventurero() {
        partyService.crear(party)
        partyService.agregarAventureroAParty(party.id()!!, aventurero)

        val aventureroRecuperado = aventureroService.recuperar(aventurero.id()!!)

        HibernateTransactionRunner.runTrx {
            assertThat(aventurero).usingRecursiveComparison().isEqualTo(aventureroRecuperado)
        }

    }

    @Test
    fun seActualizaUnAventurero() {
        partyService.crear(party)
        partyService.agregarAventureroAParty(party.id()!!, aventurero)

        val aventureroDTO = Aventurero("Otro nombre", "/otra_imagen.jpg", 1.0, 2.0, 3.0, 4.0)
        aventureroDTO.darleElId(aventurero.id()!!)
        aventurero.actualizarse(aventureroDTO)

        aventureroService.actualizar(aventurero)

        HibernateTransactionRunner.runTrx {
            val aventureroRecuperado = aventureroService.recuperar(aventurero.id()!!)
            assertThat(aventurero).usingRecursiveComparison().isEqualTo(aventureroRecuperado)
        }
    }

    @Test
    fun seEliminaUnAventurero() {
        val partyId = partyService.crear(party).id()!!
        val aventureroId = partyService.agregarAventureroAParty(partyId, aventurero).id()!!

        aventureroService.eliminar(aventurero)

        HibernateTransactionRunner.runTrx {
            val partyRecuperada = partyService.recuperar(partyId)
            assertEquals(0, partyRecuperada.numeroDeAventureros())
            val exception = assertThrows<RuntimeException> { aventureroService.recuperar(aventureroId) }
            assertEquals("No existe Aventurero con id ${aventurero.id()!!}", exception.message)
        }
    }

    @AfterEach
    internal fun tearDown() {
        partyService.eliminarTodo()
        OrientDBDataDAO().clear()
    }

}

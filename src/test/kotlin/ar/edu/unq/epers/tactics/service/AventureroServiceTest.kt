package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.service.dto.AtributosDTO
import ar.edu.unq.epers.tactics.service.dto.AventureroDTO
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PersistentPartyService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.assertj.core.api.Assertions
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
    private lateinit var partyService: PersistentPartyService

    private lateinit var party: Party
    private lateinit var aventurero: Aventurero

    @BeforeEach
    fun setUp() {
        aventureroDao = HibernateAventureroDAO()
        partyDao = HibernatePartyDAO()

        aventureroService = AventureroServiceImpl(aventureroDao, partyDao)
        partyService = PersistentPartyService(partyDao)

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

        val aventureroDTO = AventureroDTO(aventurero.id()!!, 2, "Otro nombre", "/otra_imagen.jpg", listOf(), AtributosDTO(null, 1,2, 3, 4))
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
            partyRecuperada // Breakpoint... si se inspecciona partyRecuperada todavia tiene al aventurero. la variable de instancia "party" no lo tiene
            assertEquals(0, partyRecuperada.numeroDeAventureros())
            val exception = assertThrows<RuntimeException> { aventureroService.recuperar(aventureroId) }
            assertEquals("No existe una entidad con ese id", exception.message)
        }
    }

}

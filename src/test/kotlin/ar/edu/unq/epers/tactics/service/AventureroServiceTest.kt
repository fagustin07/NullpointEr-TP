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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AventureroServiceTest {

    private val aventureroDao: AventureroDAO = HibernateAventureroDAO()
    private val partyDao: PartyDAO = HibernatePartyDAO()

    private lateinit var aventureroService: AventureroServiceImpl
    private lateinit var partyService: PersistentPartyService

    private lateinit var party: Party
    private lateinit var aventurero: Aventurero

    @BeforeEach
    fun setUp() {
        aventureroService = AventureroServiceImpl(aventureroDao, partyDao)
        partyService = PersistentPartyService(partyDao)

        party = Party("Party", "")
        aventurero = Aventurero("Aventurero")
    }

    @Test
    fun seRecuperaUnAventurero() {
        HibernateTransactionRunner.runTrx {
            partyService.crear(party)
            partyService.agregarAventureroAParty(party.id()!!, aventurero)

            val aventureroRecuperado = aventureroService.recuperar(aventurero.id()!!)

            Assertions.assertThat(aventurero).usingRecursiveComparison().isEqualTo(aventureroRecuperado)
        }

    }

    @Test
    fun seActualizaUnAventurero() {
        HibernateTransactionRunner.runTrx {
            partyService.crear(party)
            partyService.agregarAventureroAParty(party.id()!!, aventurero)

            val aventureroDTO = AventureroDTO(aventurero.id()!!, 2, "Otro nombre", "/otra_imagen.jpg", listOf(), AtributosDTO(null, 1,2, 3, 4))
            aventurero.actualizarse(aventureroDTO)


            aventureroService.actualizar(aventurero)


            val aventureroRecuperado = aventureroService.recuperar(aventurero.id()!!)
            Assertions.assertThat(aventurero).usingRecursiveComparison().isEqualTo(aventureroRecuperado)
        }

    }

    @Test
    fun seEliminaUnAventurero() {
        HibernateTransactionRunner.runTrx {
            partyService.crear(party)
            partyService.agregarAventureroAParty(party.id()!!, aventurero)

            aventureroService.eliminar(aventurero)

            val exception = assertThrows<RuntimeException> { aventureroService.recuperar(aventurero.id()!!) }
            org.junit.jupiter.api.Assertions.assertEquals(exception.message, "No existe una entidad con ese id")
        }

    }

    @AfterEach
    fun tearDown() {
        partyDao.eliminarTodo()
    }


}

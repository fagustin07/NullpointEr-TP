package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.modelo.enums.Accion
import ar.edu.unq.epers.tactics.modelo.enums.Criterio
import ar.edu.unq.epers.tactics.modelo.enums.TipoDeEstadistica
import ar.edu.unq.epers.tactics.modelo.enums.TipoDeReceptor
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.service.AventureroLeaderboardService
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.PeleaService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AventureroLeaderboardServiceTest {
    val NOMBRE_DE_PARTY_ENEMIGA = "Nombre de party enemiga"
    val NOMBRE_DE_PEPE = "Pepe"
    val NOMBRE_DE_JUAN = "Juan"

    private lateinit var partyDAO: PartyDAO
    private lateinit var aventureroDAO: AventureroDAO
    private lateinit var peleaDAO: PeleaDAO

    private lateinit var aventureroService: AventureroService
    private lateinit var partyService: PartyService
    private lateinit var peleaService: PeleaService
    private lateinit var aventureroLeaderboardService: AventureroLeaderboardService

    @BeforeEach
    fun setUp() {
        aventureroDAO = HibernateAventureroDAO()
        partyDAO = HibernatePartyDAO()
        peleaDAO = HibernatePeleaDAO()

        partyService = PartyServiceImpl(partyDAO)
        aventureroService = AventureroServiceImpl(aventureroDAO, partyDAO)
        peleaService = PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO)
        aventureroLeaderboardService = AventureroLeaderboardServiceImpl(aventureroDAO)
    }

    @Test
    fun `no se puede pedir un buda a una party sin aventureros`() {
        val partyId = nuevaPartyPersistida()
        nuevoAventureroConTacticaEn(partyId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_PEPE)

        val exception = assertThrows<RuntimeException> { aventureroLeaderboardService.buda() }
        assertEquals("No entity found for query", exception.message)
    }

    @Test
    fun `no se puede pedir un buda a una party con aventureros si ninguno medito`() {
        val partyId = nuevaPartyPersistida()
        nuevoAventureroConTacticaEn(partyId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_PEPE)

        val exception = assertThrows<RuntimeException> { aventureroLeaderboardService.buda() }
        assertEquals("No entity found for query", exception.message)
    }

    @Test
    fun `una party con un solo aventurero tiene un buda si este medito alguna vez`() {
        val partyId = nuevaPartyPersistida()
        val aventurero = nuevoAventureroConTacticaEn(partyId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_PEPE)
        val peleaId = comenzarPeleaDe(partyId)

        val habilidadDeMeditacion = peleaService.resolverTurno(peleaId, aventurero.id()!!, listOf())

        peleaService.recibirHabilidad(aventurero.id()!!, habilidadDeMeditacion)
        val buda = aventureroLeaderboardService.buda()

        //assertThat(aventurero).usingRecursiveComparison().isEqualTo(buda)
        assertEquals(aventurero.nombre(), buda.nombre())
    }

    @Test
    fun `en una party en la cual varios de sus aventureros meditaron, el buda es aquel que lo haya hecho mas veces`() {
        val partyId = nuevaPartyPersistida()
        val pepe = nuevoAventureroConTacticaEn(partyId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_PEPE)
        val juan = nuevoAventureroConTacticaEn(partyId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_JUAN)
        val peleaId = comenzarPeleaDe(partyId)

        val habilidadDeMeditacionDePepe = peleaService.resolverTurno(peleaId, pepe.id()!!, listOf())
        val habilidadDeMeditacionDeJuan = peleaService.resolverTurno(peleaId, juan.id()!!, listOf())

        peleaService.recibirHabilidad(juan.id()!!, habilidadDeMeditacionDeJuan)
        peleaService.recibirHabilidad(juan.id()!!, habilidadDeMeditacionDeJuan)

        peleaService.recibirHabilidad(pepe.id()!!, habilidadDeMeditacionDePepe)

        val buda = aventureroLeaderboardService.buda()

        //assertThat(aventurero).usingRecursiveComparison().isEqualTo(buda)
        assertEquals(juan.nombre(), buda.nombre())
    }



    private fun comenzarPeleaDe(partyId: Long): Long {
        val peleaId = peleaService.iniciarPelea(partyId, NOMBRE_DE_PARTY_ENEMIGA).id()!!
        return peleaId
    }

    private fun nuevoAventureroConTacticaEn(partyId: Long, tactica: Tactica, nombreDeAventurero: String): Aventurero {
        val aventurero = Aventurero(nombreDeAventurero)

        aventurero.agregarTactica(tactica)
        partyService.agregarAventureroAParty(partyId, aventurero)

        return aventurero
    }

    private fun nuevaPartyPersistida(): Long {
        val party = Party("Nombre de party", "/party.jpg")
        val partyId = partyService.crear(party).id()!!
        return partyId
    }

    private fun tacticaDeMeditacionSobreUnoMismoVivo() =
        Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.MEDITAR)

    @AfterEach
    fun tearDown() {
        partyService.eliminarTodo()
    }
}
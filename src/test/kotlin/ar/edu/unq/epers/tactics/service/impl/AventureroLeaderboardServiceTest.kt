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
import org.assertj.core.api.Assertions.assertThat
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

    /* MEJOR GUERRERO */
    @Test
    fun `cuando existe un solo aventurero que realizo un ataque fisico alguna vez, ese es el mejor guerrero`() {
        val partyDePepeId = nuevaPartyPersistida()
        val pepe = nuevoAventureroConTacticaEn(partyDePepeId, tacticaDeAtaqueSobreEnemigoVivo(), NOMBRE_DE_PEPE)
        val peleaPartyDePepeId = comenzarPeleaDe(partyDePepeId)

        val partyDeJuanId = nuevaPartyPersistida()
        val juan = nuevoAventureroConTacticaEn(partyDeJuanId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_JUAN)
        comenzarPeleaDe(partyDeJuanId)

        peleaService.resolverTurno(peleaPartyDePepeId, pepe.id()!!, listOf(juan))


        val mejorGuerrero = aventureroLeaderboardService.mejorGuerrero()

        assertEquals(pepe.nombre(), mejorGuerrero.nombre())
    }

    @Test
    fun `ataqueeee 2222222`() {
        val partyDePepeId = nuevaPartyPersistida()
        val pepe = nuevoAventureroConTacticaEn(partyDePepeId, tacticaDeAtaqueSobreEnemigoVivo(), NOMBRE_DE_PEPE)
        val peleaPartyDePepeId = comenzarPeleaDe(partyDePepeId)

        val partyDeJuanId = nuevaPartyPersistida()
        val juan = nuevoAventureroConTacticaEn(partyDeJuanId, tacticaDeAtaqueSobreEnemigoVivo(), NOMBRE_DE_JUAN)
        val peleaPartyJuanId = comenzarPeleaDe(partyDeJuanId)

        peleaService.resolverTurno(peleaPartyDePepeId, pepe.id()!!, listOf(juan))

        peleaService.resolverTurno(peleaPartyJuanId, juan.id()!!, listOf(pepe))
        peleaService.resolverTurno(peleaPartyJuanId, juan.id()!!, listOf(pepe))


        val mejorGuerrero = aventureroLeaderboardService.mejorGuerrero()

        assertEquals(juan.nombre(), mejorGuerrero.nombre())
    }


    /* BUDA */
    @Test
    fun `cuando no hay ningun aventurero en ninguna party, no se puede pedir un buda`() {
        val partyId = nuevaPartyPersistida()
        nuevoAventureroConTacticaEn(partyId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_PEPE)

        val exception = assertThrows<RuntimeException> { aventureroLeaderboardService.buda() }
        assertEquals("No entity found for query", exception.message)
    }

    @Test
    fun `cuando existen aventureros, pero ninguno medito nunca, no se puede pedir un buda`() {
        val partyId = nuevaPartyPersistida()
        nuevoAventureroConTacticaEn(partyId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_PEPE)

        val exception = assertThrows<RuntimeException> { aventureroLeaderboardService.buda() }
        assertEquals("No entity found for query", exception.message)
    }

    @Test
    fun `cuando existe un solo aventurero que medito alguna vez, ese es el buda`() {
        val partyId = nuevaPartyPersistida()
        val aventurero = nuevoAventureroConTacticaEn(partyId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_PEPE)
        val peleaId = comenzarPeleaDe(partyId)

        peleaService.resolverTurno(peleaId, aventurero.id()!!, listOf())

        val buda = aventureroLeaderboardService.buda()

        assertThat(aventurero).usingRecursiveComparison().ignoringFields("party").isEqualTo(buda)
    }

    @Test
    fun `cuando existen varios aventureros que meditaron en distintas parties, el buda es aquel que lo haya hecho mas veces`() {
        val partyDePepeId = nuevaPartyPersistida()
        val pepe = nuevoAventureroConTacticaEn(partyDePepeId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_PEPE)
        val peleaPartyDePepeId = comenzarPeleaDe(partyDePepeId)
        val partyDeJuanId = nuevaPartyPersistida()
        val juan = nuevoAventureroConTacticaEn(partyDeJuanId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_JUAN)
        val peleaPartyDeJuanId = comenzarPeleaDe(partyDeJuanId)

        peleaService.resolverTurno(peleaPartyDePepeId, pepe.id()!!, listOf())

        val habilidadesEmitidasPorJuan = listOf(
            peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf()),
            peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf()),
            peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf())
        )

        //habilidadesEmitidasPorJuan.forEach { peleaService.recibirHabilidad(peleaPartyDeJuanId, ) }

        val buda = aventureroLeaderboardService.buda()

        assertThat(juan).usingRecursiveComparison().ignoringFields("party").isEqualTo(buda)
    }

    @Test
    fun `para encontrar al buda solo son tenidas en cuanta las RECEPCIONES de habilidades de meditacion`() {
        val partyDePepeId = nuevaPartyPersistida()
        val pepe = nuevoAventureroConTacticaEn(partyDePepeId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_PEPE)
        val peleaPartyDePepeId = comenzarPeleaDe(partyDePepeId)
        val partyDeJuanId = nuevaPartyPersistida()
        val juan = nuevoAventureroConTacticaEn(partyDeJuanId, tacticaDeMeditacionSobreUnoMismoVivo(), NOMBRE_DE_JUAN)
        val peleaPartyDeJuanId = comenzarPeleaDe(partyDeJuanId)

        peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf())
        peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf())
        peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf())


        val meditacionDePepe = peleaService.resolverTurno(peleaPartyDePepeId, pepe.id()!!, listOf())
        peleaService.recibirHabilidad(peleaPartyDePepeId, pepe.id()!!, meditacionDePepe)

        val buda = aventureroLeaderboardService.buda()

        assertThat(pepe).usingRecursiveComparison().ignoringFields("party").isEqualTo(buda)
    }

    @Test
    fun `cuando existen varios aventureros que hicieron ataques magicos, el mejorMago es aquel que inflingio mas daño magico`() {
        val partyDePepeId = nuevaPartyPersistida()
        val pepe = nuevoAventureroConTacticaEn(partyDePepeId, tacticaDeAtaqueMagico(), NOMBRE_DE_PEPE)
        val peleaPartyDePepeId = comenzarPeleaDe(partyDePepeId)
        val partyDeJuanId = nuevaPartyPersistida()
        val juan = nuevoAventureroConTacticaEn(partyDeJuanId, tacticaDeAtaqueMagico(), NOMBRE_DE_JUAN)
        val peleaPartyDeJuanId = comenzarPeleaDe(partyDeJuanId)

        peleaService.resolverTurno(peleaPartyDePepeId, pepe.id()!!, listOf(juan))

        peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf(pepe))
        peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf(pepe))
        peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf(pepe))

        val mejorMago = aventureroLeaderboardService.mejorMago()

        assertThat(juan).usingRecursiveComparison().ignoringFields("party","mana").isEqualTo(mejorMago)
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

    private fun tacticaDeCuracionUnoMismo() =
        Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.CURAR)

    private fun tacticaDeAtaqueMagico() =
        Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.ATAQUE_MAGICO)

    private fun tacticaDeMeditacionSobreUnoMismoVivo() =
        Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.MEDITAR)

    private fun tacticaDeAtaqueSobreEnemigoVivo() =
        Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.ATAQUE_FISICO)

    @AfterEach
    fun tearDown() {
        partyService.eliminarTodo()
    }
}
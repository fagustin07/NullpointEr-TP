package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.service.AventureroLeaderboardService
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.PeleaService
import helpers.FactoryAventureroLeaderboardService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AventureroLeaderboardServiceTest {
    lateinit var factory: FactoryAventureroLeaderboardService
    lateinit var aventureroLeaderboardService: AventureroLeaderboardService
    lateinit var partyService: PartyService
    lateinit var peleaService: PeleaService
    lateinit var aventureroService: AventureroService

    @BeforeEach
    fun setUp() {
        factory = FactoryAventureroLeaderboardService()
        aventureroLeaderboardService = factory.aventureroLeaderboardService()
        partyService = factory.partyService()
        peleaService = factory.peleaService()
        aventureroService = factory.aventureroService()
    }

    /* MEJOR GUERRERO */
    @Test
    fun `cuando existe un solo aventurero que realizo un ataque fisico alguna vez, ese es el mejor guerrero`() {
        val partyDePepeId = factory.nuevaPartyPersistida()
        val pepe = factory.nuevoGuerreroEn(partyDePepeId)
        val peleaPartyDePepeId = factory.comenzarPeleaDe(partyDePepeId)

        val partyDeJuanId = factory.nuevaPartyPersistida()
        val juan = factory.nuevoMeditadorEn(partyDeJuanId)

        peleaService.resolverTurno(peleaPartyDePepeId, pepe.id()!!, listOf(juan))

        val mejorGuerrero = aventureroLeaderboardService.mejorGuerrero()

        assertEquals(pepe.nombre(), mejorGuerrero.nombre())
    }

    @Test
    fun `cuando existen varios aventureros que emitieron ataques fisicos, el mejorGuerrero es aquel que emitió mas daño fisico`() {
        val partyDePepeId = factory.nuevaPartyPersistida()
        val pepe = factory.nuevoGuerreroEn(partyDePepeId)
        val peleaPartyDePepeId = factory.comenzarPeleaDe(partyDePepeId)

        val partyDeJuanId = factory.nuevaPartyPersistida()
        val juan = factory.nuevoGuerreroEn(partyDeJuanId)
        val peleaPartyJuanId = factory.comenzarPeleaDe(partyDeJuanId)

        peleaService.resolverTurno(peleaPartyDePepeId, pepe.id()!!, listOf(juan))

        peleaService.resolverTurno(peleaPartyJuanId, juan.id()!!, listOf(pepe))
        peleaService.resolverTurno(peleaPartyJuanId, juan.id()!!, listOf(pepe))


        val mejorGuerrero = aventureroLeaderboardService.mejorGuerrero()

        assertThat(juan)
            .usingRecursiveComparison()
            .ignoringFields("party")
            .isEqualTo(mejorGuerrero)
    }


    /* BUDA */
    @Test
    fun `cuando no hay ningun aventurero en ninguna party, no se puede pedir un buda`() {
        val partyId = factory.nuevaPartyPersistida()
        factory.nuevoCuranderoEgoistaEn(partyId)

        val exception = assertThrows<RuntimeException> { aventureroLeaderboardService.buda() }
        assertEquals("No entity found for query", exception.message)
    }

    @Test
    fun `cuando existen aventureros, pero ninguno medito nunca, no se puede pedir un buda`() {
        val partyId = factory.nuevaPartyPersistida()
        factory.nuevoMeditadorEn(partyId)

        val exception = assertThrows<RuntimeException> { aventureroLeaderboardService.buda() }
        assertEquals("No entity found for query", exception.message)
    }

    @Test
    fun `cuando existe un solo aventurero que medito alguna vez, ese es el buda`() {
        val partyId = factory.nuevaPartyPersistida()
        val aventurero = factory.nuevoMeditadorEn(partyId)
        val peleaId = factory.comenzarPeleaDe(partyId)

        val habilidad = peleaService.resolverTurno(peleaId, aventurero.id()!!, listOf())
        val aventureroActualizado = peleaService.recibirHabilidad(peleaId, aventurero.id()!!, habilidad)

        val buda = aventureroLeaderboardService.buda()

        assertThat(aventureroActualizado)
            .usingRecursiveComparison()
            .ignoringFields("party")
            .isEqualTo(buda)
    }

    @Test
    fun `cuando existen varios aventureros que meditaron en distintas parties, el buda es aquel que lo haya hecho mas veces`() {
        val partyDePepeId = factory.nuevaPartyPersistida()
        val pepe = factory.nuevoMeditadorEn(partyDePepeId)
        val peleaPartyDePepeId = factory.comenzarPeleaDe(partyDePepeId)
        val partyDeJuanId = factory.nuevaPartyPersistida()
        var juan = factory.nuevoMeditadorEn(partyDeJuanId)
        val peleaPartyDeJuanId = factory.comenzarPeleaDe(partyDeJuanId)

        peleaService.resolverTurno(peleaPartyDePepeId, pepe.id()!!, listOf())

        val habilidadesEmitidasPorJuan = listOf(
            peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf()),
            peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf()),
            peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf())
        )

        habilidadesEmitidasPorJuan.forEach { juan = peleaService.recibirHabilidad(peleaPartyDeJuanId, juan.id()!!, it) }

        val buda = aventureroLeaderboardService.buda()

        assertThat(juan)
            .usingRecursiveComparison()
            .ignoringFields("party")
            .isEqualTo(buda)
    }

    @Test
    fun `para encontrar al buda solo son tenidas en cuanta las RECEPCIONES de habilidades de meditacion`() {
        val partyDePepeId = factory.nuevaPartyPersistida()
        val pepe = factory.nuevoMeditadorEn(partyDePepeId)
        val peleaPartyDePepeId = factory.comenzarPeleaDe(partyDePepeId)
        val partyDeJuanId = factory.nuevaPartyPersistida()
        val juan = factory.nuevoMeditadorEn(partyDeJuanId)
        val peleaPartyDeJuanId = factory.comenzarPeleaDe(partyDeJuanId)

        peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf())
        peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf())
        peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf())


        val meditacionDePepe = peleaService.resolverTurno(peleaPartyDePepeId, pepe.id()!!, listOf())
        val pepeActualizado = peleaService.recibirHabilidad(peleaPartyDePepeId, pepe.id()!!, meditacionDePepe)

        val buda = aventureroLeaderboardService.buda()

        assertThat(pepeActualizado)
            .usingRecursiveComparison()
            .ignoringFields("party")
            .isEqualTo(buda)
    }

    /* MEJOR MAGO */
    @Test
    fun `cuando existen varios aventureros que emitieron ataques magicos, el mejorMago es aquel que emitió mas daño magico`() {
        val partyDePepeId = factory.nuevaPartyPersistida()
        val pepe = factory.nuevoMagoEn(partyDePepeId)
        val peleaPartyDePepeId = factory.comenzarPeleaDe(partyDePepeId)
        val partyDeJuanId = factory.nuevaPartyPersistida()
        val juan = factory.nuevoMagoEn(partyDeJuanId)
        val peleaPartyDeJuanId = factory.comenzarPeleaDe(partyDeJuanId)

        peleaService.resolverTurno(peleaPartyDePepeId, pepe.id()!!, listOf(juan))

        peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf(pepe))
        peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf(pepe))
        peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf(pepe))

        val mejorMago = aventureroLeaderboardService.mejorMago()

        val juanActualizado = aventureroService.recuperar(juan.id()!!)
        assertThat(juanActualizado)
            .usingRecursiveComparison()
            .ignoringFields("party")
            .isEqualTo(mejorMago)
    }

    /* CURANDERO */
    @Test
    fun `cuando existen varios aventureros que hicieron curaciones, el mejorCurandero es aquel que realizó mas puntos de curacion`() {
        val partyDePepeId = factory.nuevaPartyPersistida()
        val pepeModelo = Aventurero("Poderoso", "", 80.0, 80.0, 80.0, 80.0)
        var pepe = factory.aventureroEn(partyDePepeId, factory.tacticaDeCuracionUnoMismo(), pepeModelo)
        val peleaPartyDePepeId = factory.comenzarPeleaDe(partyDePepeId)

        val partyDeJuanId = factory.nuevaPartyPersistida()
        val juan = factory.nuevoCuranderoEgoistaEn(partyDeJuanId)
        val peleaPartyDeJuanId = factory.comenzarPeleaDe(partyDeJuanId)
        val curacion = peleaService.resolverTurno(peleaPartyDePepeId, pepe.id()!!, listOf())

        val habilidadesEmitidasPorJuan = listOf(
            peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf()),
            peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf()),
            peleaService.resolverTurno(peleaPartyDeJuanId, juan.id()!!, listOf())
        )
        habilidadesEmitidasPorJuan.forEach { peleaService.recibirHabilidad(peleaPartyDeJuanId, juan.id()!!, it) }
        pepe = peleaService.recibirHabilidad(peleaPartyDePepeId, pepe.id()!!, curacion)

        val mejorCurandero = aventureroLeaderboardService.mejorCurandero()

        assertThat(pepe)
            .usingRecursiveComparison()
            .ignoringFields("party")
            .isEqualTo(mejorCurandero)
    }

    @AfterEach
    fun tearDown() {
        partyService.eliminarTodo()
    }
}
package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.modelo.enums.Accion
import ar.edu.unq.epers.tactics.modelo.enums.Criterio
import ar.edu.unq.epers.tactics.modelo.enums.TipoDeEstadistica
import ar.edu.unq.epers.tactics.modelo.enums.TipoDeReceptor
import ar.edu.unq.epers.tactics.modelo.habilidades.Ataque
import ar.edu.unq.epers.tactics.modelo.habilidades.Curacion
import ar.edu.unq.epers.tactics.modelo.habilidades.Meditacion
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PeleaServiceTest {
    val peleaDAO = HibernatePeleaDAO()
    val partyDAO = HibernatePartyDAO()
    val aventureroDAO = HibernateAventureroDAO()
    val peleaService = PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO)
    val partyService = PersistentPartyService(partyDAO)
    lateinit var party: Party

    @BeforeEach
    fun setUp() {
        party = Party("Los geniales", "URL")
        partyService.crear(party)
    }

    @Test
    fun `una party inicialmente no esta en una pelea`() {
        assertFalse(peleaService.estaEnPelea(party.id()!!))
    }

    @Test
    fun `una party puede comenzar una pelea`() {
        peleaService.iniciarPelea(party.id()!!)

        assertTrue(peleaService.estaEnPelea(party.id()!!))
    }

    @Test
    fun `una party que esta en pelea no puede entrar en otra`() {
        peleaService.iniciarPelea(party.id()!!)

        val exception = assertThrows<RuntimeException> { peleaService.iniciarPelea(party.id()!!) }
        assertThat(exception.message).isEqualTo("No se puede iniciar una pelea: la party ya esta peleando")
    }

    @Test
    fun `un aventurero sabe resolver su turno`() {
        val curador = Aventurero("Fede", "", 10, 10, 10, 10)
        val aliado = Aventurero("Jorge", "", 10, 10, 10, 10)
        val manaOriginal = curador.mana()

        curador.agregarTactica(Tactica(1, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.CURAR))
        curador.agregarTactica(Tactica(2, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 3, Accion.CURAR))

        partyService.agregarAventureroAParty(party.id()!!, curador)
        partyService.agregarAventureroAParty(party.id()!!, aliado)

        val pelea = peleaService.iniciarPelea(party.id()!!)
        val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, curador.id()!!, listOf())

        runTrx {
            val curadorLuegoDeResolverTurno = aventureroDAO.recuperar(curador.id()!!)
            assertThat(curadorLuegoDeResolverTurno.mana()).isEqualTo(manaOriginal - 5)
            assertTrue(habilidadGenerada is Curacion)
        }
    }

    @Test
    fun `un aventurero elige una tactica que ataca a un enemigo`() {
        val atacante = Aventurero("Fede", "", 10, 10, 10, 10)
        val enemigo = Aventurero("Jorge", "", 20, 20, 20, 20)
        val enemigos = listOf(enemigo)
        val tactica = Tactica(
            1,
            TipoDeReceptor.ENEMIGO,
            TipoDeEstadistica.VIDA,
            Criterio.MENOR_QUE,
            9999,
            Accion.ATAQUE_FISICO
        )
        atacante.agregarTactica(tactica)

        partyService.agregarAventureroAParty(party.id()!!, atacante)

        val pelea = peleaService.iniciarPelea(party.id()!!)
        val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, atacante.id()!!, enemigos) as Ataque

        assertThat(habilidadGenerada.aventureroReceptor.id()).isEqualTo(enemigo.id()) // TODO: enemigo no esta en ninguna party. Su id es null
    }


    @Test
    fun `un aventurero resuelve su turno buscando la tactica que cumpla su criterio dependiendo de la prioridad `() {
        val aventurero = Aventurero("Fede", "", 10, 10, 10, 10)
        val tactica1 = Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 9999, Accion.ATAQUE_FISICO)
        val tactica2 = Tactica(2, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.MEDITAR)
        aventurero.agregarTactica(tactica1)
        aventurero.agregarTactica(tactica2)

        partyService.agregarAventureroAParty(party.id()!!, aventurero)

        val pelea = peleaService.iniciarPelea(party.id()!!)
        val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, aventurero.id()!!, listOf()) as Meditacion

        assertThat(habilidadGenerada.aventureroReceptor.id()).isEqualTo(aventurero.id()!!)
    }


    @Test
    fun `un aventurero que resuelve su turno ejecuta la habilidad de curar sobre otro y este recibe la habilidad`() {
        val curador = Aventurero("Fede", "", 10, 10, 10, 10)
        val aliado = Aventurero("Jorge", "", 10, 10, 10, 10)
        val vidaAntesDeCuracion = aliado.vida()
        val tactica = Tactica(
            1, TipoDeReceptor.ALIADO,
            TipoDeEstadistica.VIDA,
            Criterio.MAYOR_QUE, 0, Accion.CURAR
        )

        curador.agregarTactica(tactica)

        partyService.agregarAventureroAParty(party.id()!!, curador)
        partyService.agregarAventureroAParty(party.id()!!, aliado)

        val pelea = peleaService.iniciarPelea(party.id()!!)
        val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, curador.id()!!, listOf())
        val aliadoQueRecibiraHabilidad = peleaService.recibirHabilidad(aliado.id()!!, habilidadGenerada)

        val vidaEsperada = vidaAntesDeCuracion + curador.poderMagico()
        assertThat(aliado.id()!!).isEqualTo(aliadoQueRecibiraHabilidad.id())
        assertEquals(vidaEsperada, aliadoQueRecibiraHabilidad.vida())
    }

    @Test
    fun `una party puede salir de una pelea`() {
        peleaService.iniciarPelea(party.id()!!)

        peleaService.terminarPelea(party.id()!!)

        assertFalse(peleaService.estaEnPelea(party.id()!!))
    }

    @Test
    fun `una party no puede salir de una pelea si no esta en ninguna`() {
        val exception = assertThrows<RuntimeException> { peleaService.terminarPelea(party.id()!!) }
        assertThat(exception).hasMessage("La party no esta en ninguna pelea")
        assertFalse(peleaService.estaEnPelea(party.id()!!))
    }

    @Test
    fun `luego de una pelea, los aventureros vuelven a sus puntajes iniciales`() {
        val curador = Aventurero("Fede", "", 10, 10, 10, 10)
        val aliado = Aventurero("Jorge", "", 10, 10, 10, 10)
        val vidaAntesDeCuracion = aliado.vida()
        val manaAntesDeCuracion = curador.mana()
        val tactica = Tactica(
            1, TipoDeReceptor.ALIADO,
            TipoDeEstadistica.VIDA,
            Criterio.MAYOR_QUE, 0, Accion.CURAR
        )

        curador.agregarTactica(tactica)

        partyService.agregarAventureroAParty(party.id()!!, curador)
        partyService.agregarAventureroAParty(party.id()!!, aliado)

        val pelea = peleaService.iniciarPelea(party.id()!!)
        val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, curador.id()!!, listOf())
        peleaService.recibirHabilidad(aliado.id()!!, habilidadGenerada)

        peleaService.terminarPelea(party.id()!!)
        runTrx {
            assertThat(this.aventureroDAO.recuperar(curador.id()!!).mana()).isEqualTo(manaAntesDeCuracion)
            assertThat(this.aventureroDAO.recuperar(aliado.id()!!).vida()).isEqualTo(vidaAntesDeCuracion)
        }

    }

    @Test
    fun `cuando una party estuvo en multiples peleas se retorna la ultima`() {
        peleaService.iniciarPelea(party.id()!!)
        peleaService.terminarPelea(party.id()!!)

        peleaService.iniciarPelea(party.id()!!)
        val ultimaPelea = peleaService.terminarPelea(party.id()!!)
        runTrx {
            assertThat(peleaDAO.recuperarUltimaPeleaDeParty(party.id()!!).id()).isEqualTo(ultimaPelea.id())
        }
    }
    
    @Test
    fun `cuando se recupera la party de un aventurero que tenga varias tacticas, este aparece una sola vez`() {
        val party = Party("Nombre de Party", "/img.jpg")
        val aventurero = Aventurero("Nombre de Aventurero", "", 1, 2, 3, 4)
        aventurero.agregarTactica(Tactica(1, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.CURAR))
        aventurero.agregarTactica(Tactica(2, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.CURAR))

        val partyID = partyService.crear(party).id()!!
        partyService.agregarAventureroAParty(partyID, aventurero)

        runTrx {
            val partyRecuperada = partyService.recuperar(partyID)
            assertEquals(1, partyRecuperada.numeroDeAventureros())
        }
    }

    @AfterEach
    fun tearDown() {
        partyDAO.eliminarTodo()
    }
}
package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.modelo.habilidades.Ataque
import ar.edu.unq.epers.tactics.modelo.habilidades.Curacion
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.modelo.habilidades.Meditacion
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.dto.Criterio
import ar.edu.unq.epers.tactics.service.dto.TipoDeEstadistica
import ar.edu.unq.epers.tactics.service.dto.TipoDeReceptor
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
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
    lateinit var party : Party

    @BeforeEach
    fun setUp(){
        party = Party("Los geniales", "URL")
        HibernateTransactionRunner.runTrx {
            partyDAO.crear(party)
        }
    }
    @Test
    fun `una party inicialmente no esta en una pelea`() {
        HibernateTransactionRunner.runTrx {
            assertFalse(peleaService.estaEnPelea(party.id()!!))
        }
    }

    @Test
    fun `una party puede comenzar una pelea`() {
        HibernateTransactionRunner.runTrx {
            peleaService.iniciarPelea(party.id()!!)
        }

        HibernateTransactionRunner.runTrx {
            assertTrue(peleaService.estaEnPelea(party.id()!!))
        }
    }

    @Test
    fun `una party que esta en pelea no puede entrar en otra`() {
        val peleaDAO = HibernatePeleaDAO()
        val partyDAO = HibernatePartyDAO()
        val aventureroDAO = HibernateAventureroDAO()
        val peleaService = PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO)
        HibernateTransactionRunner.runTrx {
            peleaService.iniciarPelea(party.id()!!)
        }

        HibernateTransactionRunner.runTrx {
            val exception = assertThrows<RuntimeException> { peleaService.iniciarPelea(party.id()!!) }
            assertThat(exception.message).isEqualTo("No se puede iniciar una pelea: la party ya esta peleando")
        }
    }

    @Test
    fun `un aventurero sabe resolver su turno`() {
        val curador = Aventurero("Fede", "",10, 10, 10, 10)
        val aliado = Aventurero("Jorge", "",10, 10, 10, 10)
        val manaOriginal = curador.mana()
        val tactica = Tactica(1,TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA,
                                                    Criterio.MAYOR_QUE,0,Accion.CURAR)
        curador.agregarTactica(tactica)

        HibernateTransactionRunner.runTrx {
            party.agregarUnAventurero(curador)
            party.agregarUnAventurero(aliado)
            partyDAO.actualizar(party)
        }

        lateinit var pelea : Pelea
        lateinit var habilidadGenerada : Habilidad
        HibernateTransactionRunner.runTrx {
            pelea = peleaService.iniciarPelea(party.id()!!)
        }
        HibernateTransactionRunner.runTrx {
            habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, curador.id()!!, listOf())
        }
        lateinit var curadorLuegoDeResolverTurno : Aventurero
        HibernateTransactionRunner.runTrx {
            curadorLuegoDeResolverTurno = aventureroDAO.recuperar(curador.id()!!)
        }

        assertThat(curadorLuegoDeResolverTurno.mana()).isEqualTo(manaOriginal - 5)
        assertTrue(habilidadGenerada is Curacion)
    }

    @Test
    fun `un aventurero elige una tactica que ataca a un enemigo`() {
        val atacante = Aventurero("Fede", "", 10, 10, 10, 10)
        val enemigo = Aventurero("Jorge", "", 10, 10, 10, 10)
        val enemigos = listOf(enemigo)
        val tactica = Tactica(1,
            TipoDeReceptor.ENEMIGO,
            TipoDeEstadistica.VIDA,
            Criterio.MENOR_QUE,
            9999,
            Accion.ATAQUE_FISICO
        )
        atacante.agregarTactica(tactica)
        HibernateTransactionRunner.runTrx {
            party.agregarUnAventurero(atacante)
            partyDAO.actualizar(party)
        }

        lateinit var pelea: Pelea
        lateinit var habilidadGenerada: Ataque
        HibernateTransactionRunner.runTrx {
            pelea = peleaService.iniciarPelea(party.id()!!)
        }
        HibernateTransactionRunner.runTrx {
            habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, atacante.id()!!, enemigos) as Ataque
        }

        assertThat(habilidadGenerada.aventureroReceptor.id()).isEqualTo(enemigo.id())
    }


    @Test
    fun `un aventurero resuelve su turno buscando la tactica que cumpla su criterio dependiendo de la prioridad `(){
        val aventurero = Aventurero("Fede","", 10, 10, 10, 10)
        val tactica1 = Tactica(1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MENOR_QUE,9999,Accion.ATAQUE_FISICO)
        val tactica2 = Tactica(2,TipoDeReceptor.UNO_MISMO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.MEDITAR)
        aventurero.agregarTactica(tactica1)
        aventurero.agregarTactica(tactica2)
        HibernateTransactionRunner.runTrx {
            party.agregarUnAventurero(aventurero)
            partyDAO.actualizar(party)
        }

        lateinit var pelea: Pelea
        lateinit var habilidadGenerada: Meditacion
        HibernateTransactionRunner.runTrx {
            pelea = peleaService.iniciarPelea(party.id()!!)
        }
        HibernateTransactionRunner.runTrx {
            habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, aventurero.id()!!, listOf()) as Meditacion
        }

        assertThat(habilidadGenerada.aventureroReceptor.id()).isEqualTo(aventurero.id())
    }


    @Test
    fun `un aventurero que resuelve su turno ejecuta la habilidad de curar sobre otro y este recibe la habilidad`() {
        val curador = Aventurero("Fede", "",10, 10, 10, 10)
        val aliado = Aventurero("Jorge", "",10, 10, 10, 10)
        val vidaAntesDeCuracion = aliado.vida()
        val tactica = Tactica(1,TipoDeReceptor.ALIADO,
                TipoDeEstadistica.VIDA,
                Criterio.MAYOR_QUE,0,Accion.CURAR)

        curador.agregarTactica(tactica)

        HibernateTransactionRunner.runTrx {
            party.agregarUnAventurero(curador)
            party.agregarUnAventurero(aliado)
            partyDAO.actualizar(party)
        }

        lateinit var pelea : Pelea
        lateinit var habilidadGenerada : Habilidad
        lateinit var aliadoQueRecibiraHabilidad:Aventurero
        HibernateTransactionRunner.runTrx {
            pelea = peleaService.iniciarPelea(party.id()!!)
        }
        HibernateTransactionRunner.runTrx {
            habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, curador.id()!!, listOf())
        }
        HibernateTransactionRunner.runTrx {
            aliadoQueRecibiraHabilidad = peleaService.recibirHabilidad(aliado.id()!!, habilidadGenerada)
        }
        val vidaEsperada = vidaAntesDeCuracion + curador.poderMagico()
        assertThat(aliado.id()).isEqualTo(aliadoQueRecibiraHabilidad.id())
        assertEquals(vidaEsperada,aliadoQueRecibiraHabilidad.vida())

    }

    @Test
    fun `una party puede salir de una pelea`() {
        HibernateTransactionRunner.runTrx {
            peleaService.iniciarPelea(party.id()!!)

            peleaService.terminarPelea(party.id()!!)

            assertFalse(peleaService.estaEnPelea(party.id()!!))
        }
    }

    @Test
    fun `una party no puede salir de una pelea si no esta en ninguna`() {
        HibernateTransactionRunner.runTrx {
            val exception = assertThrows<RuntimeException> { peleaService.terminarPelea(party.id()!!) }
            assertThat(exception).hasMessage("La party no esta en ninguna pelea")
            assertFalse(peleaService.estaEnPelea(party.id()!!))
        }
    }

    @AfterEach
    fun tearDown() {
        partyDAO.eliminarTodo()
    }
}
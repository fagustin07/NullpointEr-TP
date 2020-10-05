package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.modelo.habilidades.Ataque
import ar.edu.unq.epers.tactics.modelo.habilidades.Curacion
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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PeleaServiceTest {
    val peleaDAO = HibernatePeleaDAO()
    val partyDAO = HibernatePartyDAO()
    val aventureroDAO = HibernateAventureroDAO()
    val peleaService = PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO)
    val party = Party("Los geniales", "URL")

    @Test
    fun `una party inicialmente no esta en una pelea`() {
        val party = Party("Los geniales", "URL")
        HibernateTransactionRunner.runTrx {
            partyDAO.crear(party)

            assertFalse(peleaService.estaEnPelea(party.id()!!))
        }
    }

    @Test
    fun `una party puede comenzar una pelea`() {
        HibernateTransactionRunner.runTrx {
            partyDAO.crear(party)

            peleaService.iniciarPelea(party.id()!!)

            assertTrue(peleaService.estaEnPelea(party.id()!!))
        }
    }

    @Test
    fun `una party que esta en pelea no puede entrar en otra`() {
        val peleaDAO = HibernatePeleaDAO()
        val partyDAO = HibernatePartyDAO()
        val aventureroDAO = HibernateAventureroDAO()
        val peleaService = PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO)

        val party = Party("Los geniales", "URL")
        HibernateTransactionRunner.runTrx {
            partyDAO.crear(party)

            peleaService.iniciarPelea(party.id()!!)

            val exception = assertThrows<RuntimeException> { peleaService.iniciarPelea(party.id()!!) }
            assertThat(exception.message).isEqualTo("No se puede iniciar una pelea: la party ya esta peleando")
        }
    }

    @Test
    fun `un aventurero sabe resolver su turno`() {
        val curador = Aventurero("Fede", "",10, 10, 10, 10)
        val aliado = Aventurero("Jorge", "",10, 10, 10, 10)
        val manaAnterior = curador.mana()
        val tac = Tactica(1,TipoDeReceptor.ALIADO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.CURAR)
        curador.agregarTactica(tac)
        party.agregarUnAventurero(curador)
        party.agregarUnAventurero(aliado)

        HibernateTransactionRunner.runTrx {
            partyDAO.crear(party)
            val pelea = peleaService.iniciarPelea(party.id()!!)
            val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, curador.id()!!, listOf())

            assertThat(curador.mana()).isEqualTo(manaAnterior - 5)
            assertTrue(habilidadGenerada is Curacion)
        }
    }

    @Test
    fun `un aventurero elige una tactica que ataca a un enemigo`(){
        val atacante = Aventurero("Fede","", 10, 10, 10, 10)
        val enemigo = Aventurero("Jorge","", 10, 10, 10, 10)
        val enemigos = listOf(enemigo)
        val tac = Tactica(1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MENOR_QUE,9999,Accion.ATAQUE_FISICO)
        atacante.agregarTactica(tac)
        party.agregarUnAventurero(atacante)

        HibernateTransactionRunner.runTrx {
            partyDAO.crear(party)
            val pelea = peleaService.iniciarPelea(party.id()!!)
            val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, atacante.id()!!, enemigos) as Ataque

            assertThat(habilidadGenerada.aventureroReceptor).isEqualTo(enemigo)
        }
    }

    @Test
    fun `un aventurero resuelve su turno buscando la tactica que cumpla su criterio dependiendo de la prioridad `(){
        val aventurero = Aventurero("Fede","", 10, 10, 10, 10)
        val tactica1 = Tactica(1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MENOR_QUE,9999,Accion.ATAQUE_FISICO)
        val tactica2 = Tactica(2,TipoDeReceptor.UNO_MISMO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.MEDITAR)
        aventurero.agregarTactica(tactica1)
        aventurero.agregarTactica(tactica2)
        party.agregarUnAventurero(aventurero)

        HibernateTransactionRunner.runTrx {
            partyDAO.crear(party)
            val pelea = peleaService.iniciarPelea(party.id()!!)
            val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, aventurero.id()!!, listOf()) as Meditacion

            assertThat(habilidadGenerada.aventureroReceptor).isEqualTo(aventurero)
        }
    }

    @AfterEach
    fun tearDown() {
        partyDAO.eliminarTodo()
    }
}
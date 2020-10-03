package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PeleaServiceTest{

    @Test
    fun `una party inicialmente no esta en una pelea`() {
        val peleaDAO = HibernatePeleaDAO()
        val partyDAO = HibernatePartyDAO()
        val aventureroDAO = HibernateAventureroDAO()
        val peleaService = PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO)

        val party = Party("Los geniales", "URL")
        HibernateTransactionRunner.runTrx {
            partyDAO.crear(party)

            assertFalse(peleaService.estaEnPelea(party.id()!!))
        }
    }

    @Test
    fun `una party puede comenzar una pelea`() {
        val peleaDAO = HibernatePeleaDAO()
        val partyDAO = HibernatePartyDAO()
        val aventureroDAO = HibernateAventureroDAO()
        val peleaService = PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO)

        val party = Party("Los geniales", "URL")
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
    fun xxx() {
        val peleaDAO = HibernatePeleaDAO()
        val partyDAO = HibernatePartyDAO()
        val aventureroDAO = HibernateAventureroDAO()
        val peleaService = PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO)
        val party = Party("Los geniales", "URL")
        val curador = Aventurero("Fede", 10, 10, 10, 10)
        val aliado = Aventurero("Jorge", 10, 10, 10, 10)
        val manaAnterior = curador.mana()
        party.agregarUnAventurero(curador)

        HibernateTransactionRunner.runTrx {
            partyDAO.crear(party)
            val pelea = peleaService.iniciarPelea(party.id()!!)

            peleaService.resolverTurno(pelea.id()!!, curador.id()!!, listOf())

            assertThat(curador.mana()).isEqualTo(manaAnterior - 5)
        }

    }
}
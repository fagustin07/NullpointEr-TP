package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.exceptions.*
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PeleaServiceImpl
import ar.edu.unq.epers.tactics.service.impl.TiendaServicePersistente
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TiendaServiceTest {

    private val peleaService = PeleaServiceImpl(HibernatePeleaDAO(), HibernatePartyDAO(), HibernateAventureroDAO())
    private val partyService = PartyServiceImpl(HibernatePartyDAO())
    val tiendaService = TiendaServicePersistente(OrientDBPartyDAO(), OrientDBItemDAO())
    lateinit var party : Party

    @BeforeEach
    fun setUp(){
        party = partyService.crear(Party("Memories",""))
    }

    @Test
    fun `se pueden registar partys`(){
        val miParty = tiendaService.recuperarParty(party.nombre())

        assertThat(miParty.monedas).isEqualTo(0)
    }

    @Test
    fun `no se puede registrar una party con un id existente`(){
        val exception = assertThrows<PartyAlreadyRegisteredException> { partyService.crear(party) }
        assertThat(exception.message).isEqualTo("La party ${party.nombre()} ya está en el sistema.")
    }

    @Test
    fun `no se puede recuperar una party con un id sin registrar`(){
        val exception = assertThrows<PartyNotRegisteredException> { tiendaService.recuperarParty("Los del fuego") }
        assertThat(exception.message).isEqualTo("No exite una party llamada Los del fuego en el sistema.")
    }

    @Test
    fun `no se puede recuperar un item con un nombre sin registrar`(){
        val exception = assertThrows<InexistentItemException> { tiendaService.recuperarItem("Lanzallamas") }
        assertThat(exception.message).isEqualTo("No existe el item llamado Lanzallamas.")
    }

    @Test
    fun `no se puede registrar un item con un nombre ya existente`(){
        tiendaService.registrarItem("capa en llamas",400)

        val exception = assertThrows<ItemAlreadyRegisteredException> {
            tiendaService.registrarItem("capa en llamas",400)
        }
        assertThat(exception.message).isEqualTo("El item capa en llamas ya se encuentra en el sistema.")
    }

    @Test
    fun `se levanta una excepcion al querer comprar un item de mas valor que las monedas de la party`(){
        tiendaService.registrarItem("bandera flameante", 10)

        val exception = assertThrows<CannotBuyException> { tiendaService.registrarCompra(party.nombre(),"bandera flameante") }
        assertThat(exception.message).isEqualTo("No puedes comprar 'bandera flameante', te faltan 10 monedas.")
    }

    @Test
    fun `perder una pelea no incrementa el dinero de una party`(){
        val partyMonedas = tiendaService.recuperarParty(party.nombre())
        val monedasAntesDePelea = partyMonedas.monedas
        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)

        val partyLuegoDePelea = tiendaService.recuperarParty(party.nombre())
        assertThat(partyLuegoDePelea.monedas).isEqualTo(monedasAntesDePelea)
    }

    @Test
    fun `ganar una pelea incrementa el dinero de una party`(){
        val aliado = Aventurero("Jorge")
        partyService.agregarAventureroAParty(party.id()!!,aliado)

        val monedasAntesDeGanarPelea = tiendaService.recuperarParty(party.nombre()).monedas
        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)

        val partyLuegoDePelea = tiendaService.recuperarParty(party.nombre())
        val recompensaPorGanarPelea = 500
        val monedasEsperadas = monedasAntesDeGanarPelea + recompensaPorGanarPelea

        assertThat(partyLuegoDePelea.monedas).isEqualTo(monedasEsperadas)
    }

    @Test
    fun `party compra item`(){
        val aliado = Aventurero("Jorge")
        partyService.agregarAventureroAParty(party.id()!!,aliado)

        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)

        val monedasAntesDeCompra = tiendaService.recuperarParty(party.nombre()).monedas
        tiendaService.registrarItem("bandera flameante", 200)

        tiendaService.registrarCompra(party.nombre(),"bandera flameante")


        val partyRecuperada = tiendaService.recuperarParty(party.nombre())
        val precioItem = tiendaService.recuperarItem("bandera flameante").precio

        assertThat(partyRecuperada.monedas).isEqualTo(monedasAntesDeCompra - precioItem)
    }

    @AfterEach
    fun tearDown(){
        OrientDBDataDAO().clear()

        HibernateTransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }
}
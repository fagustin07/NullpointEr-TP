package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.exceptions.*
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PeleaServiceImpl
import ar.edu.unq.epers.tactics.service.impl.TiendaServicePersistente
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TiendaServiceTest {

    private val peleaService = PeleaServiceImpl(HibernatePeleaDAO(), HibernatePartyDAO(), HibernateAventureroDAO())
    private val partyService = PartyServiceImpl(HibernatePartyDAO())
    val tiendaService = TiendaServicePersistente(OrientDBPartyDAO(), OrientDBItemDAO())

    @Test
    fun `se pueden registar partys`(){
        tiendaService.registrarParty(1, 500)

        val miParty = tiendaService.recuperarParty(1)

        assertThat(miParty.monedas).isEqualTo(500) //assert malisimo xd
    }

    @Test
    fun `no se puede registrar una party con un id existente`(){
        tiendaService.registrarParty(1,400)

        val exception = assertThrows<PartyAlreadyRegisteredException> { tiendaService.registrarParty(1,400) }
        assertThat(exception.message).isEqualTo("La party 1 ya está en el sistema.")
    }

    @Test
    fun `no se puede recuperar una party con un id sin registrar`(){
        val exception = assertThrows<PartyUnregisteredException> { tiendaService.recuperarParty(6555) }
        assertThat(exception.message).isEqualTo("La party con id 6555 no se encuentra en el sistema.")
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
    fun `party compra item`(){
        val monedasAntesDeCompra = 500
        val precioItem = 200

        tiendaService.registrarParty(1, monedasAntesDeCompra)
        tiendaService.registrarItem("bandera flameante", precioItem)

        tiendaService.registrarCompra(1,"bandera flameante")


        val partyRecuperada = tiendaService.recuperarParty(1)

        assertThat(partyRecuperada.monedas).isEqualTo(monedasAntesDeCompra - precioItem)
    }

    @Test
    fun `se levanta una excepcion al querer comprar un item de mas valor que las monedas de la party`(){
        tiendaService.registrarParty(1,8)
        tiendaService.registrarItem("bandera flameante", 10)

        val exception = assertThrows<CannotBuyException> { tiendaService.registrarCompra(1,"bandera flameante") }
        assertThat(exception.message).isEqualTo("No puedes comprar 'bandera flameante', te faltan 2 monedas.")
    }

    @Test
    fun `perder una pelea no incrementa el dinero de una party`(){
        val party = Party("Nombre", "")
        partyService.crear(party)
        val partyMonedas = tiendaService.registrarParty(party.id()!!, 0)
        val monedasAntesDePelea = partyMonedas.monedas
        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)

        val partyLuegoDePelea = tiendaService.recuperarParty(party.id()!!)
        assertThat(partyLuegoDePelea.monedas).isEqualTo(monedasAntesDePelea)
    }

    @Test
    fun `ganar una pelea incrementa el dinero de una party`(){
        val party = Party("Nombre", "")
        val aliado = Aventurero("Jorge")
        party.agregarUnAventurero(aliado)
        partyService.crear(party)
        val partyMonedas = tiendaService.registrarParty(party.id()!!, 0)

        val monedasAntesDeGanarPelea = partyMonedas.monedas
        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)

        val partyLuegoDePelea = tiendaService.recuperarParty(party.id()!!)
        val recompensaPorGanarPelea = 500
        val monedasEsperadas = monedasAntesDeGanarPelea + recompensaPorGanarPelea

        assertThat(partyLuegoDePelea.monedas).isEqualTo(monedasEsperadas)
    }

    @AfterEach
    fun tearDown(){
        OrientDBDataDAO().clear()
    }
}
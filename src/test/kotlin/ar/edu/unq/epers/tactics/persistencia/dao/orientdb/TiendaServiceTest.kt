package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.exceptions.*
import ar.edu.unq.epers.tactics.modelo.tienda.Compra
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
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner
import helpers.Factory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TiendaServiceTest {

    val partyClienteDeTiendaDAO = OrientDBPartyDAO()
    private val peleaService = PeleaServiceImpl(
        HibernatePeleaDAO(),
        HibernatePartyDAO(),
        HibernateAventureroDAO(),
        OrientDBPartyDAO()
    )
    private val partyService = PartyServiceImpl(HibernatePartyDAO())
    val tiendaService = TiendaServicePersistente(partyClienteDeTiendaDAO, OrientDBItemDAO(), OperacionesDAO())
    val objetosDeTestFactory = Factory()
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
    fun `party compra item y se le cobra`(){

        val partyClienteDeTienda = OrientDBTransactionRunner.runTrx { // TODO: hacerla pelear y que gane dinero como corresponde
            val partyClienteDeTienda = partyClienteDeTiendaDAO.recuperar(party.nombre())
            partyClienteDeTienda.adquirirRecompensaDePelea()
            partyClienteDeTiendaDAO.actualizar(partyClienteDeTienda)

            partyClienteDeTienda
        }
        val monedasAntesDeCompra = partyClienteDeTienda.monedas()
        val precioDelItem = 200
        tiendaService.registrarItem("bandera flameante", 200)

        tiendaService.registrarCompra(party.nombre(),"bandera flameante")

        var partyRecuperada = tiendaService.recuperarParty(party.nombre())
        assertThat(partyRecuperada.monedas).isEqualTo(monedasAntesDeCompra - precioDelItem)
    }

    @Test
    fun `inicialmente una party no tiene ninguna compra registrada`(){
        //val party = tiendaService.registrarParty(1, 100)

        val comprasRealizadas = tiendaService.comprasRealizadasPorParty(party.nombre())

        assertThat(comprasRealizadas).isEmpty()
    }

    @Test
    fun `una party realiza una compra y queda registrada`(){
        //val party = tiendaService.registrarParty(1, 100)
        val item = tiendaService.registrarItem("Un item", 0)

        tiendaService.registrarCompra(party.nombre(),item.nombre())

        val comprasRealizadas = tiendaService.comprasRealizadasPorParty(party.nombre())

        val comprasEsperadas = listOf(Compra(item))

        assertThat(comprasRealizadas).usingRecursiveComparison().isEqualTo(comprasEsperadas)
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
        val precioItem = tiendaService.recuperarItem("bandera flameante").precio()

        assertThat(partyRecuperada.monedas).isEqualTo(monedasAntesDeCompra - precioItem)
    }

    @Test
    fun `los 10 mas comprados ordenados de mayor numero de compras a menor de una tienda`(){
        tiendaService.registrarItem("chocolate", 0)
        tiendaService.registrarItem("banana",0)
        tiendaService.registrarItem("frutilla", 0)
        tiendaService.registrarItem("anillo", 0)
        tiendaService.registrarItem("piedra", 0)
        tiendaService.registrarItem("baculo", 0)
        tiendaService.registrarItem("tela", 0)
        tiendaService.registrarItem("espada", 0)
        tiendaService.registrarItem("coco", 0)
        tiendaService.registrarItem("mango", 0)


        comprarNVeces(20, party.nombre(), "banana")
        comprarNVeces(18, party.nombre(), "frutilla")
        comprarNVeces(17,  party.nombre(), "chocolate")
        comprarNVeces(15, party.nombre(), "anillo")
        comprarNVeces(13, party.nombre(), "piedra")
        comprarNVeces(10,  party.nombre(), "baculo")
        comprarNVeces(9, party.nombre(), "tela")
        comprarNVeces(7, party.nombre(), "espada")
        comprarNVeces(6,  party.nombre(), "coco")
        comprarNVeces(5,  party.nombre(), "mango")

        val losMasComprados = tiendaService.loMasComprado()

        assertThat(losMasComprados[0].first.nombre).isEqualTo("banana")
        assertThat(losMasComprados[1].first.nombre).isEqualTo("frutilla")
        assertThat(losMasComprados[2].first.nombre).isEqualTo("chocolate")
        assertThat(losMasComprados[3].first.nombre).isEqualTo("anillo")
        assertThat(losMasComprados[4].first.nombre).isEqualTo("piedra")
        assertThat(losMasComprados[5].first.nombre).isEqualTo("baculo")
        assertThat(losMasComprados[6].first.nombre).isEqualTo("tela")
        assertThat(losMasComprados[7].first.nombre).isEqualTo("espada")
        assertThat(losMasComprados[8].first.nombre).isEqualTo("coco")
        assertThat(losMasComprados[9].first.nombre).isEqualTo("mango")
    }

    @Test
    fun `los mas comprados de una tienda sin ventas devuelve una lista vacia`() {
        tiendaService.registrarItem("chocolate", 2)
        tiendaService.registrarItem("banana", 2)
        tiendaService.registrarItem("frutilla", 2)

        val losMasComprados = tiendaService.loMasComprado()
        assertThat(losMasComprados).isEmpty()

    }

    @Test
     fun `los accesorios de una party con 2 accesorios`() {
        val aliado = Aventurero("Jorge")
        partyService.agregarAventureroAParty(party.id()!!,aliado)

        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)

        tiendaService.registrarItem("chocolate", 2)
        tiendaService.registrarItem("banana", 2)
        tiendaService.registrarItem("frutilla", 2)

        tiendaService.registrarCompra(party.nombre(),"chocolate")
        tiendaService.registrarCompra(party.nombre(),"frutilla")

        val items = tiendaService.losItemsDe(party.nombre())

        assertThat(items[0].nombre).isEqualTo("chocolate")
        assertThat(items[1].nombre).isEqualTo("frutilla")
    }

    @Test
    fun `una party recien registrada en la tienda no tiene items`() {
        val party = Party("Lakers", "URL")
        partyService.crear(party)
        // al crear una party se registra en la tienda tambien

        val items = tiendaService.losItemsDe(party.nombre())

        assertThat(items).isEmpty()
    }

    private fun comprarNVeces(cantDeCompras: Int, nombreParty: String, nombreItem: String) {
        repeat(cantDeCompras){
            tiendaService.registrarCompra(nombreParty,nombreItem)
        }
    }

    @AfterEach
    fun tearDown(){
        OrientDBDataDAO().clear()

        HibernateTransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }
}
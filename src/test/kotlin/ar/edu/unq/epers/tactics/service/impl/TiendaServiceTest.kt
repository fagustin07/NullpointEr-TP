package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.exceptions.*
import ar.edu.unq.epers.tactics.modelo.tienda.Compra
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.InventarioPartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBInventarioPartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBItemDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBOperacionesDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner.runTrx
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TiendaServiceTest {

    private val inventarioPartyDAO: InventarioPartyDAO = OrientDBInventarioPartyDAO()
    private val peleaService = PeleaServiceImpl(
        HibernatePeleaDAO(),
        HibernatePartyDAO(),
        HibernateAventureroDAO(),
        inventarioPartyDAO
    )
    private val partyService = PartyServiceImpl(HibernatePartyDAO(), inventarioPartyDAO)
    val tiendaService = TiendaServicePersistente(inventarioPartyDAO, OrientDBItemDAO(), OrientDBOperacionesDAO())
    lateinit var party : Party

    @BeforeEach
    fun setUp(){
        party = partyService.crear(Party("Memories",""))
    }

    @Test
    fun `no se puede registrar una party con un nombre existente`(){
        val exception = assertThrows<PartyAlreadyRegisteredException> { partyService.crear(party) }
        assertThat(exception.message).isEqualTo("La party ${party.nombre()} ya está en el sistema.")
    }

    @Test
    fun `no se puede recuperar una party con un nombre sin registrar`(){
        val exception = assertThrows<PartyNotRegisteredException> { tiendaService.registrarCompra("Los del fuego", "Item") }
        assertThat(exception.message).isEqualTo("No exite una party llamada Los del fuego en el sistema.")
    }

    @Test
    fun `no se puede recuperar un item con un nombre sin registrar`(){
        val exception = assertThrows<InexistentItemException> { tiendaService.registrarCompra("Memories","Lanzallamas") }
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

        val aliado = Aventurero("Jorge")
        partyService.agregarAventureroAParty(party.id()!!,aliado)

        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)

        val monedasAntesDeCompra = runTrx { inventarioPartyDAO.recuperar(party.nombre()).monedas }
        val precioDelItem = 200
        tiendaService.registrarItem("bandera flameante", 200)

        tiendaService.registrarCompra(party.nombre(),"bandera flameante")

        val monedasLuegoDeCompra = runTrx { inventarioPartyDAO.recuperar(party.nombre()).monedas }

        assertThat(monedasLuegoDeCompra).isEqualTo(monedasAntesDeCompra-precioDelItem)
    }

    @Test
    fun `inicialmente una party no tiene ninguna compra registrada`(){
        val comprasRealizadas = tiendaService.comprasRealizadasPor(party.nombre())

        assertThat(comprasRealizadas).isEmpty()
    }

    @Test
    fun `party compra item y queda registrado`(){
        val aliado = Aventurero("Jorge")
        partyService.agregarAventureroAParty(party.id()!!,aliado)
        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!
        peleaService.terminarPelea(peleaId)

        val item = tiendaService.registrarItem("bandera flameante", 200)

        tiendaService.registrarCompra(party.nombre(),"bandera flameante")


        val comprasEsperadas = listOf(Compra(item))
        val comprasRealizadas = tiendaService.comprasRealizadasPor(party.nombre())
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
        val partyMonedas = runTrx{ inventarioPartyDAO.recuperar(party.nombre()) }
        val monedasAntesDePelea = partyMonedas.monedas
        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)

        val partyLuegoDePelea = runTrx{ inventarioPartyDAO.recuperar(party.nombre()) }
        assertThat(partyLuegoDePelea.monedas).isEqualTo(monedasAntesDePelea)
    }

    @Test
    fun `ganar una pelea incrementa el dinero de una party`(){
        val aliado = Aventurero("Jorge")
        partyService.agregarAventureroAParty(party.id()!!,aliado)

        val monedasAntesDeGanarPelea = runTrx{ inventarioPartyDAO.recuperar(party.nombre()).monedas }
        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)

        val partyLuegoDePelea = runTrx{ inventarioPartyDAO.recuperar(party.nombre())}
        val recompensaPorGanarPelea = 500
        val monedasEsperadas = monedasAntesDeGanarPelea + recompensaPorGanarPelea

        assertThat(partyLuegoDePelea.monedas).isEqualTo(monedasEsperadas)
    }

    @Test
    fun `los 10 mas comprados ordenados de mayor numero de compras a menor de una tienda`(){
        val aliado = Aventurero("Jorge")
        partyService.agregarAventureroAParty(party.id()!!,aliado)

        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)

        tiendaService.registrarItem("chocolate", 2)
        tiendaService.registrarItem("banana", 2)
        tiendaService.registrarItem("frutilla", 2)
        tiendaService.registrarItem("anillo", 2)
        tiendaService.registrarItem("piedra", 2)
        tiendaService.registrarItem("baculo", 2)
        tiendaService.registrarItem("tela", 2)
        tiendaService.registrarItem("espada", 2)
        tiendaService.registrarItem("coco", 2)
        tiendaService.registrarItem("mango", 2)


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
    fun `los items de una party sin items devuelve una lista vacia`() {
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
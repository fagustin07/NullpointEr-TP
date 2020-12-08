package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.exceptions.*
import ar.edu.unq.epers.tactics.modelo.tienda.Compra
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.calendario.FakeProveedorDeFechas
import ar.edu.unq.epers.tactics.modelo.tienda.Item
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
import java.time.LocalDate

class TiendaServiceTest {

    private val NOW = LocalDate.now()
    private val inventarioPartyDAO: InventarioPartyDAO = OrientDBInventarioPartyDAO()
    private val peleaService = PeleaServiceImpl(
        HibernatePeleaDAO(),
        HibernatePartyDAO(),
        HibernateAventureroDAO(),
        inventarioPartyDAO
    )

    private val proveedorDeFechas = FakeProveedorDeFechas(NOW)
    private val partyService = PartyServiceImpl(HibernatePartyDAO(), inventarioPartyDAO)

    val tiendaService = TiendaServicePersistente(
        inventarioPartyDAO,
        OrientDBItemDAO(proveedorDeFechas),
        OrientDBOperacionesDAO(proveedorDeFechas),
        PartyServiceImpl(HibernatePartyDAO(), inventarioPartyDAO)
    )
    lateinit var party: Party

    @BeforeEach
    fun setUp() {
        party = partyService.crear(Party("Memories", ""))
    }

    @Test
    fun `no se puede registrar un inventario de party con un nombre existente`(){
        val exception = assertThrows<RuntimeException> { partyService.crear(party) }
        assertThat(exception.message).isEqualTo("Ya existe un InventarioParty llamado ${party.nombre()} en el sistema.")
    }

    @Test
    fun `no se puede recuperar un inventario de party con un nombre sin registrar`(){
        val exception = assertThrows<RuntimeException> { tiendaService.registrarCompra("Los del fuego", "Item") }
        assertThat(exception.message).isEqualTo("No existe un InventarioParty llamado Los del fuego en el sistema.")
    }

    @Test
    fun `no se puede recuperar un item con un nombre sin registrar`(){
        val exception = assertThrows<RuntimeException> { tiendaService.registrarCompra("Memories","Lanzallamas") }
        assertThat(exception.message).isEqualTo("No existe un Item llamado Lanzallamas en el sistema.")
    }

    @Test
    fun `no se puede registrar un item con un nombre ya existente`() {
        tiendaService.registrarItem("capa en llamas", 400)

        val exception = assertThrows<RuntimeException> {
            tiendaService.registrarItem("capa en llamas", 400)
        }
        assertThat(exception.message).isEqualTo("Ya existe un Item llamado capa en llamas en el sistema.")
    }

    @Test
    fun `party compra item y se le cobra`() {

        ganarPeleaParaGanarMonedas()

        val monedasAntesDeCompra = runTrx { inventarioPartyDAO.recuperar(party.nombre()).monedas }
        val precioDelItem = 200
        tiendaService.registrarItem("bandera flameante", 200)

        tiendaService.registrarCompra(party.nombre(), "bandera flameante")

        val monedasLuegoDeCompra = runTrx { inventarioPartyDAO.recuperar(party.nombre()).monedas }

        assertThat(monedasLuegoDeCompra).isEqualTo(monedasAntesDeCompra - precioDelItem)
    }

    @Test
    fun `inicialmente una party no tiene ninguna compra registrada`() {
        val comprasRealizadas = tiendaService.comprasRealizadasPor(party.nombre())

        assertThat(comprasRealizadas).isEmpty()
    }

    @Test
    fun `party compra item y queda registrado`() {
        ganarPeleaParaGanarMonedas()

        val item = tiendaService.registrarItem("bandera flameante", 200)

        tiendaService.registrarCompra(party.nombre(), "bandera flameante")


        val comprasEsperadas = listOf(Compra(item, NOW))
        val comprasRealizadas = tiendaService.comprasRealizadasPor(party.nombre())
        assertThat(comprasRealizadas).usingRecursiveComparison().isEqualTo(comprasEsperadas)
    }

    @Test
    fun `se levanta una excepcion al querer comprar un item de mas valor que las monedas que tiene party en su inventario`(){
        tiendaService.registrarItem("bandera flameante", 10)

        val exception = assertThrows<RuntimeException> { tiendaService.registrarCompra(party.nombre(),"bandera flameante") }
        assertThat(exception.message).isEqualTo("No puedes comprar 'bandera flameante', te faltan 10 monedas.")
    }

    @Test
    fun `perder una pelea no incrementa el dinero de una party`() {
        val partyMonedas = runTrx { inventarioPartyDAO.recuperar(party.nombre()) }
        val monedasAntesDePelea = partyMonedas.monedas
        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)

        val partyLuegoDePelea = runTrx { inventarioPartyDAO.recuperar(party.nombre()) }
        assertThat(partyLuegoDePelea.monedas).isEqualTo(monedasAntesDePelea)
    }

    @Test
    fun `ganar una pelea incrementa el dinero de una party`() {
        val aliado = Aventurero("Jorge")
        partyService.agregarAventureroAParty(party.id()!!, aliado)

        val monedasAntesDeGanarPelea = runTrx { inventarioPartyDAO.recuperar(party.nombre()).monedas }
        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)

        val partyLuegoDePelea = runTrx { inventarioPartyDAO.recuperar(party.nombre()) }
        val recompensaPorGanarPelea = 500
        val monedasEsperadas = monedasAntesDeGanarPelea + recompensaPorGanarPelea

        assertThat(partyLuegoDePelea.monedas).isEqualTo(monedasEsperadas)
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
    fun `lo mas comprado de la ultima semana es solo frutilla`() {
        ganarPeleaParaGanarMonedas()

        tiendaService.registrarItem("chocolate", 2)
        tiendaService.registrarItem("banana", 2)
        tiendaService.registrarItem("frutilla", 2)


        proveedorDeFechas.cambiarFechaActual(LocalDate.of(1999, 10, 29))
        comprarNVeces(5, party.nombre(), "chocolate")
        comprarNVeces(15, party.nombre(), "banana")
        comprarNVeces(12, party.nombre(), "frutilla")

        proveedorDeFechas.cambiarFechaActual(NOW)
        comprarNVeces(8, party.nombre(), "frutilla")

        val losMasComprados = tiendaService.loMasComprado()

        val parEsperado = Pair(Item("frutilla", 2), 8)
        assertThat(losMasComprados.size).isEqualTo(1)
        assertThat(losMasComprados[0])
            .usingRecursiveComparison()
            .isEqualTo(parEsperado)
    }

    @Test
     fun `los accesorios de una party con 2 accesorios`() {
        ganarPeleaParaGanarMonedas()

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

    @Test
    fun `si una party compra un accesorio entonces aparece en la lista de compradores de ese accesorio`() {
        ganarPeleaParaGanarMonedas()

        val nombreItem = "bandera flameante"
        tiendaService.registrarItem(nombreItem, 10)
        tiendaService.registrarCompra(party.nombre(), nombreItem)

        val compradoresDeItem = tiendaService.compradoresDe(nombreItem)

        assertThat(compradoresDeItem)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(party)
    }

    private fun ganarPeleaParaGanarMonedas() {
        val aliado = Aventurero("Jorge")
        partyService.agregarAventureroAParty(party.id()!!, aliado)

        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)
        party = partyService.recuperar(party.id()!!)
    }

    private fun comprarNVeces(cantDeCompras: Int, nombreParty: String, nombreItem: String) {
        repeat(cantDeCompras) {
            tiendaService.registrarCompra(nombreParty, nombreItem)
        }
    }

    @AfterEach
    fun tearDown() {
        OrientDBDataDAO().clear()

        HibernateTransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }
}
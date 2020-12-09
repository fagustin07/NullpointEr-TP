package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.calendario.FakeProveedorDeFechas
import ar.edu.unq.epers.tactics.modelo.tienda.Compra
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
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class TiendaServiceTest {

    private val NOW = LocalDateTime.now().withNano(0)
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
    fun `no se puede registrar un inventario de party con un nombre existente`() {
        val exception = assertThrows<RuntimeException> { partyService.crear(party) }
        assertThat(exception.message).isEqualTo("Ya existe un InventarioParty llamado ${party.nombre()} en el sistema.")
    }

    @Test
    fun `no se puede recuperar un inventario de party con un nombre sin registrar`() {
        val exception = assertThrows<RuntimeException> { tiendaService.registrarCompra("Los del fuego", "Item") }
        assertThat(exception.message).isEqualTo("No existe un InventarioParty llamado Los del fuego en el sistema.")
    }

    @Test
    fun `no se puede recuperar un item con un nombre sin registrar`() {
        val exception = assertThrows<RuntimeException> { tiendaService.registrarCompra("Memories", "Lanzallamas") }
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

        party = ganarPeleaParaGanarMonedas(party.id()!!)

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
        party = ganarPeleaParaGanarMonedas(party.id()!!)

        val item = tiendaService.registrarItem("bandera flameante", 200)

        tiendaService.registrarCompra(party.nombre(), "bandera flameante")


        val comprasEsperadas = listOf(Compra(item, proveedorDeFechas.ahora()))
        val comprasRealizadas = tiendaService.comprasRealizadasPor(party.nombre())
        assertThat(comprasRealizadas).usingRecursiveComparison().isEqualTo(comprasEsperadas)
    }

    @Test
    fun `se levanta una excepcion al querer comprar un item de mas valor que las monedas que tiene party en su inventario`() {
        tiendaService.registrarItem("bandera flameante", 10)

        val exception =
            assertThrows<RuntimeException> { tiendaService.registrarCompra(party.nombre(), "bandera flameante") }
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
        party = ganarPeleaParaGanarMonedas(party.id()!!)

        tiendaService.registrarItem("chocolate", 2)
        tiendaService.registrarItem("banana", 2)
        tiendaService.registrarItem("frutilla", 2)


        proveedorDeFechas.cambiarFechaActual(LocalDateTime.of(1999, 10, 29,12,30,11))
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
        party = ganarPeleaParaGanarMonedas(party.id()!!)

        tiendaService.registrarItem("chocolate", 2)
        tiendaService.registrarItem("banana", 2)
        tiendaService.registrarItem("frutilla", 2)

        tiendaService.registrarCompra(party.nombre(), "chocolate")
        tiendaService.registrarCompra(party.nombre(), "frutilla")

        val items = tiendaService.losItemsDe(party.nombre())
        val itemsEsperados = listOf(Item("chocolate",2), Item("frutilla",2))

        assertThat(items)
            .usingFieldByFieldElementComparator()
            .containsAll(itemsEsperados)
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
        party = ganarPeleaParaGanarMonedas(party.id()!!)

        val nombreItem = "bandera flameante"
        tiendaService.registrarItem(nombreItem, 10)
        tiendaService.registrarCompra(party.nombre(), nombreItem)

        val compradoresDeItem = tiendaService.compradoresDe(nombreItem)

        assertThat(compradoresDeItem)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(party)
    }

    @Test
    fun `si una party le vende un item a otra, entonces la party vendedora ya no tiene ese item`() {
        var partyCompradora = partyService.crear(Party("Dalasha", "url"))
        partyCompradora = ganarPeleaParaGanarMonedas(partyCompradora.id()!!)
        party = ganarPeleaParaGanarMonedas(party.id()!!)
        val item = comprarItem()

        proveedorDeFechas.cambiarFechaActual(LocalDateTime.now())
        tiendaService.tradear(party.nombre(), partyCompradora.nombre(), listOf(item), 10)

        assertThat(tiendaService.losItemsDe(party.nombre()))
            .usingRecursiveFieldByFieldElementComparator()
            .doesNotContain(item)
    }

    @Test
    fun `si una party le vende un item a otra, entonces la party compradora tiene ese item`() {
        var partyCompradora = partyService.crear(Party("Dalasha", "url"))
        partyCompradora = ganarPeleaParaGanarMonedas(partyCompradora.id()!!)
        party = ganarPeleaParaGanarMonedas(party.id()!!)
        val item = comprarItem()

        tiendaService.tradear(party.nombre(), partyCompradora.nombre(), listOf(item), 10)

        assertThat(tiendaService.losItemsDe(partyCompradora.nombre()))
            .usingRecursiveFieldByFieldElementComparator()
            .contains(item)
    }

    @Test
    fun `si una party le vende un item a otra, entonces la party compradora gasta las monedas`() {
        var partyCompradora = partyService.crear(Party("Dalasha", "url"))
        party = ganarPeleaParaGanarMonedas(party.id()!!)
        partyCompradora = ganarPeleaParaGanarMonedas(partyCompradora.id()!!)
        val monedasAntesDeCompra = runTrx { inventarioPartyDAO.recuperar(partyCompradora.nombre()).monedas }
        val item = comprarItem()

        val monedasAIntercambiar = 20
        tiendaService.tradear(party.nombre(), partyCompradora.nombre(), listOf(item), monedasAIntercambiar)

        val monedasLuegoDeCompra = runTrx { inventarioPartyDAO.recuperar(partyCompradora.nombre()).monedas }
        assertThat(monedasLuegoDeCompra)
            .isEqualTo(monedasAntesDeCompra - monedasAIntercambiar)
    }

    @Test
    fun `si una party le vende un item a otra, entonces la party vendedora recibe las monedas`() {
        var partyCompradora = partyService.crear(Party("Dalasha", "url"))
        party = ganarPeleaParaGanarMonedas(party.id()!!)
        partyCompradora = ganarPeleaParaGanarMonedas(partyCompradora.id()!!)
        val monedasAntesDeCompra = runTrx { inventarioPartyDAO.recuperar(party.nombre()).monedas }
        val item = comprarItem()

        tiendaService.tradear(party.nombre(), partyCompradora.nombre(), listOf(item), 20)

        val monedasLuegoDeCompra = runTrx { inventarioPartyDAO.recuperar(party.nombre()).monedas }
        assertThat(monedasLuegoDeCompra)
            .isEqualTo(monedasAntesDeCompra + item.precio)
    }

    @Test
    fun `una party no puede vender un item que no tiene`() {
        var partyCompradora = partyService.crear(Party("Dalasha", "url"))
        partyCompradora = ganarPeleaParaGanarMonedas(partyCompradora.id()!!)

        val item = Item("Item que no posee", 10)
        tiendaService.registrarItem(item.nombre(), item.precio())
        assertThatThrownBy { tiendaService.tradear(party.nombre(), partyCompradora.nombre(), listOf(item), 10) }
            .hasMessageContaining("La party debe ser dueña de todos los items que pretende vender")
    }

    @Test
    fun `una party no puede comprar un item a otra si no tiene monedas suficientes`() {
        val partyCompradora = partyService.crear(Party("Dalasha", "url"))
        ganarPeleaParaGanarMonedas(party.id()!!)
        val item = comprarItem()

        assertThatThrownBy{ tiendaService.tradear(party.nombre(), partyCompradora.nombre(), listOf(item), 10) }
            .hasMessageContaining("No puedes debitar '10', te faltan 10 monedas.")
    }

    @Test
    fun `si una party vende un item y luego lo vuelve a comprar, vuelve a estar entre sus items`() {
        var partyCompradora = partyService.crear(Party("Dalasha", "url"))
        partyCompradora = ganarPeleaParaGanarMonedas(partyCompradora.id()!!)
        party = ganarPeleaParaGanarMonedas(party.id()!!)
        val item = comprarItem()

        tiendaService.tradear(party.nombre(), partyCompradora.nombre(), listOf(item), 10)

        proveedorDeFechas.cambiarFechaActual(LocalDateTime.now())
        tiendaService.registrarCompra(party.nombre(), item.nombre)

        assertThat(tiendaService.losItemsDe(party.nombre()))
            .usingRecursiveFieldByFieldElementComparator()
            .contains(item)
    }

    @Test
    fun `una tienda tiene 3 items en venta`() {
        tiendaService.registrarItem("chocolate", 2)
        tiendaService.registrarItem("banana", 2)
        tiendaService.registrarItem("frutilla", 2)

        val itemsEnTienda = tiendaService.itemsEnVenta()

        assertThat(itemsEnTienda[0].nombre).isEqualTo("banana")
        assertThat(itemsEnTienda[1].nombre).isEqualTo("chocolate")
        assertThat(itemsEnTienda[2].nombre).isEqualTo("frutilla")
    }

    @Test
    fun `una tienda con 3 items en venta, vende uno mas de 20 veces y ahora solo se muestran 2 en venta debido al limite de ventas`() {
        tiendaService.registrarItem("chocolate", 2)
        tiendaService.registrarItem("banana", 0)
        tiendaService.registrarItem("frutilla", 2)

        comprarNVeces(22,party.nombre(),"banana")

        val itemsEnTienda = tiendaService.itemsEnVenta()

        assertThat(itemsEnTienda[0].nombre).isEqualTo("chocolate")
        assertThat(itemsEnTienda[1].nombre).isEqualTo("frutilla")
    }

    private fun comprarItem(): Item {
        val nombreItem = "bandera flameante"
        val item = tiendaService.registrarItem(nombreItem, 10)
        tiendaService.registrarCompra(party.nombre(), nombreItem)
        return item
    }


    private fun ganarPeleaParaGanarMonedas(partyId: Long): Party {
        val aliado = Aventurero("Jorge")
        partyService.agregarAventureroAParty(partyId, aliado)

        val peleaId = peleaService.iniciarPelea(party.id()!!, "party enemiga").id()!!

        peleaService.terminarPelea(peleaId)
        return partyService.recuperar(partyId)
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
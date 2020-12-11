package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.tienda.Compra
import ar.edu.unq.epers.tactics.modelo.tienda.InventarioParty
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.persistencia.dao.InventarioPartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.ItemDAO
import ar.edu.unq.epers.tactics.persistencia.dao.OperacionesDAO
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.TiendaService
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner.runTrx
import java.lang.RuntimeException

class TiendaServicePersistente(
    protected val inventarioPartyDAO: InventarioPartyDAO,
    protected val itemDAO: ItemDAO,
    protected val operacionesDAO: OperacionesDAO,
    protected val partyService: PartyService
) : TiendaService {

    override fun registrarItem(nombre: String, precio: Int) =
        runTrx { itemDAO.guardar(Item(nombre, precio)) }

    override fun registrarCompra(nombreParty: String, nombreDeItemAComprar: String) =
        runTrx {
            val party = inventarioPartyDAO.recuperar(nombreParty)
            val item = itemDAO.recuperar(nombreDeItemAComprar)

            party.comprar(item)

            inventarioPartyDAO.actualizar(party)

            operacionesDAO.registrarCompraDe(party, item)
        }

    override fun comprasRealizadasPor(nombreDeParty: String): List<Compra> =
        runTrx { operacionesDAO.comprasRealizadasPorParty(nombreDeParty) }

    override fun loMasComprado(): List<Pair<Item, Int>> {
        return runTrx { itemDAO.loMasCompradoEnLaUltimaSemana() }
    }

    override fun losItemsDe(nombreParty: String): List<Item> {
        return runTrx { itemDAO.itemsEnVenta().filter { itemDAO.perteneceA(it.nombre,nombreParty) } }
    }

    override fun compradoresDe(nombreItem: String): List<Party> {
        val inventarios = runTrx { operacionesDAO.partiesQueCompraron(nombreItem) }
        return inventarios.map { partyService.recuperarPorNombre(it.nombre) }
    }

    override fun tradear(
        nombrePartyVendedora: String,
        nombrePartyCompradora: String,
        itemsAVender: List<Item>,
        monedas: Int
    ) {
        lateinit var inventarioPartyVendedora: InventarioParty
        lateinit var inventarioPartyCompradora: InventarioParty
        verificarQueLaPartyVendedoraPoseaTodosLosItems(nombrePartyVendedora, itemsAVender)
        runTrx {
            inventarioPartyVendedora = inventarioPartyDAO.recuperar(nombrePartyVendedora)
            inventarioPartyCompradora = inventarioPartyDAO.recuperar(nombrePartyCompradora)

            registrarVentaDeItems(itemsAVender, inventarioPartyVendedora, monedas, nombrePartyCompradora)

            inventarioPartyCompradora.debitarMonto(monedas)
            inventarioPartyDAO.actualizar(inventarioPartyCompradora)
        }


    }

    private fun registrarVentaDeItems(
        itemsAVender: List<Item>,
        inventarioPartyVendedora: InventarioParty,
        monedas: Int,
        nombrePartyCompradora: String
    ) {
        itemsAVender.forEach {
            this.registrarVenta(inventarioPartyVendedora, it, monedas)
            this.registrarCompraAParty(nombrePartyCompradora, it.nombre())
        }
    }


    private fun registrarCompraAParty(nombreParty: String, nombreDeItemAComprar: String) {
        val party = inventarioPartyDAO.recuperar(nombreParty)
        val item = itemDAO.recuperar(nombreDeItemAComprar)

        operacionesDAO.registrarCompraDe(party, item)
    }

    private fun registrarVenta(inventarioPartyVendedora: InventarioParty, item: Item, monedas: Int) {
        inventarioPartyVendedora.monedas += monedas

        inventarioPartyDAO.actualizar(inventarioPartyVendedora)
        operacionesDAO.registrarVentaDe(inventarioPartyVendedora, item)
    }

    private fun verificarQueLaPartyVendedoraPoseaTodosLosItems(nombreParty: String, itemsAVender: List<Item>) {
        val items = losItemsDe(nombreParty)
        if (!items.containsAll(itemsAVender)) {
            throw RuntimeException("La party debe ser dueña de todos los items que pretende vender")
        }
    }

    override fun itemsEnVenta(): List<Item> {
        return runTrx { itemDAO.itemsEnVenta() }
    }


}

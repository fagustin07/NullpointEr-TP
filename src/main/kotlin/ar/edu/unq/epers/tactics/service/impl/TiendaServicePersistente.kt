package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.tienda.Compra
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.persistencia.dao.InventarioPartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.ItemDAO
import ar.edu.unq.epers.tactics.persistencia.dao.OperacionesDAO
import ar.edu.unq.epers.tactics.service.TiendaService
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner.runTrx

class TiendaServicePersistente(protected val inventarioPartyDAO: InventarioPartyDAO, protected val itemDAO: ItemDAO, protected val operacionesDAO: OperacionesDAO): TiendaService {

    override fun registrarItem(nombre: String, precio: Int) =
        runTrx { itemDAO.guardar(Item(nombre,precio)) }

    override fun registrarCompra(nombreParty: String, nombreDeItemAComprar: String) =
        runTrx {
            val party = inventarioPartyDAO.recuperar(nombreParty)
            val item = itemDAO.recuperar(nombreDeItemAComprar)

            party.comprar(item)

            inventarioPartyDAO.actualizar(party)

            operacionesDAO.registrarCompraDe(party ,item)
        }

    override fun comprasRealizadasPor(nombreDeParty: String): List<Compra> =
        runTrx { operacionesDAO.comprasRealizadasPorParty(nombreDeParty) }

    override fun loMasComprado(): List<Pair<Item, Int>>{
        return runTrx { itemDAO.loMasComprado() }
    }

}

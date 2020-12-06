package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.tienda.Compra
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBItemDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBPartyDAO
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OperacionesDAO
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner.runTrx
import java.time.LocalDate

class TiendaServicePersistente(protected val partyMonedasDAO: OrientDBPartyDAO, protected val itemDAO: OrientDBItemDAO, protected val operacionesDAO: OperacionesDAO) {

    fun recuperarParty(nombreParty: String) =
        runTrx { partyMonedasDAO.recuperar(nombreParty) }

    fun registrarItem(nombre: String, precio: Int) =
        runTrx { itemDAO.guardar(Item(nombre,precio)) }

    fun recuperarItem(nombre: String) =
        runTrx { itemDAO.recuperar(nombre) }

    fun registrarCompra(nombreParty: String, nombreDeItemAComprar: String) =
        runTrx {
            val party = partyMonedasDAO.recuperar(nombreParty)
            val item = itemDAO.recuperar(nombreDeItemAComprar)

            party.comprar(item)

            partyMonedasDAO.actualizar(party)

            operacionesDAO.registrarCompraDe(party ,item)
        }

    fun comprasRealizadasPorParty(nombreDeParty: String): List<Compra> =
        runTrx { operacionesDAO.comprasRealizadasPorParty(nombreDeParty) }

    fun loMasComprado(): List<Pair<Item, Int>>{
        return runTrx { itemDAO.loMasComprado() }
    }

}

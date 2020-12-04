package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBItemDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBPartyDAO
import ar.edu.unq.epers.tactics.modelo.tienda.PartyConMonedas
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner.runTrx

class TiendaServicePersistente(protected val partyMonedasDAO: OrientDBPartyDAO, protected val itemDAO: OrientDBItemDAO) {

    fun registrarParty(partyId: Long, cantidadDeMonedasIniciales: Int) =
        runTrx { partyMonedasDAO.guardar(PartyConMonedas(partyId, cantidadDeMonedasIniciales)) }

    fun recuperarParty(partyId: Long) =
        runTrx { partyMonedasDAO.recuperar(partyId) }

    fun registrarItem(nombre: String, precio: Int) =
        runTrx { itemDAO.guardar(Item(nombre,precio)) }

    fun recuperarItem(nombre: String) =
        runTrx { itemDAO.recuperar(nombre) }

    fun registrarCompra(partyId: Long, nombreDeItemAComprar: String) =
        runTrx {
            val party = partyMonedasDAO.recuperar(partyId)
            val item = itemDAO.recuperar(nombreDeItemAComprar)

            party.comprar(item)

            partyMonedasDAO.actualizar(party)

            val query =
                """
                CREATE EDGE haComprado
                FROM (SELECT FROM Party WHERE id = ?)
                TO (SELECT FROM Item WHERE nombre = ?)
                """

            OrientDBSessionFactoryProvider.instance.session.command(query,partyId, nombreDeItemAComprar) // TODO: con actualizar la party tal vez se deberia actualizar todo.... (?)
        }



}

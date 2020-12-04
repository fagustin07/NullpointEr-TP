package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.modelo.tienda.Compra
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.modelo.tienda.PartyConMonedas
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import kotlin.streams.toList

class OperacionesDAO {
    val session get() = OrientDBSessionFactoryProvider.instance.session

    fun registrarCompraDe(party: PartyConMonedas, item: Item) {
        val query =
            """
            CREATE EDGE HaComprado
            FROM (SELECT FROM PartyConMonedas WHERE id = ?)
            TO (SELECT FROM Item WHERE nombre = ?)
            """

        session.command(query, party.id, item.nombre)
    }

    fun comprasRealizadasPorParty(partyId: Long): List<Compra> {
        val query = """
            select from haComprado where out IN (select from PartyConMonedas where id=?)
            """

        return session
            .query(query, partyId)
            .stream()
            .map { it.edge.get().to }
            .map { Item(it.getProperty("nombre"), it.getProperty("precio")) }
            .map { Compra(it) }
            .toList()
    }

}

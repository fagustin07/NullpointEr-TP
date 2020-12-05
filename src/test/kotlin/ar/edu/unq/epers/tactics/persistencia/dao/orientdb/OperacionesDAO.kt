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
            FROM (SELECT FROM PartyConMonedas WHERE nombre = ?)
            TO (SELECT FROM Item WHERE nombre = ?)
            """

        session.command(query, party.nombre, item.nombre())
    }

    fun comprasRealizadasPorParty(nombreDeParty: String): List<Compra> {
        val query = """
            select from haComprado where out IN (select from PartyConMonedas where nombre=?)
            """

        return session
            .query(query, nombreDeParty)
            .stream()
            .map { it.edge.get().to } // TODO: corregir la query para que esto no sea necesario. Que retorne el item
            .map { Item(it.getProperty("nombre"), it.getProperty("precio")) }
            .map { Compra(it) }
            .toList()
    }

}

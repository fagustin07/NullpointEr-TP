package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.modelo.tienda.Compra
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.modelo.tienda.InventarioParty
import ar.edu.unq.epers.tactics.persistencia.dao.OperacionesDAO
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import kotlin.streams.toList

class OrientDBOperacionesDAO: OperacionesDAO {
    val session get() = OrientDBSessionFactoryProvider.instance.session

    override fun registrarCompraDe(inventarioParty: InventarioParty, item: Item) {
        val query =
            """
            CREATE EDGE HaComprado
            FROM (SELECT FROM InventarioParty WHERE nombre = ?)
            TO (SELECT FROM Item WHERE nombre = ?)
            """

        session.command(query, inventarioParty.nombre, item.nombre())
    }

    override fun comprasRealizadasPorParty(nombreDeParty: String): List<Compra> {
        val query = """
            select from haComprado where out IN (select from InventarioParty where nombre=?)
            """

        return session
            .query(query, nombreDeParty)
            .stream()
            .map { it.edge.get().to } // TODO: corregir la query para que esto no sea necesario. Que retorne el item
            .map { Item(it.getProperty("nombre"), it.getProperty("precio")) }
            .map { Compra(it) }
            .toList()
    }

    override fun clear() {
        session.command("DELETE EDGE HaComprado")
    }

}

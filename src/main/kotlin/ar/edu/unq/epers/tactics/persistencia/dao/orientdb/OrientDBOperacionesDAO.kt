package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.modelo.tienda.Compra
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.modelo.tienda.InventarioParty
import ar.edu.unq.epers.tactics.persistencia.dao.OperacionesDAO
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import java.time.LocalDate
import kotlin.streams.toList

class OrientDBOperacionesDAO: OperacionesDAO {
    val session get() = OrientDBSessionFactoryProvider.instance.session

    override fun registrarCompraDe(inventarioParty: InventarioParty, item: Item, fechaDeCompra: LocalDate) {
        val query =
            """
            CREATE EDGE HaComprado
            FROM (SELECT FROM InventarioParty WHERE nombre = ?)
            TO (SELECT FROM Item WHERE nombre = ?)
            SET fechaDeCompra = ?
            """

        session.command(query, inventarioParty.nombre, item.nombre(), fechaDeCompra.toString())
    }

    override fun comprasRealizadasPorParty(nombreDeParty: String): List<Compra> {
        val query = """
            SELECT in.nombre as nombreItem, in.precio as precioItem, fechaDeCompra
            FROM haComprado 
            WHERE out.nombre = ?
            """

        return session
            .query(query, nombreDeParty)
            .stream()
            .map {
                val item = Item(it.getProperty("nombreItem"), it.getProperty("precioItem"))
                val fechaDeCompra = LocalDate.parse(it.getProperty("fechaDeCompra"))

                Compra(item,fechaDeCompra)
            }
            .toList()
    }

    override fun clear() {
        session.command("DELETE EDGE HaComprado")
    }

}

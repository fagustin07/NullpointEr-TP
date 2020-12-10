package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.modelo.calendario.ProveedorDeFechas
import ar.edu.unq.epers.tactics.modelo.tienda.Compra
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.modelo.tienda.InventarioParty
import ar.edu.unq.epers.tactics.persistencia.dao.OperacionesDAO
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.streams.toList

class OrientDBOperacionesDAO(private val proveedorDeFechas: ProveedorDeFechas): OperacionesDAO {
    val session get() = OrientDBSessionFactoryProvider.instance.session

    override fun registrarCompraDe(inventarioParty: InventarioParty, item: Item) {
        val query =
            """
            CREATE EDGE HaComprado
            FROM (SELECT FROM InventarioParty WHERE nombre = ?)
            TO (SELECT FROM Item WHERE nombre = ?)
            SET fechaDeCompra = ?
            """

        session.command(query, inventarioParty.nombre, item.nombre(), this.fechaActualParaGuardarEnDb())
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
                val fechaDeCompra: String = it.getProperty<String>("fechaDeCompra").replace(' ','T')

                Compra(item, LocalDateTime.parse(fechaDeCompra))
            }
            .toList()
    }

    override fun partiesQueCompraron(nombreItem: String): List<InventarioParty> {
        val query = """
           select out.nombre, out.monedas from haComprado where in.nombre=? 
        """

        return session
            .query(query, nombreItem)
            .stream()
            .map { InventarioParty(it.getProperty("out.nombre"), it.getProperty("out.monedas")) }
            .toList()
    }

    override fun registrarVentaDe(inventarioParty: InventarioParty, item: Item) {
        val query =
            """
            CREATE EDGE HaVendido
            FROM (SELECT FROM InventarioParty WHERE nombre = ?)
            TO (SELECT FROM Item WHERE nombre = ?)
            SET fechaDeVenta = ?
            """

        session.command(query, inventarioParty.nombre, item.nombre(), this.fechaActualParaGuardarEnDb())
    }

    private fun fechaActualParaGuardarEnDb() = proveedorDeFechas.ahora().toString().replace('T', ' ')

    override fun clear() {
        session.command("DELETE EDGE HaComprado")
        session.command("DELETE EDGE HaVendido")
    }
}

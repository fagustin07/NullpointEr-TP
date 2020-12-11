package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.modelo.calendario.Almanaque
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.persistencia.dao.ItemDAO
import com.orientechnologies.orient.core.sql.executor.OResult
import kotlin.streams.toList


class OrientDBItemDAO(private val almanaque: Almanaque) : OrientDBDAO<Item>(Item::class.java), ItemDAO {

    override fun loMasCompradoEnLaUltimaSemana(): List<Pair<Item, Int>> {
        val query =
            """
            SELECT in.nombre as nombre, in.precio as precio, count(*) as vecesComprado
            FROM HaComprado
            WHERE fechaDeCompra >= ?
            GROUP BY in.nombre
            ORDER BY vecesComprado DESC LIMIT 5
            """

        return session.query(query, almanaque.haceUnaSemana())
            .stream()
            .map {
                val item = mapearAEntidad(it)
                val vecesComprado: Int = it.getProperty("vecesComprado")
                Pair(item, vecesComprado)
            }
            .toList()
    }

    override fun itemsEnVenta(): List<Item> {
        val query =
            """
            SELECT FROM Item 
            ORDER BY nombre ASC
            """

        return session.query(query)
            .stream()
            .map { mapearAEntidad(it) }
            .toList()
    }

    override fun mapearAEntidad(oResult: OResult) =
        Item(
            oResult.getProperty("nombre"),
            oResult.getProperty("precio")
        )
}
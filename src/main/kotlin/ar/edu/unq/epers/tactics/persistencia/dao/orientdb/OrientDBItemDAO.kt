package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.modelo.calendario.ProveedorDeFechas
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.persistencia.dao.ItemDAO
import com.orientechnologies.orient.core.sql.executor.OResult
import java.time.LocalDate
import kotlin.streams.toList


class OrientDBItemDAO(private val proveedorDeFechas: ProveedorDeFechas) : OrientDBDAO<Item>(Item::class.java),  ItemDAO {

    override fun loMasComprado(): List<Pair<Item, Int>> {
        val query =
            """
                    SELECT in.nombre as nombre, in.precio as precio, count(*) as vecesComprado
                    FROM HaComprado
                    WHERE fechaDeCompra >= ?
                    GROUP BY in.nombre
                    ORDER BY vecesComprado DESC LIMIT 5
                """

        return session.query(query, proveedorDeFechas.haceUnaSemana())
            .stream()
            .map {
                val item = mapearAEntidad(it)
                val vecesComprado: Int = it.getProperty("vecesComprado")
                Pair(item, vecesComprado)
            }
            .toList()
    }

    override fun mapearAEntidad(result: OResult) =
        Item(
            result.getProperty("nombre"),
            result.getProperty("precio")
        )

    override fun mensajeDeErrorParaEntidadNoEncontrada(nombreDeItem: String) =
        "No existe el item llamado ${nombreDeItem}."

    override fun mensajeDeErrorParaNombreDeEntidadYaRegistrado(nombreDeItem: String) =
        "El item ${nombreDeItem} ya se encuentra en el sistema."

}
package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.persistencia.dao.ItemDAO
import com.orientechnologies.orient.core.sql.executor.OResult
import kotlin.streams.toList


class OrientDBItemDAO : OrientDBDAO<Item>(Item::class.java),  ItemDAO {

    override fun loMasComprado(): List<Pair<Item, Int>> {
        val query =
            """
                    SELECT *, in().size() AS vecesComprado 
                    FROM Item
                    WHERE in().size() > 0
                    ORDER BY vecesComprado DESC LIMIT 10
                """

        return session.query(query)
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
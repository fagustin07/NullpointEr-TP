package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.modelo.tienda.InventarioParty
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.persistencia.dao.InventarioPartyDAO
import com.orientechnologies.orient.core.sql.executor.OResult
import com.orientechnologies.orient.core.record.ORecord
import java.util.*
import kotlin.streams.toList

class OrientDBInventarioPartyDAO : OrientDBDAO<InventarioParty>(InventarioParty::class.java), InventarioPartyDAO {

    override fun actualizar(inventarioParty: InventarioParty) {
        val query = "UPDATE InventarioParty SET monedas = ? WHERE nombre = ?"
        session.command(query, inventarioParty.monedas, inventarioParty.nombre)
    }

    override fun mapearAEntidad(result: OResult) =
        InventarioParty(
            result.getProperty("nombre"),
            result.getProperty("monedas")
        )
    override fun losItemsDe(nombreParty: String):List<Item>{
        val query =
            """
                    SELECT * 
                    FROM Item 
                    WHERE @rid in 
                    (SELECT out() FROM InventarioParty WHERE nombre = ?)
                    ORDER BY nombre asc
                """

        return session.query(query, nombreParty)
            .stream()
            .map {
                Item(it.getProperty("nombre"), it.getProperty("precio"))
            }
            .toList()
    }

    override fun clear() {
        session.command("DELETE VERTEX InventarioParty")
    }

    override fun intentarRecuperar(nombreParty: String): Optional<InventarioParty> =
        session.query("SELECT FROM InventarioParty WHERE nombre = ?", nombreParty)
            .stream()
            .findFirst()
            .map { InventarioParty(nombreParty, it.getProperty("monedas")) }


    /** PRIVATE **/
    override fun mapEntidadDesdeOResult(result: OResult): InventarioParty {
    override fun mapearAEntidad(result: OResult): InventarioParty {
        return InventarioParty(result.getProperty("nombre"), result.getProperty("monedas"))
    }

    override fun mensajeDeErrorParaEntidadNoEncontrada(nombreDeParty: String) =
        "No exite una party llamada ${nombreDeParty} en el sistema."

    override fun mensajeDeErrorParaNombreDeEntidadYaRegistrado(nombreDeParty: String) =
        "La party ${nombreDeParty} ya está en el sistema."
}
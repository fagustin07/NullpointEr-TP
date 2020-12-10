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

    override fun losItemsDe(nombreParty: String):List<Item>{

        val query =
            """ 
                SELECT FROM ITEM
                LET
                ${'$'}ultimaCompra = (SELECT FROM HaComprado WHERE out.nombre = ? ORDER BY fechaDeCompra DESC LIMIT 1),
                ${'$'}ultimaVenta = (SELECT FROM HaVendido WHERE out.nombre = ? ORDER BY fechaDeVenta DESC LIMIT 1),
                ${'$'}fechaUltimaCompra = first(${'$'}ultimaCompra).fechaDeCompra.asDatetime(),
                ${'$'}fechaUltimaVenta = first(${'$'}ultimaVenta).fechaDeVenta.asDatetime()
                
                WHERE 
                ( ${'$'}fechaUltimaCompra IS NOT NULL AND ${'$'}fechaUltimaVenta IS NULL )
                OR
                ( ${'$'}fechaUltimaCompra IS NOT NULL AND
                  ${'$'}fechaUltimaVenta IS NOT NULL AND
                  ${'$'}fechaUltimaCompra  >  ${'$'}fechaUltimaVenta )

                ORDER BY nombre
                """

        return session.query(query, nombreParty, nombreParty)
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


    override fun mapearAEntidad(oResult: OResult) =
        InventarioParty(
            oResult.getProperty("nombre"),
            oResult.getProperty("monedas")
        )
}
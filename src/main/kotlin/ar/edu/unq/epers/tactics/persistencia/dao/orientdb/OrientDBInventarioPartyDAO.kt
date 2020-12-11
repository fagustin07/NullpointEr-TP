package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.modelo.tienda.InventarioParty
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.persistencia.dao.InventarioPartyDAO
import com.orientechnologies.orient.core.sql.executor.OResult

class OrientDBInventarioPartyDAO : OrientDBDAO<InventarioParty>(InventarioParty::class.java), InventarioPartyDAO {

    override fun actualizar(inventarioParty: InventarioParty) {
        val query = "UPDATE InventarioParty SET monedas = ? WHERE nombre = ?"
        session.command(query, inventarioParty.monedas, inventarioParty.nombre)
    }

    override fun mapearAEntidad(oResult: OResult) =
        InventarioParty(
            oResult.getProperty("nombre"),
            oResult.getProperty("monedas")
        )
}
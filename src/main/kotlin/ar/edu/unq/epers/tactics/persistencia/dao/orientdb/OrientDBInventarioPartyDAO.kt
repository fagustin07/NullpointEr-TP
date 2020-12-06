package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.exceptions.PartyAlreadyRegisteredException
import ar.edu.unq.epers.tactics.exceptions.PartyNotRegisteredException
import ar.edu.unq.epers.tactics.modelo.tienda.InventarioParty
import ar.edu.unq.epers.tactics.persistencia.dao.InventarioPartyDAO
import com.orientechnologies.orient.core.record.ORecord
import java.util.*
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBDAO as OrientDBDAO1


class OrientDBInventarioPartyDAO : OrientDBDAO1<InventarioParty>(InventarioParty::class.java), InventarioPartyDAO {

    override fun guardar(inventarioParty: InventarioParty): InventarioParty {
        validarQueNoEsteRegistrada(inventarioParty)

        val nuevoVertexParty = session.newVertex("InventarioParty")
        nuevoVertexParty.setProperty("nombre", inventarioParty.nombreParty)
        nuevoVertexParty.setProperty("monedas", inventarioParty.monedas)
        nuevoVertexParty.save<ORecord>()

        return inventarioParty
    }

    override fun actualizar(inventarioParty: InventarioParty) {
        val query = "UPDATE InventarioParty SET monedas = ? WHERE nombre = ?"
        session.command(query, inventarioParty.monedas, inventarioParty.nombreParty)
    }

    override fun recuperar(nombreParty: String): InventarioParty {
        return intentarRecuperar(nombreParty).orElseThrow { PartyNotRegisteredException(nombreParty) }
    }

    override fun intentarRecuperar(nombreParty: String): Optional<InventarioParty> =
        session.query("SELECT FROM InventarioParty WHERE nombre = ?", nombreParty)
            .stream()
            .findFirst()
            .map { InventarioParty(nombreParty, it.getProperty("monedas")) }


    /** PRIVATE **/
    override fun validarQueNoEsteRegistrada(inventarioParty: InventarioParty) {
        intentarRecuperar(inventarioParty.nombreParty).ifPresent { throw PartyAlreadyRegisteredException(inventarioParty.nombreParty) }
    }

}
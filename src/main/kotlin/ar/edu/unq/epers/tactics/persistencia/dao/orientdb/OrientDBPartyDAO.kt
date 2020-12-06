package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.exceptions.PartyAlreadyRegisteredException
import ar.edu.unq.epers.tactics.exceptions.PartyNotRegisteredException
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.modelo.tienda.PartyConMonedas
import com.orientechnologies.orient.core.record.ORecord
import java.util.*
import kotlin.streams.toList
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBDAO as OrientDBDAO1


class OrientDBPartyDAO : OrientDBDAO1<PartyConMonedas>(PartyConMonedas::class.java) {

    override fun guardar(party: PartyConMonedas): PartyConMonedas {
        validarQueNoEsteRegistrada(party)

        val nuevoVertexParty = session.newVertex("PartyConMonedas")
        nuevoVertexParty.setProperty("nombre", party.nombre)
        nuevoVertexParty.setProperty("monedas", party.monedas)
        nuevoVertexParty.save<ORecord>()

        return party
    }

    override fun actualizar(party: PartyConMonedas) {
        val query = "UPDATE PartyConMonedas SET monedas = ? WHERE nombre = ?"
        session.command(query, party.monedas, party.nombre)
    }

    override fun recuperar(nombreParty: String): PartyConMonedas {
        return intentarRecuperar(nombreParty).orElseThrow { PartyNotRegisteredException(nombreParty) }
    }

    override fun intentarRecuperar(nombreParty: String): Optional<PartyConMonedas> =
        session.query("SELECT FROM PartyConMonedas WHERE nombre = ?", nombreParty)
            .stream()
            .findFirst()
            .map { PartyConMonedas(nombreParty, it.getProperty("monedas")) }

    fun losItemsDe(nombreParty: String):List<Item>{
        val query =
            """
                    SELECT * 
                    FROM Item 
                    WHERE @rid in 
                    (SELECT out() FROM PartyConMonedas WHERE nombre = ?)
                    ORDER BY nombre asc
                """

        val result: List<Item> = session.query(query, nombreParty)
            .stream()
            .map {
                Item(it.getProperty("nombre"), it.getProperty("precio"))
            }
            .toList()

            return result
    }

    /** PRIVATE **/
    override fun validarQueNoEsteRegistrada(party: PartyConMonedas) {
        intentarRecuperar(party.nombre).ifPresent { throw PartyAlreadyRegisteredException(party.nombre) }
    }

}
package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.exceptions.CannotBuyException
import ar.edu.unq.epers.tactics.exceptions.PartyAlreadyRegisteredException
import ar.edu.unq.epers.tactics.exceptions.PartyUnregisteredException
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.record.ORecord


class OrientDBPartyDAO {
    val db: ODatabaseSession get() = OrientDBSessionFactoryProvider.instance.db

    fun registrar(partyId: Long, monedas: Int): PartyConMonedas {
        val query = "SELECT FROM Party WHERE id = ?"
        val queryResult = db.query(query, partyId)
        if (queryResult.hasNext()) throw PartyAlreadyRegisteredException(partyId)

        val result = db.newVertex("Party")
        result.setProperty("id", partyId)
        result.setProperty("monedas", monedas)
        result.save<ORecord>()

        return PartyConMonedas(partyId, monedas)
    }

    fun actualizar(party: PartyConMonedas) {
        val query = "UPDATE Party SET monedas = ? WHERE id = ?"
        db.command(query, party.monedas, party.id)
    }

    fun recuperar(partyId: Long): PartyConMonedas {
        val query = "SELECT FROM Party WHERE id = ?"
        val rs = db.query(query, partyId)

        lateinit var party : PartyConMonedas
        if (rs.hasNext()) {
            val partyPersistida = rs.next()
            val monedas = partyPersistida.getProperty<Int>("monedas")

            party = PartyConMonedas(partyId,monedas)
        } else {
            throw PartyUnregisteredException(partyId)
        }
        return party
    }

    fun comprar(partyId: Long, nombreItem: String) {
        val item: Item = OrientDBItemDAO().recuperar(nombreItem)
        val party: PartyConMonedas = this.recuperar(partyId)
        party.comprar(item)

        this.actualizar(party)

        val query = "CREATE EDGE haComprado " +
                "FROM (SELECT FROM Party WHERE id = ?) TO " +
                "(SELECT FROM Item WHERE nombre = ?)"
        db.command(query,partyId, nombreItem)
    }

}

class PartyConMonedas(val id: Long, var monedas: Int) {

    fun comprar(item: Item) {
        if (item.precio > this.monedas){
            val monedasFaltantes = item.precio - this.monedas

            throw CannotBuyException(item.nombre, monedasFaltantes)
        } else {
            monedas -= item.precio
        }
    }
}

package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.exceptions.PartyAlreadyRegisteredException
import ar.edu.unq.epers.tactics.exceptions.PartyUnregisteredException
import ar.edu.unq.epers.tactics.modelo.tienda.PartyConMonedas
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.record.ORecord
import java.util.*


class OrientDBPartyDAO {

    val session: ODatabaseSession get() = OrientDBSessionFactoryProvider.instance.session

    fun guardar(party: PartyConMonedas): PartyConMonedas {
        validarQueNoEsteRegistradaLaParty(party)

        val nuevoVertexParty = session.newVertex("Party")
        nuevoVertexParty.setProperty("id", party.id)
        nuevoVertexParty.setProperty("monedas", party.monedas)
        nuevoVertexParty.save<ORecord>()

        return party
    }

    fun actualizar(party: PartyConMonedas) {
        val query = "UPDATE Party SET monedas = ? WHERE id = ?"
        session.command(query, party.monedas, party.id)
    }

    fun recuperar(partyId: Long): PartyConMonedas {
        return intentarRecuperar(partyId).orElseThrow { PartyUnregisteredException(partyId) }
    }

    fun intentarRecuperar(partyId: Long): Optional<PartyConMonedas> =
        session.query("SELECT FROM Party WHERE id = ?", partyId)
            .stream()
            .findFirst()
            .map { PartyConMonedas(partyId, it.getProperty("monedas")) }


    /** PRIVATE **/
    private fun validarQueNoEsteRegistradaLaParty(party: PartyConMonedas) {
        intentarRecuperar(party.id).ifPresent { throw PartyAlreadyRegisteredException(party.id) }
    }

}
package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.exceptions.PartyAlreadyRegisteredException
import ar.edu.unq.epers.tactics.exceptions.PartyUnregisteredException
import ar.edu.unq.epers.tactics.modelo.tienda.PartyConMonedas
import com.orientechnologies.orient.core.record.ORecord
import java.util.*
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBDAO as OrientDBDAO1


class OrientDBPartyDAO : OrientDBDAO1<PartyConMonedas>(PartyConMonedas::class.java) {

    override fun guardar(party: PartyConMonedas): PartyConMonedas {
        validarQueNoEsteRegistrada(party)

        val nuevoVertexParty = session.newVertex("PartyConMonedas")
        nuevoVertexParty.setProperty("id", party.id)
        nuevoVertexParty.setProperty("monedas", party.monedas)
        nuevoVertexParty.save<ORecord>()

        return party
    }

    override fun actualizar(party: PartyConMonedas) {
        val query = "UPDATE PartyConMonedas SET monedas = ? WHERE id = ?"
        session.command(query, party.monedas, party.id)
    }

    override fun recuperar(partyId: Long): PartyConMonedas {
        return intentarRecuperar(partyId).orElseThrow { PartyUnregisteredException(partyId) }
    }

    override fun intentarRecuperar(partyId: Long): Optional<PartyConMonedas> =
        session.query("SELECT FROM PartyConMonedas WHERE id = ?", partyId)
            .stream()
            .findFirst()
            .map { PartyConMonedas(partyId, it.getProperty("monedas")) }


    /** PRIVATE **/
    override fun validarQueNoEsteRegistrada(party: PartyConMonedas) {
        intentarRecuperar(party.id).ifPresent { throw PartyAlreadyRegisteredException(party.id) }
    }

}
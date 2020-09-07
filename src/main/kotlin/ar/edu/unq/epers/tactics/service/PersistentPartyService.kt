package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.jdbc.IPartyDAO


class PersistentPartyService(val dao: IPartyDAO) : PartyService {

    override fun crear(party: Party) = dao.crear(party)

    override fun actualizar(party: Party) = dao.actualizar(party)

    override fun recuperar(idDeLaParty: Long) = dao.recuperar(idDeLaParty)

    override fun recuperarTodas() = dao.recuperarTodas()

    override fun agregarAventureroAParty(idDeLaParty: Long, aventurero: Aventurero): Aventurero {
        val party = dao.recuperar(idDeLaParty)
        party.agregarUnAventurero()
        actualizar(party)
        return aventurero
    }

}

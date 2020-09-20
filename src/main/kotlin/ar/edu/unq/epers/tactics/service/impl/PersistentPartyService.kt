package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx


class PersistentPartyService(val dao: PartyDAO) : PartyService {

    override fun crear(party: Party) = runTrx { dao.crear(party) }

    override fun actualizar(party: Party) = runTrx { dao.actualizar(party) }

    override fun recuperar(idDeLaParty: Long) = runTrx { dao.recuperar(idDeLaParty) }

    override fun recuperarTodas() = runTrx { dao.recuperarTodas() }

    override fun agregarAventureroAParty(idDeLaParty: Long, aventurero: Aventurero): Aventurero {
        return  runTrx {
            val party = dao.recuperar(idDeLaParty)
            party.agregarUnAventurero(aventurero)
            actualizar(party)
            aventurero
        }
    }

}

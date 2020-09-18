package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO

class HibernatePartyDAO: PartyDAO {

    override fun crear(party: Party): Party {
        TODO("Not yet implemented")
    }

    override fun actualizar(party: Party) {
        TODO("Not yet implemented")
    }

    override fun recuperar(idDeLaParty: Long): Party {
        TODO("Not yet implemented")
    }

    override fun recuperarTodas(): List<Party> {
        TODO("Not yet implemented")
    }
}
package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner

class HibernatePartyDAO : HibernateDAO<Party>(Party::class.java), PartyDAO {

    override fun recuperarTodas(): MutableList<Party> = queryMany("from Party ORDER BY nombre ASC")

    override fun actualizar(party: Party): Party {
        if (party.id == null) throw  RuntimeException("No se puede actualizar una party que no fue persistida")

        val session = HibernateTransactionRunner.currentSession
        session.update(party)
        return party
    }

}
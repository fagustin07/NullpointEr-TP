package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner

class HibernatePartyDAO: HibernateDAO<Party>(Party::class.java), PartyDAO {

    override fun crear(party: Party): Party {
        val session = HibernateTransactionRunner.currentSession
        val id = session.save(party)
        party.id= id as Long
        return party
    }

    override fun recuperarTodas() =
        queryMany("from Party ORDER BY nombre ASC")

}
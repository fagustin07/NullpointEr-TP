package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner

class HibernatePartyDAO: HibernateDAO<Party>(Party::class.java), PartyDAO {

    override fun recuperarTodas() = queryMany("from Party ORDER BY nombre ASC")

}
package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner

class HibernateAventureroDAO: HibernateDAO<Aventurero>(Aventurero::class.java),AventureroDAO {

    override fun eliminar(aventurero: Aventurero) {
       val session = HibernateTransactionRunner.currentSession
        session.delete(aventurero)
    }
}
package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner

class HibernateAventureroDAO: HibernateDAO<Aventurero>(Aventurero::class.java),AventureroDAO {

    override fun eliminar(aventurero: Aventurero) {
       val session = HibernateTransactionRunner.currentSession
        session.delete(aventurero)
    }

    override fun buda(): Aventurero  =
        createQuery("FROM Aventurero WHERE cantidadDeVecesQueMedito > 0 ORDER BY cantidadDeVecesQueMedito DESC")
            .setMaxResults(1)
            .singleResult

    override fun mejorGuerrero() =
        createQuery(
            """
                select habilidadEmitida.aventureroEmisor
                from Pelea pelea
                join pelea.habilidadesEmitidas habilidadEmitida
                where habilidadEmitida.aventureroEmisor.nombre = 'Pepe'
                """)
            .setMaxResults(1)
            .singleResult
}
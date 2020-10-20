package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner

class HibernateAventureroDAO: HibernateDAO<Aventurero>(Aventurero::class.java),AventureroDAO {

    override fun eliminar(aventurero: Aventurero) {
       val session = HibernateTransactionRunner.currentSession
        session.delete(aventurero)
    }

    override fun buda() =
        createQuery("""
                select habilidadRecibida.aventureroReceptor
                from Pelea pelea
                join pelea.habilidadesRecibidas habilidadRecibida
                where habilidadRecibida.esMeditacion = true
                group by habilidadRecibida.aventureroReceptor
                order by count(*) desc
                """)
            .setMaxResults(1)
            .singleResult

    override fun mejorGuerrero() =
        createQuery(
            """
                select habilidadEmitida.aventureroEmisor
                from Pelea pelea
                join pelea.habilidadesEmitidas habilidadEmitida
                group by habilidadEmitida.aventureroEmisor
                order by sum(habilidadEmitida.dañoFisico) desc
                """)
            .setMaxResults(1)
            .singleResult

    override fun mejorMago() =
        createQuery("""
                select habilidadEmitida.aventureroEmisor
                from Pelea pelea
                join pelea.habilidadesEmitidas habilidadEmitida
                where habilidadEmitida.esAtaqueMagico = true
                group by habilidadEmitida.aventureroEmisor.id
                order by sum(habilidadEmitida.poderMagicoEmisor) desc
                """)
            .setMaxResults(1)
            .singleResult

    override fun mejorCurandero() =
        createQuery("""
                select habilidadRecibida.aventureroEmisor
                from Pelea pelea
                join pelea.habilidadesRecibidas habilidadRecibida
                where habilidadRecibida.esCuracion = true
                group by habilidadRecibida.aventureroEmisor
                order by sum(habilidadRecibida.poderMagicoEmisor) desc
                """)
            .setMaxResults(1)
            .singleResult

}
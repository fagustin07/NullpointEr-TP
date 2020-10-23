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
        buscarAventureroSegun("Receptor", "where habilidad.esMeditacion = true", "count(*)")

    override fun mejorGuerrero() =
        buscarAventureroSegun("Emisor", "", "sum(habilidad.dañoFisico)")

    override fun mejorMago() =
        buscarAventureroSegun("Emisor", "where habilidad.esAtaqueMagico = true", "sum(habilidad.poderMagicoEmisor)")

    override fun mejorCurandero() =
        buscarAventureroSegun("Emisor", "where habilidad.esCuracion = true", "sum(habilidad.poderMagicoEmisor)")

    private fun buscarAventureroSegun(rolDelAventurero: String, criterioDeSeleccionSobreHabilidad: String, criterioDeOrdenacion: String) =
        createQuery(
            """
                select habilidad.aventurero${rolDelAventurero}
                from Pelea pelea
                join pelea.habilidades${if (rolDelAventurero == "Emisor") "Emitidas" else "Recibidas"} habilidad
                ${criterioDeSeleccionSobreHabilidad}
                group by habilidad.aventurero${rolDelAventurero}
                order by ${criterioDeOrdenacion} desc
                """)
            .setMaxResults(1)
            .singleResult
}
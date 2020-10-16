package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner

class HibernatePartyDAO : HibernateDAO<Party>(Party::class.java), PartyDAO {

    override fun recuperarTodas(): MutableList<Party> = queryMany("from Party ORDER BY nombre ASC")

    override fun actualizar(party: Party): Party {
        if (party.id() == null) throw  RuntimeException("No se puede actualizar una party que no fue persistida")

        val session = HibernateTransactionRunner.currentSession
        session.update(party)
        return party
    }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int): List<Party> {
        when(orden){
            Orden.PODER -> return recuperarOrdenadasPorPoder(direccion,pagina)
            Orden.VICTORIAS -> return recuperarOrdenadasPorVictorias(direccion,pagina)
            else -> return listOf()
        }
    }
    private fun recuperarOrdenadasPorPoder(direccion: Direccion, pagina: Int): List<Party> {
            val primerResultado = 10 * pagina
            return createQuery("select party from Party party " +
                    "join party.aventureros aventurero " +
                    "group by party.id " +
                    "order by aventurero.poderTotal " + setDir(direccion))
                .setMaxResults(10)
                .setFirstResult(primerResultado)
                .list()
    }

    private fun recuperarOrdenadasPorVictorias(direccion: Direccion,pagina: Int):List<Party>{
        val primerResultado = 10 * pagina
        return createQuery("select party from Pelea pelea " +
                "join pelea.party party " +
                "group by party.id " +
                "order by count(pelea.estaGanada) " + setDir(direccion))
                .setMaxResults(10)
                .setFirstResult(primerResultado)
                .list()
    }


    private fun setDir(direccion: Direccion):String{
        when(direccion){
            Direccion.ASCENDENTE -> return "asc"
            Direccion.DESCENDENTE -> return  "desc"
        }
    }

}
package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.service.PeleasPaginadas
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner

class HibernatePartyDAO : HibernateDAO<Party>(Party::class.java), PartyDAO {

    override fun recuperarTodas(): MutableList<Party> = queryMany("from Party ORDER BY nombre ASC")

    override fun actualizar(party: Party): Party {
        if (party.id() == null) throw  RuntimeException("No se puede actualizar una party que no fue persistida")

        val session = HibernateTransactionRunner.currentSession
        session.update(party)
        return party
    }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int):PartyPaginadas {
        val primerResultado = 10 * pagina
        val partys = createQuery(consulta(orden,direccion))
            .setMaxResults(10)
            .setFirstResult(primerResultado)
            .list()

        return PartyPaginadas(partys,totalPartys())
    }

    private fun totalPartys(): Int{
        return createQuery("select party from Party party"
        ).list().size

    }

    private fun setDir(direccion: Direccion):String{
        when(direccion){
            Direccion.ASCENDENTE -> return "asc"
            Direccion.DESCENDENTE -> return  "desc"
        }
    }

    private fun consulta(orden: Orden, direccion: Direccion):String{
        when(orden) {
            Orden.PODER -> return consultaHqlPoder() + setDir(direccion)
            Orden.VICTORIAS -> return  consultaHqlVictorias() + setDir(direccion)
            else -> return  ""
        }
    }
    private fun consultaHqlPoder() = "select party from Party party join party.aventureros aventurero group by party.id order by aventurero.poderTotal "
    private fun consultaHqlVictorias() = "select party from Pelea pelea join pelea.party party group by party.id order by count(pelea.estaGanada) "
}
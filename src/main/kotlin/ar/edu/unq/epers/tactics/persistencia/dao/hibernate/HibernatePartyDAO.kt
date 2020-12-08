package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.EstadoPartida
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import javax.persistence.NoResultException

class HibernatePartyDAO : HibernateDAO<Party>(Party::class.java), PartyDAO {

    override fun recuperarTodas(): MutableList<Party> = queryMany("from Party ORDER BY nombre ASC")

    override fun actualizar(party: Party): Party {
        if (party.id() == null) throw  RuntimeException("No se puede actualizar una party que no fue persistida")

        val session = HibernateTransactionRunner.currentSession
        session.update(party)
        return party
    }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int):List<Party> {
        val primerResultado = 10 * pagina
        when(orden){
            Orden.PODER -> return recuperarPorPoder(direccion,primerResultado)
            else -> {
                val estadoPartida = estadoPartidaSegunCorresponda(orden)
                return recuperarSegunEstadoPartida(estadoPartida,direccion,primerResultado)
            }
        }
    }

    private fun recuperarPorPoder(direccion: Direccion, primerResultado: Int) =
            createQuery("select party " +
                    "from Party party " +
                    "left join party.aventureros aventurero " +
                    "group by party.id " +
                    "order by sum(aventurero.poderTotal) ${direccion.keywordHql()}")
                    .setMaxResults(10)
                    .setFirstResult(primerResultado)
                    .list()

    private fun recuperarSegunEstadoPartida(estadoPartida: EstadoPartida, direccion: Direccion, primerResultado:Int): List<Party> =
            createQuery("select party " +
                "from Pelea pelea " +
                "join pelea.party party " +
                "where pelea.estadoPartida = :orden " +
                "group by party.id " +
                "order by count(*) ${direccion.keywordHql()}")
                .setParameter("orden", estadoPartida)
                .setMaxResults(10)
                .setFirstResult(primerResultado)
                .list()

    override fun cantidadDePartys() = cantidadDeEntidades()

    private fun estadoPartidaSegunCorresponda(orden: Orden): EstadoPartida {
        when(orden){
            Orden.VICTORIAS -> return EstadoPartida.GANADA
            else -> return EstadoPartida.PERDIDA
        }
    }

    override fun recuperarPorNombre(nombre: String) : Party {
        return try {
            createQuery("from Party where nombre = :nombre")
                .setParameter("nombre", nombre)
                .singleResult
        } catch (e: NoResultException) {
            throw RuntimeException("No existe ${entityType.simpleName} con nombre ${nombre}")
        }
    }

}
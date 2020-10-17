package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.EstadoPartida
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
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
        when(orden){
            Orden.PODER -> return PartyPaginadas(consultaPoder(direccion,primerResultado),totalPartys())
            else -> return PartyPaginadas(consultaVictoriasODerrotas(orden,direccion,primerResultado), totalPartys())
        }
    }

    private fun consultaPoder(direccion: Direccion,primerResultado: Int) =
            createQuery("select party " +
                    "from Party party " +
                    "join party.aventureros aventurero " +
                    "group by party.id " +
                    "order by sum(aventurero.poderTotal) ${setDir(direccion)}")
                    .setMaxResults(10)
                    .setFirstResult(primerResultado)
                    .list()

    private fun consultaVictoriasODerrotas(orden:Orden,direccion: Direccion,primerResultado:Int): List<Party> {
        val estadoPartida = estadoPartidaSegunCorresponda(orden)
        return createQuery("select party " +
                "from Pelea pelea " +
                "join pelea.party party " +
                "where pelea.estadoPartida = :orden " +
                "group by party.id " +
                "order by count(pelea.estadoPartida) ${setDir(direccion)}")
                .setParameter("orden", estadoPartida)
                .setMaxResults(10)
                .setFirstResult(primerResultado)
                .list()

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

    private fun estadoPartidaSegunCorresponda(orden: Orden): EstadoPartida {
        when(orden){
            Orden.VICTORIAS -> return EstadoPartida.GANADA
            else -> return EstadoPartida.PERDIDA
        }
    }

//    fun consulta2(deLaTabla:String,joineadaCon:String,ordenadaPor:String): String{
//        "select party " +
//        "from ${deLaTabla} " +
//        "join ${joineadaCon} " +
//        "group by party.id " +
//        "order by ${ordenadaPor} "
//    }


}
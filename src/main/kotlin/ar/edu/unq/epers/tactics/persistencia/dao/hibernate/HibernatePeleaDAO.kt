package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO

class HibernatePeleaDAO: HibernateDAO<Pelea>(Pelea::class.java), PeleaDAO {

    override fun recuperarUltimaPeleaDeParty(idDeLaParty: Long): Pelea {
        return createQuery("from Pelea pelea where pelea.party.id = :idDeLaParty order by fecha desc")
                .setParameter("idDeLaParty", idDeLaParty)
                .setMaxResults(1)
                .singleResult
    }

    override fun recuperarOrdenadas(partyId: Long, pagina: Int): List<Pelea> {
        val primerResultado = 10 * pagina
        return createQuery("from Pelea pelea where pelea.party.id = :partyId order by fecha desc")
                .setParameter("partyId", partyId)
                .setMaxResults(10)
                .setFirstResult(primerResultado)
                .list()
    }

}
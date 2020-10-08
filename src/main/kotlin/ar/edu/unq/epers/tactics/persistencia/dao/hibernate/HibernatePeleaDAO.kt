package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO

class HibernatePeleaDAO: HibernateDAO<Pelea>(Pelea::class.java), PeleaDAO {

    override fun recuperarUltimaPeleaDeParty(idDeLaParty: Long): Pelea {
        return createQuery("from Pelea pelea where pelea.party.id = :idDeLaParty order by id desc")
                .setParameter("idDeLaParty", idDeLaParty)
                .setMaxResults(1)
                .singleResult
    }

}
package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO

class HibernatePeleaDAO: HibernateDAO<Pelea>(Pelea::class.java), PeleaDAO {

    override fun recuperarPeleaDeParty(idDeLaParty: Long): Pelea {
        return createQuery("from Pelea where idDeLaParty = $idDeLaParty").singleResult
    }

}
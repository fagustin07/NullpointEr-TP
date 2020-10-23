package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO

class HibernatePeleaDAO: HibernateDAO<Pelea>(Pelea::class.java), PeleaDAO {

    override fun recuperarUltimaPeleaDeParty(partyId: Long) =
        queryDePeleasDeParty(partyId)
            .setMaxResults(1)
            .singleResult

    override fun recuperarOrdenadas(partyId: Long, pagina: Int) =
        queryDePeleasDeParty(partyId)
            .setMaxResults(10)
            .setFirstResult(10 * pagina)
            .list()

    fun queryDePeleasDeParty(partyId: Long) =
        createQuery("""
            from Pelea pelea
            where pelea.party.id = :partyId
            order by fecha desc""")
            .setParameter("partyId", partyId)

    override fun cantidadDePeleas() = cantidadDeEntidades()

}
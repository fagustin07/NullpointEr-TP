package ar.edu.unq.epers.tactics.spring.configuration


import ar.edu.unq.epers.tactics.persistencia.dao.*
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBPartyDAO
import ar.edu.unq.epers.tactics.service.*
import ar.edu.unq.epers.tactics.service.impl.AventureroLeaderboardServiceImpl
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PeleaServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfiguration {

    @Bean
    fun groupName() : String {
        val groupName :String?  = System.getenv()["GROUP_NAME"]
        if (groupName == null)
            return "NULL_POINTER"
        else
            return groupName!!
    }


    @Bean
    fun partyDAO() : PartyDAO {
        return HibernatePartyDAO()
    }

    @Bean
    fun adventurerDAO() : AventureroDAO {
        return HibernateAventureroDAO()
    }

    @Bean
    fun fightDAO() : PeleaDAO {
        return HibernatePeleaDAO()
    }

    @Bean
    fun partyService(partyDAO: PartyDAO) : PartyService {
        return PartyServiceImpl(partyDAO)
    }

    @Bean
    fun adventurerService(aventureroDAO: AventureroDAO, partyDAO: PartyDAO) : AventureroService {
        return AventureroServiceImpl(aventureroDAO, partyDAO)
    }

    @Bean
    fun adventurerLeaderboardService(aventureroDAO: AventureroDAO) : AventureroLeaderboardService {
        return AventureroLeaderboardServiceImpl(aventureroDAO)
    }

    @Bean
    fun fightService(peleaDAO: PeleaDAO, partyDAO: PartyDAO, aventureroDAO: AventureroDAO) : PeleaService {
        return PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO, OrientDBPartyDAO())
    }

}
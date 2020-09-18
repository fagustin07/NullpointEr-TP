package ar.edu.unq.epers.tactics.spring.configuration


import ar.edu.unq.epers.tactics.persistencia.dao.*
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.service.*
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PeleaServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PersistentPartyService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfiguration {

    @Bean
    fun groupName() : String {
        val groupName :String?  = System.getenv()["GROUP_NAME"]
        return groupName!!
    }


    @Bean
    fun partyDAO() : IPartyDAO {
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
    fun partyService(partyDAO: IPartyDAO) : PartyService {
        return PersistentPartyService(partyDAO)
    }

    @Bean
    fun adventurerService(aventureroDAO: AventureroDAO, partyDAO: IPartyDAO) : AventureroService {
        return AventureroServiceImpl(aventureroDAO, partyDAO)
    }

    @Bean
    fun fightService(peleaDAO: PeleaDAO, partyDAO: IPartyDAO, aventureroDAO: AventureroDAO) : PeleaService {
        return PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO)
    }
}
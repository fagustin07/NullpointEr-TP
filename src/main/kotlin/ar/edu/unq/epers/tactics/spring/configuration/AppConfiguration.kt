package ar.edu.unq.epers.tactics.spring.configuration


import ar.edu.unq.epers.tactics.modelo.calendario.AlmanaqueReal
import ar.edu.unq.epers.tactics.persistencia.dao.*
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBInventarioPartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBItemDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBOperacionesDAO
import ar.edu.unq.epers.tactics.service.*
import ar.edu.unq.epers.tactics.service.impl.*
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
    fun itemDAO(): ItemDAO {
        return OrientDBItemDAO(AlmanaqueReal())
    }

    @Bean
    fun inventarioPartyDAO(): InventarioPartyDAO {
        return OrientDBInventarioPartyDAO()
    }

    @Bean
    fun operacionesDAO(): OperacionesDAO {
        return OrientDBOperacionesDAO(AlmanaqueReal())
    }

    @Bean
    fun partyService(partyDAO: PartyDAO, inventarioPartyDAO: InventarioPartyDAO) : PartyService {
        return PartyServiceImpl(partyDAO, inventarioPartyDAO)
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
    fun fightService(peleaDAO: PeleaDAO, partyDAO: PartyDAO, aventureroDAO: AventureroDAO, inventarioPartyDAO: InventarioPartyDAO) : PeleaService {
        return PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO, inventarioPartyDAO)
    }

    @Bean
    fun tiendaService(inventarioPartyDAO: InventarioPartyDAO, itemDAO: ItemDAO, operacionesDAO: OperacionesDAO, partyService: PartyService): TiendaService {
        return TiendaServicePersistente(inventarioPartyDAO, itemDAO, operacionesDAO, partyService)
    }
}
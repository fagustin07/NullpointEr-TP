package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.service.PartyService

class PartyServiceImpl(val partyDAO: PartyDAO): PartyService {

    override fun crear(party: Party): Party {
        TODO("Not yet implemented")
    }

    override fun actualizar(party: Party): Party {
        TODO("Not yet implemented")
    }

    override fun recuperar(idDeLaParty: Long): Party {
        TODO("Not yet implemented")
    }

    override fun recuperarTodas(): List<Party> {
        TODO("Not yet implemented")
    }

    override fun agregarAventureroAParty(idDeLaParty: Long, aventurero: Aventurero): Aventurero {
        TODO("Not yet implemented")
    }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?): PartyPaginadas {
        TODO("Not yet implemented")
    }
}
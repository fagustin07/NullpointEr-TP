package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Habilidad
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PeleaService
import ar.edu.unq.epers.tactics.service.PeleasPaginadas

class PeleaServiceImpl(val peleaDAO: PeleaDAO, val partyDAO: PartyDAO, val aventureroDAO: AventureroDAO): PeleaService {

    override fun iniciarPelea(idDeLaPelea: Long, partyEnemiga:String): Pelea {
        TODO("Not yet implemented")
    }

    override fun resolverTurno(peleaId: Long, aventureroId: Long, enemigos: List<Aventurero>): Habilidad {
        TODO("Not yet implemented")
    }

    override fun recibirHabilidad(aventureroId: Long, habilidadId: Habilidad): Aventurero {
        TODO("Not yet implemented")
    }

    override fun terminarPelea(idDeLaPelea: Long): Pelea {
        TODO("Not yet implemented")
    }

    override fun recuperarOrdenadas(partyId: Long, pagina: Int?): PeleasPaginadas {
        TODO("Not yet implemented")
    }
}
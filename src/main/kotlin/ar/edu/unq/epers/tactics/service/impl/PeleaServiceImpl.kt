package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Habilidad
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.IPartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PeleaService

class PeleaServiceImpl(val peleaDAO: PeleaDAO, val partyDAO: IPartyDAO, val aventureroDAO: AventureroDAO): PeleaService {

    override fun iniciarPelea(idDeLaPelea: Long): Pelea {
        TODO("Not yet implemented")
    }

    override fun actualizar(pelea: Pelea): Pelea {
        TODO("Not yet implemented")
    }

    override fun recuperar(idDeLaPelea: Long): Pelea {
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
}
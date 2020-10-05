package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PeleaService

class PeleaServiceImpl(val peleaDAO: PeleaDAO, val partyDAO: PartyDAO, val aventureroDAO: AventureroDAO): PeleaService {

    override fun iniciarPelea(idDeLaParty: Long): Pelea {
        val party = partyDAO.recuperar(idDeLaParty)
        party.entrarEnPelea()
        partyDAO.actualizar(party)

        return peleaDAO.crear(Pelea(idDeLaParty))

    }

    override fun estaEnPelea(partyId: Long): Boolean {
        return partyDAO.recuperar(partyId).estaEnPelea()
    }

    override fun actualizar(pelea: Pelea): Pelea {
        TODO("Not yet implemented")
    }

    override fun recuperar(idDeLaPelea: Long): Pelea {
        TODO("Not yet implemented")
    }

    override fun resolverTurno(peleaId: Long, aventureroId: Long, enemigos: List<Aventurero>): Habilidad {
        val aventurero = aventureroDAO.recuperar(aventureroId)
        val habilidadGenerada = aventurero.resolverTurno(enemigos)
        aventureroDAO.actualizar(aventurero)
        return habilidadGenerada
    }

    override fun recibirHabilidad(aventureroId: Long, habilidadId: Habilidad): Aventurero {
        TODO("Not yet implemented")
    }

    override fun terminarPelea(idDeLaParty: Long): Pelea {
        TODO("Not yet implemented")
    }
}
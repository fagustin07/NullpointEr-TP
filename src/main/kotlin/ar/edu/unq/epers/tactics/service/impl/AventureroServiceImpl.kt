package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.AventureroService

class AventureroServiceImpl(val aventureroDAO: AventureroDAO, val partyDAO: PartyDAO): AventureroService {

    override fun actualizar(aventurero: Aventurero): Aventurero {
        TODO("Not yet implemented")
    }

    override fun recuperar(idDelAventurero: Long): Aventurero {
        TODO("Not yet implemented")
    }

    override fun eliminar(aventurero: Aventurero) {
        TODO("Not yet implemented")
    }
}
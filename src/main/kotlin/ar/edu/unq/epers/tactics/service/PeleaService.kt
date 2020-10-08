package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.modelo.Pelea

interface PeleaService {
    fun iniciarPelea(idDeLaParty: Long) : Pelea
    fun estaEnPelea(partyId: Long):Boolean
    fun resolverTurno(peleaId: Long, aventureroId:Long, enemigos:List<Aventurero>): Habilidad
    fun recibirHabilidad(aventureroId: Long, habilidad: Habilidad): Aventurero
    fun terminarPelea(idDeLaParty: Long): Pelea
}
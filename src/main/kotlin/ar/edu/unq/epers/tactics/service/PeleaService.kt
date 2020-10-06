package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.modelo.Pelea

interface PeleaService {
    fun iniciarPelea(idDeLaParty: Long) : Pelea
    fun estaEnPelea(partyId: Long):Boolean
    fun resolverTurno(peleaId: Long, aventureroId:Long, enemigos:List<Aventurero>): Habilidad
    //TODO: el resolveTurn(...) de la API genera una HabilidadDTO?, adaptar el codigo para que
    // si el aventurero no puede generar una habilidad simplemente retorne null, lo que
    // implica tambien hacer recibirHabilidad(av, habilidad:Habilidad?):Aventurero, esto
    // ya lo hablé con Ronny en Discord

    fun recibirHabilidad(aventureroId: Long, habilidad: Habilidad): Aventurero
    fun terminarPelea(idDeLaParty: Long): Pelea
    fun actualizar(pelea: Pelea): Pelea
    fun recuperar(idDeLaPelea: Long): Pelea
}
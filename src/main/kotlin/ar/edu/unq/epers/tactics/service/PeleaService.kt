package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Habilidad
import ar.edu.unq.epers.tactics.modelo.Pelea

interface PeleaService {
    fun iniciarPelea(idDeLaPelea: Long) : Pelea
    fun estaEnPelea(partyId: Double):Boolean
    fun resolverTurno(peleaId: Long, aventureroId:Long, enemigos:List<Aventurero>): Habilidad
    fun recibirHabilidad(aventureroId: Long, habilidadId:Habilidad): Aventurero
    fun terminarPelea(idDeLaPelea: Long): Pelea
    fun actualizar(pelea: Pelea): Pelea
    fun recuperar(idDeLaPelea: Long): Pelea
}
package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Habilidad
import ar.edu.unq.epers.tactics.modelo.Pelea

interface PeleaService {
    fun crear(idDeLaPelea: Long) : Pelea
    fun actualizar(pelea: Pelea):Pelea
    fun recuperar(idDeLaPelea: Long): Pelea
    fun resolverTurno(peleaId: Long, aventureroId:Long, enemigos:List<Aventurero>): Habilidad
    fun recibirHabilidad(aventureroId: Long, habilidadId:Habilidad): Aventurero
    fun terminar(idDeLaPelea: Long): Pelea
}
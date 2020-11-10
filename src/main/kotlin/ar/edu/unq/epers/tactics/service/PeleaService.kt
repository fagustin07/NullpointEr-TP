package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.modelo.Pelea

interface PeleaService {
    fun iniciarPelea(partyId: Long, nombrePartyEnemiga:String) : Pelea
    fun estaEnPelea(partyId: Long):Boolean
    fun resolverTurno(peleaId: Long, aventureroId:Long, enemigos:List<Aventurero>): Habilidad
    fun recibirHabilidad(peleaId: Long, aventureroId: Long, habilidad: Habilidad): Aventurero
    fun terminarPelea(idDeLaPelea: Long): Pelea
    fun recuperarOrdenadas(partyId:Long, pagina:Int?):PeleasPaginadas
}

class PeleasPaginadas(var peleas:List<Pelea>, var total:Int)
package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party

interface PartyService {
    fun crear(party: Party) : Party
    fun actualizar(party: Party):Party
    fun recuperar(idDeLaParty: Long): Party
    fun recuperarTodas(): List<Party>
    fun agregarAventureroAParty(idDeLaParty: Long, aventurero: Aventurero) : Aventurero
}
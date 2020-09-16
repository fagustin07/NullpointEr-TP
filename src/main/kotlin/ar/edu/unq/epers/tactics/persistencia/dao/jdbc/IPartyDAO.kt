package ar.edu.unq.epers.tactics.persistencia.dao.jdbc

import ar.edu.unq.epers.tactics.modelo.Party

interface IPartyDAO {
    fun crear(party: Party) : Long
    fun actualizar(party: Party)
    fun recuperar(idDeLaParty: Long): Party
    fun recuperarTodas(): List<Party>
}
package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Pelea

interface PeleaDAO: DAO<Pelea> {
    fun recuperarUltimaPeleaDeParty(idDeLaParty: Long) : Pelea
    fun recuperarOrdenadas(partyId: Long, pagina: Int): List<Pelea>
    fun cantidadDePeleas(): Long
}
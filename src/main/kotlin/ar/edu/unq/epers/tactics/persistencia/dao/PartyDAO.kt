package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Party

interface PartyDAO: DAO<Party> {
    fun recuperarTodas(): List<Party>
    fun eliminarTodo()
}
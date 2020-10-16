package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden

interface PartyDAO: DAO<Party> {
    fun recuperarTodas(): List<Party>
    fun eliminarTodo()
    fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int): List<Party>
}
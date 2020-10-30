package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Clase

interface ClaseDAO : DAO<Clase> {
    override fun crear(entity: Clase): Clase

    override fun actualizar(entity: Clase): Clase

    override fun recuperar(id: Long): Clase

    fun recuperarTodas(): List<Clase>
}
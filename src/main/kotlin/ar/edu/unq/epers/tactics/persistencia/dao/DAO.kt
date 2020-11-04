package ar.edu.unq.epers.tactics.persistencia.dao

interface DAO<T> {
    fun crear(entity:T) : T
    fun actualizar(entity: T): T
    fun recuperar(id: Long): T

    fun ejecutarCon(entityID: Long, block: (T) -> Unit): T {
        val entity = recuperar(entityID)
        block(entity)
        return actualizar(entity)
    }

    fun <U> resultadoDeEjecutarCon(entityID: Long, block: (T) -> U): U {
        val entity = recuperar(entityID)
        val resultado = block(entity)
        actualizar(entity)
        return resultado
    }
}

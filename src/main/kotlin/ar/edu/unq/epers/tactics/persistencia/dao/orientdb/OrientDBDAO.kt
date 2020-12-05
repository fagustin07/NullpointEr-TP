package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import com.orientechnologies.orient.core.db.ODatabaseSession
import java.util.*

abstract class OrientDBDAO<T>(val entityType: Class<T>) {

    val session: ODatabaseSession get() = OrientDBSessionFactoryProvider.instance.session

    abstract fun guardar(entity: T): T

    abstract fun actualizar(entity: T)

    abstract fun recuperar(entityName: String): T

    abstract fun intentarRecuperar(entityName: String): Optional<T>

    /** PRIVATE **/
    protected abstract fun validarQueNoEsteRegistrada(entity: T)
}
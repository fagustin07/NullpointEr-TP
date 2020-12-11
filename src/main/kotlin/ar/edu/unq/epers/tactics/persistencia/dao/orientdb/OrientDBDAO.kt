package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.record.ORecord
import com.orientechnologies.orient.core.record.OVertex
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.sql.executor.OResult
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.fold
import kotlin.streams.toList

abstract class OrientDBDAO<T>(val entityType: Class<T>) {

    val session: ODatabaseSession get() = OrientDBSessionFactoryProvider.instance.session

    open fun guardar(entity: T): T {
        val vertex = mapearAVertex(entity)
        validarQueNoExistaEntidadLlamada(vertex.getProperty("nombre"))
        vertex.save<ORecord>()

        return entity
    }

    open fun recuperar(entityName: String): T {
        return intentarRecuperar(entityName).orElseThrow { RuntimeException("No existe un ${entityType.simpleName} llamado ${entityName} en el sistema.") }
    }

    open fun intentarRecuperar(entityName: String): Optional<T> =
        session.query("SELECT FROM ? WHERE nombre = ? LIMIT 1", entityType.simpleName, entityName)
            .stream()
            .findFirst()
            .map { mapearAEntidad(it) }

    open fun clear() {
        session.command("DELETE VERTEX ?", entityType.simpleName)
    }

    protected abstract fun mapearAEntidad(oResult: OResult): T

    protected open fun mapearAVertex(entity: T): OVertex {
        return entityType.declaredFields
            .map { it.name to entityType.getDeclaredMethod("get" + it.name.capitalize()).invoke(entity) }
            .fold(session.newVertex(entityType.simpleName)) { vertex, it ->
                vertex.setProperty(it.first, it.second)
                vertex
            }
    }

    private fun validarQueNoExistaEntidadLlamada(entityName: String) {
        intentarRecuperar(entityName).ifPresent { throw RuntimeException("Ya existe un ${entityType.simpleName} llamado ${entityName} en el sistema.") }
    }

}
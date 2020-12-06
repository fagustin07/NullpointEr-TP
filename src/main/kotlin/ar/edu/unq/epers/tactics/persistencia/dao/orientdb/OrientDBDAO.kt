package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.modelo.tienda.InventarioParty
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.record.ORecord
import com.orientechnologies.orient.core.sql.executor.OResult
import java.lang.RuntimeException
import java.util.*

abstract class OrientDBDAO<T>(val entityType: Class<T>) {

    val session: ODatabaseSession get() = OrientDBSessionFactoryProvider.instance.session

    //abstract fun guardar(entity: T): T
    fun guardar(entity: T): T {
        entity as InventarioParty // TODO: despues generalizar esto

       validarQueNoExistaEntidadLlamada(entity.nombre)

        val nuevoVertexParty = session.newVertex("InventarioParty")
        nuevoVertexParty.setProperty("nombre", entity.nombre)
        nuevoVertexParty.setProperty("monedas", entity.monedas)
        nuevoVertexParty.save<ORecord>()

        return entity
    }

    abstract fun actualizar(entity: T)
    /*open fun actualizar(entity: T) {
        val inventarioParty = entity as InventarioParty
        //entityType.declaredFields.map { it }
        entity.javaClass.kotlin.declaredMemberProperties.map {
            it
        }

        val query = "UPDATE ${entityType.simpleName} SET monedas = ? WHERE nombre = ?"
        session.command(query, inventarioParty.monedas, inventarioParty.nombreParty)
    }*/

    open fun recuperar(entityName: String): T {
        return intentarRecuperar(entityName).orElseThrow { RuntimeException(mensajeDeErrorParaEntidadNoEncontrada(entityName)) }
    }

    open fun intentarRecuperar(entityName: String): Optional<T> =
        session.query("SELECT FROM ? WHERE nombre = ?", entityType.simpleName, entityName)
            .stream()
            .findFirst()
            .map { mapEntidadDesdeOResult(it) }

    open fun clear() {
        session.command("DELETE VERTEX ?", entityType.simpleName)
    }


    abstract fun mensajeDeErrorParaEntidadNoEncontrada(entityName: String): String
    abstract fun mensajeDeErrorParaNombreDeEntidadYaRegistrado(entityName: String): String

    protected abstract fun mapEntidadDesdeOResult(it: OResult): T // TODO: resolver con metaprogramacion

    /** PRIVATE **/
    private fun validarQueNoExistaEntidadLlamada(entityName: String) {
        intentarRecuperar(entityName).ifPresent { throw RuntimeException(mensajeDeErrorParaNombreDeEntidadYaRegistrado(entityName)) }
    }



}
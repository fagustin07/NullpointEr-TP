package ar.edu.unq.epers.tactics.persistencia.dao.orientdb


import ar.edu.unq.epers.tactics.exceptions.InexistentItemException
import ar.edu.unq.epers.tactics.exceptions.ItemAlreadyRegisteredException
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.record.ORecord
import java.util.*


class OrientDBItemDAO {

    val session: ODatabaseSession get() = OrientDBSessionFactoryProvider.instance.session

    fun guardar(item: Item): Item {
        validarQueNoExistaAlgunItemLlamado(item.nombre)

        val result = session.newVertex("Item")
        result.setProperty("nombre", item.nombre)
        result.setProperty("precio", item.precio)
        result.save<ORecord>()

        return item // TODO: deberia retornar el item con el id que le dio la base de datos
    }

    fun recuperar(nombre: String): Item {
        return intentarRecuperar(nombre).orElseThrow { InexistentItemException(nombre) }
    }

    fun intentarRecuperar(nombre: String): Optional<Item> =
        session.query("SELECT FROM Item WHERE nombre = ?", nombre)
            .stream()
            .findFirst()
            .map { Item(it.getProperty("nombre"), it.getProperty("precio")) }


    private fun validarQueNoExistaAlgunItemLlamado(nombre: String) {
        val query = "SELECT FROM Item WHERE nombre = ?"
        val queryResult = session.query(query, nombre)
        if (queryResult.hasNext()) throw ItemAlreadyRegisteredException(nombre)
    }
}
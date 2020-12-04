package ar.edu.unq.epers.tactics.persistencia.dao.orientdb


import ar.edu.unq.epers.tactics.exceptions.InexistentItemException
import ar.edu.unq.epers.tactics.exceptions.ItemAlreadyRegisteredException
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import com.orientechnologies.orient.core.record.ORecord


class OrientDBItemDAO {
    val session: ODatabaseSession get() = OrientDBSessionFactoryProvider.instance.session

    fun registrar(nombre: String, precio: Int): Item {
        //if(session.getClass("Item")==null) session.createVertexClass("Item")

        validarQueNoExistaAlgunItemLlamado(nombre)

        val result = session.newVertex("Item")
        result.setProperty("nombre", nombre)
        result.setProperty("precio", precio)
        result.save<ORecord>()

        return Item(nombre, precio)
    }

    fun recuperar(nombre: String): Item {
        val query = "SELECT FROM Item WHERE nombre = ?"
        val rs = session.query(query, nombre)

        lateinit var item : Item
        if (rs.hasNext()) {
            val partyPersistida = rs.next()
            val precio = partyPersistida.getProperty<Int>("precio")

            item = Item(nombre,precio)
        } else {
            throw InexistentItemException(nombre)
        }
        return item
    }

    private fun validarQueNoExistaAlgunItemLlamado(nombre: String) {
        val query = "SELECT FROM Item WHERE nombre = ?"
        val queryResult = session.query(query, nombre)
        if (queryResult.hasNext()) throw ItemAlreadyRegisteredException(nombre)
    }

}

class Item(val nombre: String, val precio: Int)

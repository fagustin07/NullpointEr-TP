package ar.edu.unq.epers.tactics.persistencia.dao.orientdb


import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.record.ORecord


class OrientDBItemDAO {
    val db: ODatabaseSession get() = OrientDBSessionFactoryProvider.instance.db

    fun registrar(nombre: String, precio: Int): Item {

        val result = db.newVertex("Item")
        result.setProperty("nombre", nombre)
        result.setProperty("precio", precio)
        result.save<ORecord>()

        return Item(nombre, precio)
    }

    fun recuperar(nombre: String): Item {
        val query = "SELECT FROM Item WHERE nombre = ?"
        val rs = db.query(query, nombre)

        lateinit var item : Item
        if (rs.hasNext()) {
            val partyPersistida = rs.next()
            val precio = partyPersistida.getProperty<Int>("precio")

            item = Item(nombre,precio)
        } else {
            throw RuntimeException("No existe el objeto llamado ${nombre}.")
        }
        return item
    }

    fun removeAll() {
        db.command("DELETE VERTEX FROM Item")
    }

}

class Item(val nombre: String, val precio: Int)

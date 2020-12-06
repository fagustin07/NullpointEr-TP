package ar.edu.unq.epers.tactics.persistencia.dao.orientdb


import ar.edu.unq.epers.tactics.exceptions.InexistentItemException
import ar.edu.unq.epers.tactics.exceptions.ItemAlreadyRegisteredException
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.record.ORecord
import java.util.*
import kotlin.streams.toList


class OrientDBItemDAO {

    val session: ODatabaseSession get() = OrientDBSessionFactoryProvider.instance.session

    fun guardar(item: Item): Item {
        validarQueNoExistaAlgunItemLlamado(item.nombre())

        val result = session.newVertex("Item")
        result.setProperty("nombre", item.nombre())
        result.setProperty("precio", item.precio())
        result.save<ORecord>()

        return item
        // TODO: ¿¿¿deberia retornar el item con el id que le dio la base de datos???
                    // Mauro: Entiendo que no la usamos, pero si devolvemos el Item sin ID estariamos teniendo un objeto "incompleto" en memoria thinking es discutible
                    // Fede: Mi opinión es que podriamos dejarlo como está y seguir usando nombres, o bien recuperarla y retornarla con rid, pero en los otros mensajes usar el rid para buscarlo en vez del nombre. Sin embargo, es un refactor que se puede dejar para ver mas adelante
                    // David: Ok
                    // [Deje un mensaje en Telegram. Despues de rebasear y mergear se vera que se hace]
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

    fun loMasComprado(): List<Pair<Item, Int>> {
        val query =
            """
                    SELECT *, in().size() AS vecesComprado 
                    FROM Item
                    WHERE in().size() > 0
                    ORDER BY vecesComprado DESC LIMIT 10
                """

        val result: List<Pair<Item, Int>> = session.query(query)
            .stream()
            .map {
                val item = Item(it.getProperty("nombre"), it.getProperty("precio"))
                val vecesComprado: Int = it.getProperty("vecesComprado")
                Pair(item, vecesComprado)
            }
            .toList()

        return result
    }

}
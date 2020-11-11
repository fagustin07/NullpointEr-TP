package ar.edu.unq.epers.tactics.persistencia.dao.mongodb

import ar.edu.unq.epers.tactics.exceptions.DuplicateFormationException
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.client.model.Filters.*
import org.bson.Document

class MongoFormacionDAO : MongoDAO<Formacion>(Formacion::class.java), FormacionDAO {

    /*ACTIONS*/
    override fun guardar(formacion: Formacion): Formacion {
        this.validarSiExisteLaFormacion(formacion)

        this.save(formacion)
        return buscarFormacion(formacion)!!
    }

    override fun getAll(): List<Formacion> {
        return collection.find().into(mutableListOf())
    }

    override fun formacionesQuePosee(party: Party): List<Formacion> {
        val clases = ObjectMapper().writer().writeValueAsString(party.aventureros().map { it.clases() }.flatten()) // [Aventurero, Magico, Aventurero]
        val filterType = type("requerimientos", "array")
        val filterExpression = nor(Document.parse("{ \"requerimientos\" :{\$elemMatch:{\$nin: ${clases}}}}"))

        return this.find(and(filterType, filterExpression))
    }

    /*PRIVATE*/

    /*ACTIONS*/
    private fun buscarFormacion(formacion: Formacion) =
        this.getBy("nombre", formacion.nombre)

    /* TESTING */
    private fun existeLaFormacion(formacion: Formacion) = this.buscarFormacion(formacion) != null

    /*VALIDATIONS*/
    private fun validarSiExisteLaFormacion(formacion: Formacion) {
        if (this.existeLaFormacion(formacion)) throw DuplicateFormationException("Ya existe una formacion con el nombre dado.")
    }

}
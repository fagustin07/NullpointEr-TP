package ar.edu.unq.epers.tactics.persistencia.dao.mongodb

import ar.edu.unq.epers.tactics.exceptions.DuplicateFormationException
import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import com.fasterxml.jackson.databind.ObjectMapper

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
        val clases = ObjectMapper().writer().writeValueAsString(
            party.aventureros().map { it.clases() }.flatten().groupBy { it }.mapValues { it.value.size }
        )

        val mapFunction = """
            function() {
                const clasesQueSeTiene = $clases
        
                const tieneLosRequisitos =
                    Object
                        .keys(this.requerimientos)
                        .map(nombre => ({nombre, cantidad: this.requerimientos[nombre]}))
                        .every(({nombre, cantidad}) => clasesQueSeTiene[nombre] >= cantidad)
        
                if(tieneLosRequisitos) {
                    emit(this.nombre, this)
                }
            }
            """

        val reduceFunction = """
            function(nombre_formacion, formaciones) {
                throw 'Nunca deberia ejecutarse esto. No puede haber nombres de  formaciones repetidos'
            }
        """
        return collection
            .mapReduce(mapFunction, reduceFunction, Map::class.java)
            .map {
                val formacion = it.get("value") as Map<String, String>
                val nombre = formacion.get("nombre") as String
                val requerimientos = (formacion.get("requerimientos") as Map<String, Int>)
                val atributos = (formacion.get("stats") as List<Map<String, Int>>).map { it ->
                    AtributoDeFormacion(
                        it.get("nombreAtributo") as String,
                        it.get("puntosDeGanancia") as Int
                    )
                }

                Formacion(nombre, requerimientos, atributos)
            }
            .toList()

    }

    /*PRIVATE*/

    /*ACTIONS*/
    private fun buscarFormacion(formacion: Formacion) =
        this.getBy("nombre", formacion.nombre)

    /* ACCESSING */
    override fun atributosQueCorresponden(clasesQueSeTiene: List<String>): List<AtributoDeFormacion> {
        // TODO: la conversion de List<String> a Map<String, Int> esta repetida en Formacion
        val clasesQueSeTieneString =
            ObjectMapper().writer().writeValueAsString(
                clasesQueSeTiene
                    .fold(mutableMapOf<String, Int>()) { map, nombreDeClase ->
                        map.put(
                            nombreDeClase,
                            map.getOrElse(nombreDeClase, { 0 }) + 1
                        )
                        map
                    }
            )

        val mapFunction = """
            function() {
                const clasesQueSeTiene = $clasesQueSeTieneString
        
                const cumpleConTodosLosRequisitos =
                    Object
                        .keys(this.requerimientos)
                        .map(nombre => ({nombre, cantidad: this.requerimientos[nombre]}))
                        .every(({nombre, cantidad}) => clasesQueSeTiene[nombre] >= cantidad)
        
                if (cumpleConTodosLosRequisitos)
                    this.stats.forEach(stat => emit(stat.nombreAtributo, {nombreAtributo: stat.nombreAtributo, puntosDeGanancia: stat.puntosDeGanancia}))
            }
            """

        val reduceFunction = """
            function(atributo, stats) {
                return {
                    nombreAtributo: atributo,
                    puntosDeGanancia: Arrays.sum(stats.map(stat => stat.puntosDeGanancia))
                }
            }
        """

        return collection
            .mapReduce(mapFunction, reduceFunction, AtributoDeFormacion::class.java)
            .into(mutableListOf())
    }

    /* TESTING */
    private fun existeLaFormacion(formacion: Formacion) = this.buscarFormacion(formacion) != null

    /*VALIDATIONS*/
    private fun validarSiExisteLaFormacion(formacion: Formacion) {
        if (this.existeLaFormacion(formacion)) throw DuplicateFormationException("Ya existe una formacion con el nombre dado.")
    }

}
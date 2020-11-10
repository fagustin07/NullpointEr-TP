package ar.edu.unq.epers.tactics.persistencia.dao.mongodb

import ar.edu.unq.epers.tactics.exceptions.DuplicateFormationException
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO

class MongoFormacionDAO : MongoDAO<Formacion>(Formacion::class.java), FormacionDAO {

    /*ACTIONS*/
    override fun guardar(formacion: Formacion): Formacion {
        this.validarSiExisteLaFormacion(formacion)

        this.save(formacion)
        return buscarFormacion(formacion)!!
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
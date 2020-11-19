package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Party

interface FormacionDAO {

    fun guardar(formacion: Formacion): Formacion
    fun getAll(): List<Formacion>
    fun deleteAll()

    fun atributosQueCorresponden(clasesDeSusPersonajes: List<String>): List<AtributoDeFormacion>
    fun formacionesQuePosee(party: Party) : List<Formacion>
}
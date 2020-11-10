package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Formacion

interface FormacionDAO {

    fun guardar(formacion: Formacion): Formacion
    fun deleteAll()
}
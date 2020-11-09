package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Formacion

interface FormacionService {

    // Propósito: Crea una nueva formación
    fun crearFormacion(
        nombreFormacion:String,
        requerimientos:List<Clase>,
        stats:List<AtributoDeFormacion>): Formacion

    // Propósito: Devuelve todas las formaciones creadas.
    fun todasLasFormaciones():List<Formacion>

    // Propósito: Dada una party, devuelve la sumatoria de los atributos de formacion que corresponden
    // e.g.: (si mi party cumple con 3 formaciones y esas dan "2 de fuerza", "2 de fuerza y
    // 2 de inteligencia" y "3 de agilidad" se deberia devolver una lista con 4 de fuerza,
    // 2 inteligencia y 3 de agilidad)
    fun atributosQueCorresponden(partyId:Int):List<AtributoDeFormacion>

    // Propósito: Devuelve la lista de formaciones basado en la composicion de la party
    fun formacionesQuePosee(partyId:Int): List<Formacion>
}
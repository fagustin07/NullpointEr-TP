package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Mejora

interface ClaseDAO {
    fun crear(entity: Clase): Clase

    fun requerir(nombreClaseHabilitada: String, nombreClaseRequerida: String)

    fun requeridasDe(claseSucesora: Clase): List<Clase>

    fun requiereEnAlgunNivelDe(claseSucesora: Clase, claseAntecesora: Clase): Boolean

    fun crearMejora(nombreClaseInicio:String,nombreClaseAMejorar:String,atributos:List<String>,valorAAumentar:Int): Mejora

    fun verificarBidireccionalidad(nombreClaseInicio: String,nombreClaseAMejorar: String)

    fun posiblesMejorasTeniendo(nombresDeClasesQueSeTiene: MutableSet<String>): Set<Mejora>

    fun puedeMejorarseTeniendo(clasesQueSeTiene: MutableSet<String>, mejora: Mejora): Boolean
}
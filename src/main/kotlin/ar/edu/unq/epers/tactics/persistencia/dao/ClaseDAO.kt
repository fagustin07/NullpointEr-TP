package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Atributo
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Mejora

interface ClaseDAO {
    fun crear(entity: Clase): Clase

    fun requerir(nombreClaseHabilitada: String, nombreClaseRequerida: String)

    fun requeridasDe(claseSucesora: Clase): List<Clase>

    fun requiereEnAlgunNivelDe(claseSucesora: Clase, claseAntecesora: Clase): Boolean

    fun crearMejora(nombreClaseInicio:String, nombreClaseAMejorar:String, atributos:List<Atributo>, valorAAumentar:Int): Mejora

    fun verificarQueLaClaseDeInicioNoSeaHabilitadaPorClaseAMejorar(nombreClaseInicio: String, nombreClaseAMejorar: String)

    fun posiblesMejorasPara(aventurero: Aventurero): Set<Mejora>

    fun puedeMejorarse(aventurero: Aventurero, mejora: Mejora): Boolean

    fun buscarMejora(nombreDeLaClaseInicio: String, nombreDeLaClaseAMejorar: String): Mejora

    fun caminoMasRentable(puntosDeExperiencia: Int, clases: Set<String>, atributo: Atributo): List<Mejora>
}

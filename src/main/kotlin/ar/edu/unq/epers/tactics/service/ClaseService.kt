package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Atributo
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Mejora

interface ClaseService {
    fun crearClase(nombreDeLaClase: String): Clase
    fun crearMejora(nombreClaseInicio:String,nombreClaseAMejorar:String,atributos:List<Atributo>,valorAAumentar:Int): Mejora
    fun requerir(nombreClasePredecesora: Clase, nombreClaseSucesora: Clase)
    fun puedeMejorar(aventureroID: Long, mejora: Mejora): Boolean
    fun posiblesMejoras(aventureroID: Long): Set<Mejora>
    fun ganarProficiencia(aventureroId:Long, nombreClaseInicio:String,nombreClaseAMejorar:String): Aventurero
}
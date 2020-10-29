package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Mejora

interface ClaseService {
    fun crearClase(nombreDeLaClase: String): Clase
    fun crearMejora(nombreClaseInicio:String,nombreClaseAMejorar:String,atributos:List<String>,valorAAumentar:Int): Mejora
    fun requerir(nombreClasePredecesora: Clase, nombreClaseSucesora: Clase)
}
package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Mejora

interface ClaseDAO : DAO<Clase> {
    override fun crear(entity: Clase): Clase

    override fun actualizar(entity: Clase): Clase

    override fun recuperar(id: Long): Clase

    fun requerir(nombreClaseHabilitada: String, nombreClaseRequerida: String)

    fun requeridasDe(claseSucesora: Clase): List<Clase>

    fun requiereEnAlgunNivelDe(claseSucesora: Clase, claseAntecesora: Clase): Boolean

    fun crearMejora(nombreClaseInicio:String,nombreClaseAMejorar:String,atributos:List<String>,valorAAumentar:Int): Mejora

    fun verificarBidireccionalidad(nombreClaseInicio: String,nombreClaseAMejorar: String)
}
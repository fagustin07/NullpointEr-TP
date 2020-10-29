package ar.edu.unq.epers.tactics.modelo

class Mejora(
    private val nombreDeLaClaseInicio:String,
    private val nombreDeLaClaseAMejorar:String,
    private val atributos: List<String>,
    private val puntosAMejorar:Int
){
    fun nombreDeLaClaseInicio() = nombreDeLaClaseInicio
    fun nombreDeLaClaseAMejorar() = nombreDeLaClaseAMejorar
    fun atributos() = atributos
    fun puntosAMejorar() = puntosAMejorar

}
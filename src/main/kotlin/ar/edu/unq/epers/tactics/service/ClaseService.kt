package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Clase

interface ClaseService {
    fun crearClase(nombreDeLaClase: String): Clase
    fun recuperarTodas(): List<Clase>
}
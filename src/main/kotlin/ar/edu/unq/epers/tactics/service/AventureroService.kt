package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party

interface AventureroService {
    fun actualizar(aventurero: Aventurero):Aventurero
    fun recuperar(idDelAventurero: Long): Aventurero
    fun eliminar(aventurero: Aventurero)
}
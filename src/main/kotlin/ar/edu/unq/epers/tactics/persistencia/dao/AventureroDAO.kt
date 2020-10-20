package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Aventurero

interface AventureroDAO: DAO<Aventurero> {
    fun eliminar(aventurero: Aventurero)
    fun buda(): Aventurero
    fun mejorGuerrero(): Aventurero
    fun mejorMago(): Aventurero
    fun mejorCurandero(): Aventurero
}

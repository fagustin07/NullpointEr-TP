package ar.edu.unq.epers.tactics.modelo

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class Party(val nombre: String) {

    @Id
    @GeneratedValue
    var id: Long? = null
    var numeroDeAventureros = 0

    @OneToMany
    var aventureros:List<Aventurero> = listOf()

    fun agregarUnAventurero() = numeroDeAventureros++

}
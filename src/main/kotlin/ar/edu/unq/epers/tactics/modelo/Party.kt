package ar.edu.unq.epers.tactics.modelo

import javax.persistence.*

@Entity
class Party(val nombre: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var numeroDeAventureros = 0

    @OneToMany
    var aventureros:List<Aventurero> = listOf()

    fun agregarUnAventurero() = numeroDeAventureros++

}
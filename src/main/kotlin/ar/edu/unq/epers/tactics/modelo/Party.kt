package ar.edu.unq.epers.tactics.modelo

import javax.persistence.*

@Entity
class Party(val nombre: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var numeroDeAventureros = 0

    @OneToMany
    var aventureros:  MutableList<Aventurero> = mutableListOf()

    fun agregarUnAventurero(aventurero: Aventurero) {
        numeroDeAventureros++
        aventureros.add(aventurero)
    }

}
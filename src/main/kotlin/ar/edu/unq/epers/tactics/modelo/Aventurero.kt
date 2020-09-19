package ar.edu.unq.epers.tactics.modelo

import javax.persistence.*

@Entity
class Aventurero (
    @ManyToOne
    val party: Party,
    var vida: Int,
    val nombre: String) {

    @Id
    @GeneratedValue
    var id: Long? = null
}
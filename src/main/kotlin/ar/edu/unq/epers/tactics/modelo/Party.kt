package ar.edu.unq.epers.tactics.modelo

import javax.persistence.*

@Entity
class Party(val nombre: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var numeroDeAventureros = 0

    @OneToMany
    var aventureros: MutableList<Aventurero> = mutableListOf()

    fun agregarUnAventurero(aventurero: Aventurero) {
        this.chequearSiPertenece(aventurero)

        if (puedeAgregarAventureros()) {
            this.numeroDeAventureros++
            this.aventureros.add(aventurero)
        } else {
            throw RuntimeException("La party $nombre está completa.")
        }
    }

    private fun chequearSiPertenece(aventurero: Aventurero) {
        if (!this.esLaParty(aventurero.party)) {
            throw RuntimeException("${aventurero.nombre} no pertenece a ${this.nombre}.")
        }
    }

    private fun esLaParty(party: Party) = this.nombre == party.nombre

    private fun puedeAgregarAventureros() = numeroDeAventureros < maximoDeAventureros()

    private fun maximoDeAventureros() = 5

}
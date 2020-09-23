package ar.edu.unq.epers.tactics.modelo

import javax.persistence.*

@Entity
class Party(val nombre: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany (cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var aventureros: MutableList<Aventurero> = mutableListOf()

    private val maximoDeAventureros = 5

    fun numeroDeAventureros() = aventureros.size

    fun agregarUnAventurero(aventurero: Aventurero) {
        this.chequearSiPertenece(aventurero)
        this.chequearSiHayEspacioEnParty()
        this.aventureros.add(aventurero)
    }

    private fun chequearSiHayEspacioEnParty() {
        if (!this.puedeAgregarAventureros()) throw RuntimeException("La party $nombre está completa.")
    }

    private fun chequearSiPertenece(aventurero: Aventurero) {
        if (!this.esLaParty(aventurero.party)) {
            throw RuntimeException("${aventurero.nombre} no pertenece a ${this.nombre}.")
        }
    }

    private fun esLaParty(party: Party) = this.nombre == party.nombre

    private fun puedeAgregarAventureros() = this.numeroDeAventureros() < this.maximoDeAventureros
}
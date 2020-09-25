package ar.edu.unq.epers.tactics.modelo

import javax.persistence.*

@Entity
class Party(val nombre: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    init { if (nombre.isEmpty()) throw RuntimeException("Una party debe tener un nombre") }

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var aventureros: MutableList<Aventurero> = mutableListOf()

    private val maximoDeAventureros = 5

    fun numeroDeAventureros() = aventureros.size


    fun agregarUnAventurero(aventurero: Aventurero) {
        this.validarQueNoPertenzcaAOtraParty(aventurero)
        this.validarQueNoEsteRegistrado(aventurero)
        this.validarQueSeAdmitanNuevosIntegrantes()
        this.aventureros.add(aventurero)
    }

    private fun esLaParty(party: Party) = this.nombre == party.nombre

    private fun puedeAgregarAventureros() = this.numeroDeAventureros() < this.maximoDeAventureros

    /* Assertions */
    private fun validarQueNoPertenzcaAOtraParty(aventurero: Aventurero) {
        if (!this.esLaParty(aventurero.party)) throw RuntimeException("${aventurero.nombre} no pertenece a ${this.nombre}.")
    }

    private fun validarQueNoEsteRegistrado(aventurero: Aventurero) {
        if (aventureros.contains(aventurero)) throw RuntimeException("${aventurero.nombre} ya forma parte de la party ${nombre}.")
    }

    private fun validarQueSeAdmitanNuevosIntegrantes() {
        if (!this.puedeAgregarAventureros()) throw RuntimeException("La party $nombre está completa.")
    }


}
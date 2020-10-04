package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.service.dto.PartyDTO
import javax.persistence.*

@Entity
class Party(private var nombre: String, private var imagenURL: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    init { if (nombre.isEmpty()) throw RuntimeException("Una party debe tener un nombre") }

    @OneToMany(mappedBy="party", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    var aventureros: MutableList<Aventurero> = mutableListOf() // TODO: lo cambie TEMPORALMENTE a public (David) por lo de los enums. Hay que corregirlo despues

    private val maximoDeAventureros = 5

    fun numeroDeAventureros() = aventureros.size


    fun agregarUnAventurero(aventurero: Aventurero) {
        this.validarQueNoPertenzcaAOtraParty(aventurero)
        this.validarQueNoEsteRegistrado(aventurero)
        this.validarQueSeAdmitanNuevosIntegrantes()
        this.aventureros.add(aventurero)
        aventurero.registarseEn(this)
    }

    fun removerA(aventurero: Aventurero) {
        if (!this.esLaParty(aventurero.party)) throw RuntimeException("${aventurero.nombre()} no pertenece a ${this.nombre}.")

        aventureros.remove(aventurero)
        aventurero.salirDeLaParty()
    }

    fun nombre()      = nombre
    fun id()          = id
    fun aventureros() = aventureros
    fun imagenURL()   = imagenURL

    fun darleElId(id : Long?){
        this.id = id
    }

    internal fun actualizarse(partyDTO: PartyDTO) {
        this.nombre = partyDTO.nombre
        this.imagenURL = partyDTO.imagenURL
        this.aventureros = partyDTO.aventureros.map { aventurero -> aventurero.aModelo() }.toMutableList()
    }

    private fun esLaParty(party: Party?) = party != null && this.nombre == party.nombre


    private fun puedeAgregarAventureros() = this.numeroDeAventureros() < this.maximoDeAventureros

    /* Assertions */
    private fun validarQueNoPertenzcaAOtraParty(aventurero: Aventurero) {
        // TODO: aventurero.party != null   parche momentaneo.
        if (aventurero.party != null && !this.esLaParty(aventurero.party)) throw RuntimeException("${aventurero.nombre()} no pertenece a ${this.nombre}.")
    }

    private fun validarQueNoEsteRegistrado(aventurero: Aventurero) {
        if (aventureros.contains(aventurero)) throw RuntimeException("${aventurero.nombre()} ya forma parte de la party ${nombre}.")
    }

    private fun validarQueSeAdmitanNuevosIntegrantes() {
        if (!this.puedeAgregarAventureros()) throw RuntimeException("La party $nombre está completa.")
    }

}
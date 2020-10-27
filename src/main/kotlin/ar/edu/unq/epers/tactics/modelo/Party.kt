package ar.edu.unq.epers.tactics.modelo

import javax.persistence.*

@Entity
class Party(private var nombre: String, private var imagenURL: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    init { if (nombre.isEmpty()) throw RuntimeException("Una party debe tener un nombre") }

    @OneToMany(cascade = [CascadeType.ALL],orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "party_id")
    private var aventureros: MutableList<Aventurero> = mutableListOf()

    var estaEnPelea = false

    fun numeroDeAventureros() = aventureros.size

    fun agregarUnAventurero(aventurero: Aventurero) {
        this.validarQueNoPertenzcaAOtraParty(aventurero)
        this.validarQueNoEsteRegistrado(aventurero)
        this.validarQueSeAdmitanNuevosIntegrantes()
        this.aventureros.add(aventurero)
        aventurero.registarseEn(this)
    }

    fun removerA(aventurero: Aventurero) {
        if (aventurero.party == null || !this.esLaParty(aventurero.party!!)) throw RuntimeException("${aventurero.nombre()} no pertenece a ${this.nombre}.")

        aventureros.remove(aventurero)
        aventurero.salirDeLaParty()
    }

    fun nombre() = nombre
    fun id() = id
    fun aventureros() = aventureros
    fun imagenURL() = imagenURL

    fun darleElId(id: Long?) {
        this.id = id
    }

    fun algunoEstaVivo() = this.aventureros.any { it.estaVivo() }

    fun aliadosDe(aventurero: Aventurero): List<Aventurero> {
        val aliados = aventureros.toMutableList()
        aliados.remove(aventurero)
        return aliados
    }

    internal fun actualizarse(otraParty: Party) {
        this.nombre = otraParty.nombre()
        this.imagenURL = otraParty.imagenURL()
        this.aventureros = otraParty.aventureros()
    }

    private fun esLaParty(party: Party) = (party.id != null && party.id()==this.id)
                                        || this.nombre==party.nombre

    private fun puedeAgregarAventureros() = this.numeroDeAventureros() < this.maximoDeAventureros()

    private fun maximoDeAventureros() = 5

    fun estaEnPelea(): Boolean {
        return this.estaEnPelea
    }

    fun entrarEnPelea() {
        if(this.estaEnPelea) throw RuntimeException("No se puede iniciar una pelea: la party ya esta peleando")
        this.estaEnPelea = true
    }

    fun salirDePelea() {
        this.aventureros.forEach {it.reestablecerse() }
        this.estaEnPelea = false
    }

    private fun subirDeNivelAventureros() {
        aventureros.forEach { it.ganarPelea() }
    }

    fun ganarPelea(){
        subirDeNivelAventureros()
    }

    /* Assertions */
    private fun validarQueNoPertenzcaAOtraParty(aventurero: Aventurero) {
        if (aventurero.party != null && !this.esLaParty(aventurero.party!!)) throw RuntimeException("${aventurero.nombre()} no pertenece a ${this.nombre}.")
    }

    private fun validarQueNoEsteRegistrado(aventurero: Aventurero) {
        if (aventureros.contains(aventurero)) throw RuntimeException("${aventurero.nombre()} ya forma parte de la party ${nombre}.")
    }

    private fun validarQueSeAdmitanNuevosIntegrantes() {
        if (!this.puedeAgregarAventureros()) throw RuntimeException("La party $nombre está completa.")
    }


}
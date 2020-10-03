package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.service.dto.AventureroDTO
import javax.persistence.*
import kotlin.math.max


@Entity(name = "Aventurero")
class Aventurero(
        private var nombre: String,
        private var fuerza: Int = 0,
        private var destreza: Int = 0,
        private var inteligencia: Int = 0,
        private var constitucion: Int = 0) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    @Transient
    private var turnosDefendidos = 0

    @Transient
    private var defensor: Aventurero? = null
    private var vida: Int = 0
    private var mana: Int = 0

    @ManyToOne
    var party: Party? = null

    constructor(nombre: String,
                fuerza: Int = 0,
                destreza: Int = 0,
                inteligencia: Int = 0,
                constitucion: Int = 0,
                party: Party) : this(nombre, fuerza, destreza, inteligencia, constitucion) {

        this.party = party
    }

    init {
        this.recalcularVidaYMana()
    }

    fun id() = id
    fun nombre() = nombre
    fun nivel() = 1

    fun fuerza() = fuerza
    fun destreza() = destreza
    fun constitucion() = constitucion
    fun inteligencia() = inteligencia

    fun vida() = vida
    fun mana() = mana
    fun armadura() = nivel() + constitucion
    fun velocidad() = nivel() + destreza
    fun dañoFisico() = nivel() + fuerza + (destreza / 2)
    fun poderMagico() = nivel() + mana
    fun precisionFisica() = nivel() + fuerza + destreza


    fun recibirAtaqueFisicoSiDebe(dañoFisico: Int, precisionFisica: Int) {
        val claseDeArmadura = this.armadura() + (this.velocidad() / 2)

        if (precisionFisica >= claseDeArmadura) this.recibirDaño(dañoFisico)
    }


    fun recibirAtaqueMagicoSiDebe(tirada: Int, daño: Int) {
        if (tirada >= this.velocidad() / 2) {
            this.recibirDaño(daño)
        }
    }

    fun curar(vidaACurar: Int) {
        this.vida += vidaACurar
    }

    fun meditar() {
        this.mana += this.nivel()
    }

    fun consumirMana() {
        this.mana = max(0, this.mana - 5)
    }

    fun defendidoPor(defensor: Aventurero) {
        this.defensor = defensor
        this.turnosDefendidos = 3
    }

    internal fun darleElId(id: Long?) {
        this.id = id
    }

    private fun recibirDaño(dañoRecibido: Int) {
        if (this.tieneDefensor()) {
            defensor!!.recibirDaño(dañoRecibido / 2)
            this.consumirTurnoDeDefensa()
        } else {
            this.vida = max(0, vida - dañoRecibido)
        }
    }

    private fun consumirTurnoDeDefensa() {
        turnosDefendidos -= 1

        if (turnosDefendidos == 0) {
            defensor = null
        }
    }

    private fun tieneDefensor() = this.defensor != null && this.defensor!!.estaVivo()


    private fun estaVivo() = this.vida > 0

    internal fun actualizarse(aventureroDTO: AventureroDTO) {
        this.inteligencia = aventureroDTO.atributos.inteligencia
        this.destreza = aventureroDTO.atributos.destreza
        this.constitucion = aventureroDTO.atributos.constitucion
        this.fuerza = aventureroDTO.atributos.fuerza
        this.nombre = aventureroDTO.nombre
        this.recalcularVidaYMana()
    }

    private fun recalcularVidaYMana() {
        vida = ((nivel() * 5) + (constitucion * 2) + fuerza)
        mana = nivel() + inteligencia
    }
    
    fun registarseEn(party: Party) {
        this.party = party
    }
}

package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.enums.TipoDeReceptor
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.modelo.habilidades.HabilidadNula
import javax.persistence.*
import kotlin.math.max


@Entity(name = "Aventurero")
class Aventurero(private var nombre: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null
    private var imagenURL: String = ""

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    private var tacticas: MutableList<Tactica> = mutableListOf()

    private var vida: Int = 0
    private var mana: Int = 0
    private var fuerza: Int = 1
        set(nuevoPuntaje) {
            this.validarPuntaje(nuevoPuntaje, "fuerza")
            field = nuevoPuntaje
        }
    private var destreza: Int = 1
        set(nuevoPuntaje) {
            this.validarPuntaje(nuevoPuntaje, "destreza")
            field = nuevoPuntaje
        }

    private var inteligencia: Int = 1
        set(nuevoPuntaje) {
            this.validarPuntaje(nuevoPuntaje, "inteligencia")
            field = nuevoPuntaje
        }

    private var constitucion: Int = 1
        set(nuevoPuntaje) {
            this.validarPuntaje(nuevoPuntaje, "constitucion")
            field = nuevoPuntaje
        }

    @ManyToOne
    var party: Party? = null

    constructor(
        nombre: String, imagenURL: String = "", fuerza: Int = 1,
        destreza: Int = 1, inteligencia: Int = 1, constitucion: Int = 1
    ) : this(nombre) {
        this.imagenURL = imagenURL
        this.inteligencia = inteligencia
        this.destreza = destreza
        this.constitucion = constitucion
        this.fuerza = fuerza
        this.recalcularVidaYMana()
    }

    @OneToOne(fetch = FetchType.EAGER)
    private var defensor: Aventurero? = null

    @OneToOne(fetch = FetchType.EAGER)
    private var aventureroDefendido: Aventurero? = null
    private var turnosDefendido = 0

    init {
        this.recalcularVidaYMana()
    }

    fun id() = id
    fun nombre() = nombre
    fun imagen() = this.imagenURL
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


    fun resolverTurno(enemigos: List<Aventurero>): Habilidad {
        this.tacticas.sortBy { it.prioridad }

        val posiblesReceptores = this.aliados() + enemigos + this
        val nullHability = HabilidadNula(this, this)

        for (tactica in tacticas) {
            val receptor = posiblesReceptores.firstOrNull { receptor -> tactica.puedeAplicarseA(this, receptor) }
            if (receptor != null) {
                return tactica.aplicarseSobre(this, receptor)
            }
        }
        return nullHability
    }

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

    fun defenderA(receptor: Aventurero) {
        if (this.aventureroDefendido == null) {
            this.aventureroDefendido = receptor
            aventureroDefendido!!.defendidoPor(this)
        } else {
            this.aventureroDefendido!!.perderDefensor()
            receptor.defendidoPor(this)
        }
    }

    fun esAliadoDe(otroAventurero: Aventurero) = aliados().contains(otroAventurero)

    fun esEnemigoDe(otroAventurero: Aventurero) = otroAventurero != this && !this.esAliadoDe(otroAventurero)

    fun aliados(): List<Aventurero> {
        if (party == null) return listOf()
        return party!!.aliadosDe(this)
    }

    fun registarseEn(party: Party) {
        this.party = party
    }

    private fun recibirDaño(dañoRecibido: Int) {
        if (this.tieneDefensor()) {
            defensor!!.recibirDaño(dañoRecibido / 2)
            this.consumirTurnoDeDefensa()
        } else {
            this.vida = max(0, vida - dañoRecibido)
        }
    }

    private fun recalcularVidaYMana() {
        vida = ((nivel() * 5) + (constitucion * 2) + fuerza)
        mana = nivel() + inteligencia
    }

    private fun defendidoPor(defensor: Aventurero) {
        this.defensor = defensor
        this.turnosDefendido = 3
    }

    private fun consumirTurnoDeDefensa() {
        turnosDefendido -= 1

        if (turnosDefendido == 0) {
            defensor = null
        }
    }

    private fun perderDefensor() {
        this.defensor = null
    }

    private fun tieneDefensor() = this.defensor != null && this.defensor!!.estaVivo()

    fun estaVivo() = this.vida > 0

    private fun validarPuntaje(nuevoPuntaje: Int, nombreDeAtributo: String) {
        if (nuevoPuntaje > 100) throw  RuntimeException("La $nombreDeAtributo no puede exceder los 100 puntos!")
        if (nuevoPuntaje < 1) throw  RuntimeException("La $nombreDeAtributo no puede ser menor a 1 punto!")
    }

    fun validacionParaDefenderA(receptor: Aventurero) {
        if (this == receptor) throw  RuntimeException("${this.nombre} no puede defenderse a si mismo!")
        if (this.esEnemigoDe(receptor)) throw  RuntimeException("${this.nombre} no puede defender a un enemigo!")
    }

    internal fun actualizarse(otroAventurero: Aventurero) {
        this.inteligencia = otroAventurero.inteligencia()
        this.destreza = otroAventurero.destreza()
        this.constitucion = otroAventurero.constitucion()
        this.fuerza = otroAventurero.fuerza()
        this.nombre = otroAventurero.nombre()
        this.tacticas = otroAventurero.tacticas()
        this.imagenURL = otroAventurero.imagen()
        this.recalcularVidaYMana()
    }

    internal fun darleElId(id: Long?) {
        this.id = id
    }

    internal fun tacticas() = this.tacticas

    fun agregarTactica(nuevaTactica: Tactica) {
        this.tacticas.add(nuevaTactica)
    }

    fun reestablecerse() {
        this.recalcularVidaYMana()
        this.aventureroDefendido = null
        this.defensor = null
    }

    fun estaDefendiendo(): Boolean {
        return this.aventureroDefendido != null
    }

    fun estaSiendoDefendiendo(): Boolean {
        return this.defensor != null
    }

}

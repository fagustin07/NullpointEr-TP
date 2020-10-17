package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.modelo.habilidades.HabilidadNula
import javax.persistence.*
import kotlin.math.max
import kotlin.math.min


@Entity(name = "Aventurero")
class Aventurero(private var nombre: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null
    private var imagenURL: String = ""

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "aventurero_id")
    private var tacticas: MutableList<Tactica> = mutableListOf()

    private var vida: Double = 0.0
    private var mana: Double = 0.0
    private var fuerza: Double = 1.0
        set(nuevoPuntaje) {
            this.validarPuntaje(nuevoPuntaje, "fuerza")
            field = nuevoPuntaje
        }
    private var destreza: Double = 1.0
        set(nuevoPuntaje) {
            this.validarPuntaje(nuevoPuntaje, "destreza")
            field = nuevoPuntaje
        }

    private var inteligencia: Double = 1.0
        set(nuevoPuntaje) {
            this.validarPuntaje(nuevoPuntaje, "inteligencia")
            field = nuevoPuntaje
        }

    private var constitucion: Double = 1.0
        set(nuevoPuntaje) {
            this.validarPuntaje(nuevoPuntaje, "constitucion")
            field = nuevoPuntaje
        }
    private var dañoRecibido = 0.0

    @ManyToOne
    var party: Party? = null

    constructor(
        nombre: String, imagenURL: String = "", fuerza: Double = 1.0,
        destreza: Double = 1.0, inteligencia: Double = 1.0, constitucion: Double = 1.0
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

    /** ACCESSING **/
    fun id() = id
    fun nombre() = nombre
    fun imagenURL() = this.imagenURL
    fun nivel() = 1

    fun fuerza() = fuerza
    fun destreza() = destreza
    fun constitucion() = constitucion
    fun inteligencia() = inteligencia

    fun vidaActual() = vida-dañoRecibido
    fun mana() = mana
    fun armadura() = nivel() + constitucion
    fun velocidad() = nivel() + destreza
    fun dañoFisico() = nivel() + fuerza + (destreza / 2)
    fun poderMagico() = nivel() + mana
    fun precisionFisica() = nivel() + fuerza + destreza
    fun dañoRecibido() = this.dañoRecibido

    fun aliados(): List<Aventurero> {
        if (party == null) return listOf()
        return party!!.aliadosDe(this)
    }

    fun tacticas() = this.tacticas

    /** TESTING **/
    fun esAliadoDe(otroAventurero: Aventurero) = aliados().contains(otroAventurero)

    fun esEnemigoDe(otroAventurero: Aventurero) = otroAventurero != this && !this.esAliadoDe(otroAventurero)

    fun estaVivo() = this.vidaActual() > 0.0

    fun estaDefendiendo() = this.aventureroDefendido != null

    fun estaSiendoDefendiendo() = this.defensor != null

    /** ACTIONS **/
    fun resolverTurno(enemigos: List<Aventurero>): Habilidad {
//        validarSiEstaVivo() TODO: si dejamos esto, explota el front. En el FRONT le piden resolver turno a aventureros muertos

        this.tacticas.sortBy { it.prioridad }

        val posiblesReceptores = this.aliados() + enemigos + this

        for (tactica in tacticas) {
            val receptor = posiblesReceptores.firstOrNull { receptor -> tactica.puedeAplicarseA(this, receptor) }
            if (receptor != null) {
                return tactica.aplicarseSobre(this, receptor)
            }
        }
        return HabilidadNula.para(this,this)
    }

    fun recibirAtaqueFisicoSiDebe(dañoFisico: Double, precisionFisica: Double) {
        val claseDeArmadura = this.armadura() + (this.velocidad() / 2)

        if (precisionFisica >= claseDeArmadura) this.recibirDaño(dañoFisico)
    }

    fun recibirAtaqueMagicoSiDebe(tirada: Int, daño: Double) {
        if (tirada >= this.velocidad() / 2) {
            this.recibirDaño(daño)
        }
    }

    fun curar(vidaACurar: Double) {
        this.dañoRecibido = max(0.0,dañoRecibido - vidaACurar)
    }

    fun meditar() {
        this.mana += this.nivel()
    }

    fun consumirMana() {
        this.mana = max(0.0, this.mana - 5)
    }

    fun defenderA(receptor: Aventurero) {
        aventureroDefendido?.perderDefensor()
        aventureroDefendido = receptor
        receptor.defendidoPor(this)
    }

    fun registarseEn(party: Party) {
        this.party = party
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
        this.imagenURL = otroAventurero.imagenURL()
        this.dañoRecibido = otroAventurero.dañoRecibido
        this.recalcularVidaYMana()
    }

    internal fun darleElId(id: Long?) {
        this.id = id
    }

    fun agregarTactica(nuevaTactica: Tactica) {
        this.tacticas.add(nuevaTactica)
    }

    fun reestablecerse() {
        this.dañoRecibido = 0.0
        this.turnosDefendido = 0
        this.aventureroDefendido = null
        this.defensor = null
        this.recalcularVidaYMana()
    }

    fun salirDeLaParty() {
        this.party = null
    }

    fun actualizarDañoRecibido(nuevoDañoRecibido: Double) {
        this.dañoRecibido = nuevoDañoRecibido
    }

    /*** PRIVATE ***/
    /** TESTING **/
    private fun tieneDefensor() = this.defensor != null && this.defensor!!.estaVivo()

    /** ACTIONS **/
    private fun recibirDaño(dañoAAplicar: Double) {
        if (this.tieneDefensor()) {
            defensor!!.recibirDaño(dañoAAplicar / 2)
            this.consumirTurnoDeDefensa()
        } else {
            this.dañoRecibido = min(this.dañoRecibido+dañoAAplicar, vida)
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

    private fun validarPuntaje(nuevoPuntaje: Double, nombreDeAtributo: String) {
        if (nuevoPuntaje > 100) throw  RuntimeException("La $nombreDeAtributo no puede exceder los 100 puntos!")
        if (nuevoPuntaje < 1) throw  RuntimeException("La $nombreDeAtributo no puede ser menor a 1 punto!")
    }

//    private fun validarSiEstaVivo() {
//        if (!this.estaVivo()) throw RuntimeException("Un aventurero muerto no puede resolver su turno");
//    }


}

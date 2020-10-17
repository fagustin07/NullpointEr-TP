package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import java.lang.RuntimeException
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Pelea(@OneToOne(fetch = FetchType.EAGER) val party: Party, private val nombrePartyEnemiga: String) {

    private val fecha = LocalDateTime.now()
    private var estaFinalizada = false
    private var estaGanada = false

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(name="habilidadesEmitidas")
    private var habilidadesEmitidas: MutableList<Habilidad> = mutableListOf()

    init { habilidadesEmitidas = mutableListOf() }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    fun id() = this.id

    fun fecha() = this.fecha

    fun idDeLaParty() = party.id()!!

    fun nombrePartyEnemiga() = this.nombrePartyEnemiga

    fun finalizar() {
        if (estaFinalizada) throw RuntimeException("La pelea ya ha terminado antes.")
        estaFinalizada = true
        this.estaGanada = party.algunoEstaVivo()
        party.salirDePelea()
    }

    fun registrarEmisionDe(habilidadEmitida: Habilidad) {
        habilidadesEmitidas.add(habilidadEmitida)
    }

    fun estaGanada() = this.estaGanada

}
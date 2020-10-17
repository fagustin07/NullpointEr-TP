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
    @JoinColumn(name = "pelea_en_que_fue_emitida_id")
    private var habilidadesEmitidas: MutableList<Habilidad> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(name="habilidadesRecibidas")
    @JoinColumn(name = "pelea_en_que_fue_recibida_id")
    private var habilidadesRecibidas: MutableList<Habilidad> = mutableListOf()

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

    fun registrarRecepcionDe(habilidadRecibida: Habilidad) {
        habilidadesRecibidas.add(habilidadRecibida)
    }
    fun estaGanada() = this.estaGanada

}
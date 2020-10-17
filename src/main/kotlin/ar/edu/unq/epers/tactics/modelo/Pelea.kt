package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import java.lang.RuntimeException
import java.time.LocalDateTime
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
class Pelea(@OneToOne(fetch = FetchType.EAGER) val party: Party) {

    private val fecha = LocalDateTime.now()
    private var estaFinalizada = false

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(name="habilidadesEmitidas")
    @JoinColumn(name = "pelea_id")
    private var habilidadesEmitidas: MutableList<Habilidad> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(name="habilidadesRecibidas")
    @JoinColumn(name = "pelea_id")
    private var habilidadesRecibidas: MutableList<Habilidad> = mutableListOf()

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    fun id() = this.id

    fun fecha() = this.fecha

    fun idDeLaParty() = party.id()!!

    fun finalizar() {
        if (estaFinalizada) throw RuntimeException("La pelea ya ha terminado antes.")
        estaFinalizada = true
        party.salirDePelea()
    }

    fun registrarEmisionDe(habilidadEmitida: Habilidad) {
        habilidadesEmitidas.add(habilidadEmitida)
    }

    fun registrarRecepcionDe(habilidadRecibida: Habilidad) {
        habilidadesRecibidas.add(habilidadRecibida)
    }

}
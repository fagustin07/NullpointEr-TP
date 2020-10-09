package ar.edu.unq.epers.tactics.modelo

import java.lang.RuntimeException
import java.time.LocalDateTime
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
class Pelea(
    @OneToOne(fetch = FetchType.EAGER)
    val party: Party
    ) {

    private val fecha = LocalDateTime.now()
    private var estaFinalizada = false

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

}
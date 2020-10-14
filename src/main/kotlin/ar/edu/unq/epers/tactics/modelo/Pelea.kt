package ar.edu.unq.epers.tactics.modelo

import java.lang.RuntimeException
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Pelea(@OneToOne(fetch = FetchType.EAGER) val party: Party, private val partyEnemiga: String) {

    private val fecha = LocalDateTime.now()
    private var estaFinalizada = false
    private var estaGanada = false

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    fun id() = this.id

    fun fecha() = this.fecha

    fun idDeLaParty() = party.id()!!

    fun partyEnemiga() = this.partyEnemiga

    fun finalizar() {
        if (estaFinalizada) throw RuntimeException("La pelea ya ha terminado antes.")
        estaFinalizada = true
        this.estaGanada = party.algunoEstaVivo()
        party.salirDePelea()
    }

    fun estaGanada() = this.estaGanada

}
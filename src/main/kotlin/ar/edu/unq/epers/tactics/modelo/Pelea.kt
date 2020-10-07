package ar.edu.unq.epers.tactics.modelo

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Pelea(private val idDeLaParty: Long) {

    private val fecha = LocalDateTime.now()

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    fun id() = this.id

    fun fecha() = this.fecha

    fun idDeLaParty() = this.idDeLaParty
}
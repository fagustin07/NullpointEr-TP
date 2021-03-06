package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import javax.persistence.*

@Entity
@Inheritance(strategy= InheritanceType.JOINED)
abstract class Habilidad(
    @OneToOne(fetch=FetchType.EAGER)
    val aventureroEmisor: Aventurero?,

    @OneToOne(fetch=FetchType.EAGER)
    val aventureroReceptor: Aventurero
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    open val esAtaqueMagico = false
    open val esMeditacion = false
    open val esCuracion = false

    abstract fun resolversePara(receptor: Aventurero)

}
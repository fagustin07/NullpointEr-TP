package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import javax.persistence.*

@Entity
@Inheritance(strategy= InheritanceType.JOINED)
abstract class Habilidad(
    @ManyToOne(fetch = FetchType.LAZY) val aventureroEmisor: Aventurero?,
    @ManyToOne(fetch = FetchType.LAZY) val aventureroReceptor: Aventurero
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    abstract fun resolversePara(receptor: Aventurero)

}
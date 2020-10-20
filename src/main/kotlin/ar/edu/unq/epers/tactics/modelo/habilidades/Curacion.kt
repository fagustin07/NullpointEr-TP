package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import javax.persistence.Entity
import javax.persistence.PrimaryKeyJoinColumn

@Entity
@PrimaryKeyJoinColumn(name="id")
class Curacion(
    val poderMagicoEmisor: Double,
    aventureroEmisor: Aventurero?,
    aventureroReceptor: Aventurero
) : Habilidad(aventureroEmisor, aventureroReceptor) {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): Curacion {
            val curacionCreada = Curacion(aventureroEmisor.poderMagico(), aventureroEmisor, aventureroReceptor)
            aventureroEmisor.consumirMana()
            return curacionCreada
        }
    }
    override val esCuracion = true

    override fun resolversePara(receptor: Aventurero) = receptor.curar(poderMagicoEmisor)

}

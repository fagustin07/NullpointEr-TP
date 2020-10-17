package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import javax.persistence.Entity
import javax.persistence.PrimaryKeyJoinColumn

@Entity
@PrimaryKeyJoinColumn(name="id")
class Defensa(
    aventureroEmisor: Aventurero?,
    aventureroReceptor: Aventurero
): Habilidad(
    aventureroEmisor,
    aventureroReceptor
) {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): Defensa {
            aventureroEmisor.validacionParaDefenderA(aventureroReceptor)
            return Defensa(aventureroEmisor, aventureroReceptor)
        }
    }

    override fun resolversePara(receptor: Aventurero) = aventureroEmisor!!.defenderA(receptor)

}

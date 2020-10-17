package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.dado.Dado
import javax.persistence.Entity
import javax.persistence.PrimaryKeyJoinColumn

@Entity
@PrimaryKeyJoinColumn(name="id")
class Ataque(
    val dañoFisico: Double,
    val precisionFisica: Double,
    aventureroEmisor: Aventurero?,
    aventureroReceptor: Aventurero,
    @Transient val dado: Dado
) : Habilidad(
    aventureroEmisor,
    aventureroReceptor
) {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero, dado: Dado): Ataque {
            return Ataque(
                aventureroEmisor.dañoFisico(),
                aventureroEmisor.precisionFisica(),
                aventureroEmisor,
                aventureroReceptor,
                dado
            )
        }
    }

    override fun resolversePara(receptor: Aventurero) {
        receptor.recibirAtaqueFisicoSiDebe(dañoFisico, dado.tirada() + precisionFisica)
    }

}
